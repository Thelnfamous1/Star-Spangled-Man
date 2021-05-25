package com.infamous.captain_america.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class VibraniumShieldEntity extends ThrowableEntity {

    private static final DataParameter<Boolean> ID_FOIL = EntityDataManager.defineId(VibraniumShieldEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<ItemStack> DATA_SHIELD_ITEM = EntityDataManager.defineId(VibraniumShieldEntity.class, DataSerializers.ITEM_STACK);

    private int life;
    private double baseDamage = 2.0D;
    public VibraniumShieldEntity.PickupStatus pickup = VibraniumShieldEntity.PickupStatus.DISALLOWED;

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

    private void ricochetOffBlock(BlockRayTraceResult blockRayTraceResult) {
        this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 1.0F);

        Vector3d deltaMovement = this.getDeltaMovement();
        switch (blockRayTraceResult.getDirection()) {
            case UP:
            case DOWN: {
                this.setDeltaMovement(deltaMovement.x, -deltaMovement.y, deltaMovement.z);
                break;
            }
            case EAST:
            case WEST: {
                this.setDeltaMovement(-deltaMovement.x, deltaMovement.y, deltaMovement.z);
                break;
            }
            case NORTH:
            case SOUTH: {
                this.setDeltaMovement(deltaMovement.x, deltaMovement.y, -deltaMovement.z);
                break;
            }
        }

        this.checkNotMoving();
    }

    private void ricochet() {
        this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 1.0F);
        this.setDeltaMovement(this.getDeltaMovement().scale(-1.0D));
        this.yRot += 180.0F;
        this.yRotO += 180.0F;

        this.checkNotMoving();
    }

    private void checkNotMoving() {
        if (!this.level.isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
            if (this.pickup == PickupStatus.ALLOWED) {
                this.spawnAtLocation(this.getShieldStack(), 0.1F);
            }
            this.remove();
        }
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult entityRayTraceResult) {
        super.onHitEntity(entityRayTraceResult);
        Entity entity = entityRayTraceResult.getEntity();
        if(entity != this.getOwner()){
            float deltaMovementLength = (float)this.getDeltaMovement().length();
            int damage = MathHelper.ceil(MathHelper.clamp((double)deltaMovementLength * this.getBaseDamage(), 0.0D, 2.147483647E9D));
            entity.hurt(DamageSource.thrown(this, this.getOwner()), (float)damage);
            this.ricochet();
        }
    }

    public void setBaseDamage(double baseDamage) {
        this.baseDamage = baseDamage;
    }

    public double getBaseDamage() {
        return this.baseDamage;
    }

    @Override
    public void playerTouch(PlayerEntity player) {
        boolean leftOwner = this.getLeftOwner();
        if (!this.level.isClientSide && leftOwner) {
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

    private Boolean getLeftOwner() {
        return ObfuscationReflectionHelper.getPrivateValue(ProjectileEntity.class, this, "field_234611_d_");
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
        if (compoundNBT.contains("damage", 99)) {
            this.baseDamage = compoundNBT.getDouble("damage");
        }
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
        compoundNBT.putDouble("damage", this.baseDamage);
    }

    @Override
    public void setOwner(@Nullable Entity p_212361_1_) {
        super.setOwner(p_212361_1_);
        if (p_212361_1_ instanceof PlayerEntity) {
            this.pickup = ((PlayerEntity)p_212361_1_).abilities.instabuild ? VibraniumShieldEntity.PickupStatus.CREATIVE_ONLY : VibraniumShieldEntity.PickupStatus.ALLOWED;
        }
    }

    public enum PickupStatus {
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
