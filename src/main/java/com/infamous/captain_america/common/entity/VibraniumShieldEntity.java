package com.infamous.captain_america.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class VibraniumShieldEntity extends ThrowableEntity {
    public VibraniumShieldEntity.PickupStatus pickup = VibraniumShieldEntity.PickupStatus.DISALLOWED;

    private static final DataParameter<Boolean> ID_FOIL = EntityDataManager.defineId(VibraniumShieldEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<ItemStack> DATA_SHIELD_ITEM = EntityDataManager.defineId(VibraniumShieldEntity.class, DataSerializers.ITEM_STACK);
    private int life;

    public VibraniumShieldEntity(EntityType<? extends VibraniumShieldEntity> entityType, World world){
        super(entityType, world);
    }

    public VibraniumShieldEntity(EntityType<? extends VibraniumShieldEntity> entityType, LivingEntity thrower, World world) {
        super(entityType, thrower, world);
        if (thrower instanceof PlayerEntity) {
            this.pickup = VibraniumShieldEntity.PickupStatus.ALLOWED;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.level.isClientSide){
            this.tickDespawn();
        }
    }

    protected void tickDespawn() {
        ++this.life;
        if (this.life >= 1200) {
            this.spawnAtLocation(this.getShieldStack(), 0.1F);
            this.remove();
        }
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult blockRayTraceResult) {
        super.onHitBlock(blockRayTraceResult);
        this.ricochet();
    }

    private void ricochet() {
        this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 1.0F);
        this.setDeltaMovement(this.getDeltaMovement().scale(-1.0D));
        this.yRot += 180.0F;
        this.yRotO += 180.0F;
        if (!this.level.isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
            if (this.pickup == VibraniumShieldEntity.PickupStatus.ALLOWED) {
                this.spawnAtLocation(this.getShieldStack(), 0.1F);
            }
            this.remove();
        }
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult entityRayTraceResult) {
        super.onHitEntity(entityRayTraceResult);
        Entity entity = entityRayTraceResult.getEntity();
        int damage = 5;
        entity.hurt(DamageSource.thrown(this, this.getOwner()), (float)damage);
        this.ricochet();
    }

    @Override
    public void playerTouch(PlayerEntity player) {
        if (!this.level.isClientSide && this.life > 1) {
            boolean canPickUp = this.pickup == PickupStatus.ALLOWED
                    || this.pickup == PickupStatus.CREATIVE_ONLY
                    && player.abilities.instabuild;
            if (this.pickup == PickupStatus.ALLOWED && !player.inventory.add(this.getShieldStack())) {
                canPickUp = false;
            }

            if (canPickUp) {
                this.remove();
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_SHIELD_ITEM, ItemStack.EMPTY);
        this.entityData.define(ID_FOIL, false);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public ItemStack getShieldStack() {
        return this.entityData.get(DATA_SHIELD_ITEM);
    }

    public void setShieldStack(ItemStack shieldStack) {
        this.entityData.set(DATA_SHIELD_ITEM, shieldStack);
        this.entityData.set(ID_FOIL, shieldStack.hasFoil());
    }

    public boolean isFoil() {
        return this.entityData.get(ID_FOIL);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        ItemStack itemstack = ItemStack.of(compoundNBT.getCompound("ShieldItem"));
        if (!itemstack.isEmpty()) {
            this.entityData.set(DATA_SHIELD_ITEM, itemstack);
        }
        if (compoundNBT.contains("pickup", 99)) {
            this.pickup = VibraniumShieldEntity.PickupStatus.byOrdinal(compoundNBT.getByte("pickup"));
        } else if (compoundNBT.contains("player", 99)) {
            this.pickup = compoundNBT.getBoolean("player") ? VibraniumShieldEntity.PickupStatus.ALLOWED : VibraniumShieldEntity.PickupStatus.DISALLOWED;
        }
        this.life = compoundNBT.getShort("life");
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        ItemStack itemstack = this.entityData.get(DATA_SHIELD_ITEM);
        if (!itemstack.isEmpty()) {
            compoundNBT.put("ShieldItem", itemstack.save(new CompoundNBT()));
        }
        compoundNBT.putByte("pickup", (byte)this.pickup.ordinal());
        compoundNBT.putShort("life", (short)this.life);
    }

    public static enum PickupStatus {
        DISALLOWED,
        ALLOWED,
        CREATIVE_ONLY;

        public static VibraniumShieldEntity.PickupStatus byOrdinal(int ordinal) {
            if (ordinal < 0 || ordinal > values().length) {
                ordinal = 0;
            }

            return values()[ordinal];
        }
    }
}
