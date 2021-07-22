package com.infamous.captain_america.common.entity.projectile;

import com.infamous.captain_america.common.registry.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

public class TimedGrenadeEntity extends ExplosiveProjectileEntity{
    private static final EntityDataAccessor<Integer> DATA_EXPLOSION_TIMER = SynchedEntityData.defineId(TimedGrenadeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_MAX_EXPLOSION_TIMER = SynchedEntityData.defineId(TimedGrenadeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<OptionalInt> DATA_ATTACHED_TO_TARGET_ID = SynchedEntityData.defineId(TimedGrenadeEntity.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
    private static final EntityDataAccessor<Optional<UUID>> DATA_ATTACHED_TO_TARGET_UUID = SynchedEntityData.defineId(TimedGrenadeEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Optional<BlockPos>> DATA_ATTACH_POS_ID = SynchedEntityData.defineId(TimedGrenadeEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);

    private Entity attachedToEntity;

    public TimedGrenadeEntity(EntityType<? extends TimedGrenadeEntity> entityType, Level world) {
        super(entityType, world);
    }

    public TimedGrenadeEntity(LivingEntity shooter, Level world) {
        super(EntityTypeRegistry.TIMED_GRENADE.get(), shooter, world);
    }

    protected void setAttachedToEntity(Entity entity){
        this.setAttachedToEntityUUIDRaw(entity.getUUID());
        this.setAttachedToEntityIdRaw(entity.getId());
        this.attachedToEntity = entity;
    }

    protected Optional<UUID> getAttachedToEntityUUIDRaw() {
        return this.entityData.get(DATA_ATTACHED_TO_TARGET_UUID);
    }

    protected OptionalInt getAttachedToEntityIdRaw() {
        return this.entityData.get(DATA_ATTACHED_TO_TARGET_ID);
    }

    protected void setAttachedToEntityUUIDRaw(@Nullable UUID uuid) {
        this.entityData.set(DATA_ATTACHED_TO_TARGET_UUID, Optional.ofNullable(uuid));
    }

    protected void setAttachedToEntityIdRaw(int id) {
        this.entityData.set(DATA_ATTACHED_TO_TARGET_ID, OptionalInt.of(id));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ATTACHED_TO_TARGET_UUID, Optional.empty());
        this.entityData.define(DATA_ATTACHED_TO_TARGET_ID, OptionalInt.empty());
        this.entityData.define(DATA_ATTACH_POS_ID, Optional.empty());
        this.entityData.define(DATA_EXPLOSION_TIMER, 0);
        this.entityData.define(DATA_MAX_EXPLOSION_TIMER, 40);
    }

    @Override
    protected boolean preBaseProjectileTick() {
        boolean isAttachedToEntity = this.getAttachedToEntityUUIDRaw().isPresent();
        boolean isAttachedToBlockPos = this.getAttachedBlockPos().isPresent();
        if(isAttachedToEntity || isAttachedToBlockPos){
            this.tickExplosionTimer();
            this.tickAttachment();
        }
        return !isAttachedToEntity && !isAttachedToBlockPos;
    }

    @Override
    protected void postHitBlock(BlockHitResult blockRTR) {
        if(!this.level.isClientSide){
            this.setAttachedBlockPos(blockRTR.getBlockPos());
            this.setExplosionTimer(this.getMaxExplosionTimer());
        }
    }

    @Override
    protected void postHitEntity(EntityHitResult entityRTR) {
        if(!this.level.isClientSide){
            this.setAttachedToEntity(entityRTR.getEntity());
            this.setExplosionTimer(this.getMaxExplosionTimer());
        }
    }

    /**
     * This automatically syncs the attachedToEntity field on both sides.
     * First, the UUID is checked for presence.
     * If it is present and the attachedToEntity is null on the server,
     * then the entity is obtained server-side and cached to attachedToEntity,
     * and the entity's network id is saved to the data parameter.
     * The same UUID will also be present on the client,
     * so the entity will be obtained client-side by the network id
     * and then cached to attachedToEntity.
     */
    protected void tickAttachment(){
        if (this.getAttachedToEntityUUIDRaw().isPresent() && !this.getAttachedBlockPos().isPresent()) {
            if (this.attachedToEntity == null) {
                if(!this.level.isClientSide){
                    this.getAttachedToEntityUUIDRaw().ifPresent((uuid) -> {
                        if(this.level instanceof ServerLevel){
                            ServerLevel serverWorld = (ServerLevel) this.level;
                            Entity entity = serverWorld.getEntity(uuid);
                            if (entity != null) {
                                this.attachedToEntity = entity;
                                this.setAttachedToEntityIdRaw(entity.getId());
                            }
                        }
                    });
                } else{
                    this.getAttachedToEntityIdRaw().ifPresent((id) -> {
                        Entity entity = this.level.getEntity(id);
                        if (entity != null) {
                            this.attachedToEntity = entity;
                        }
                    });
                }
            }

            if (this.attachedToEntity != null && this.getAttachedToEntityIdRaw().isPresent()) {
                Entity attachedToEntity = this.attachedToEntity;
                this.setPos(attachedToEntity.getX(0.5D), attachedToEntity.getY(0.5D), attachedToEntity.getZ(0.5D));
                this.setDeltaMovement(this.attachedToEntity.getDeltaMovement());
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        if (this.getAttachedToEntityUUIDRaw().isPresent()) {
            compoundNBT.putUUID("AttachedToEntity", this.getAttachedToEntityUUIDRaw().get());
        }
        Optional<BlockPos> optionalBlockPos = this.getAttachedBlockPos();
        if (optionalBlockPos.isPresent()) {
            BlockPos blockPos = optionalBlockPos.get();
            compoundNBT.putInt("APX", blockPos.getX());
            compoundNBT.putInt("APY", blockPos.getY());
            compoundNBT.putInt("APZ", blockPos.getZ());
        }
        compoundNBT.putInt("ExplosionTimer", this.getExplosionTimer());
        compoundNBT.putInt("MaxExplosionTimer", this.getMaxExplosionTimer());

    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        UUID uuid = null;
        if (compoundNBT.hasUUID("AttachedToEntity")) {
            uuid = compoundNBT.getUUID("AttachedToEntity");
        }
        if (compoundNBT.contains("APX")) {
            int x = compoundNBT.getInt("APX");
            int y = compoundNBT.getInt("APY");
            int z = compoundNBT.getInt("APZ");
            this.setAttachedBlockPos(new BlockPos(x, y, z));
        } else {
            this.setAttachedBlockPos(null);
        }
        this.setAttachedToEntityUUIDRaw(uuid);
        this.setExplosionTimer(compoundNBT.getInt("ExplosionTimer"));
        this.setMaxExplosionTimer(compoundNBT.getInt("MaxExplosionTimer"));
    }

    protected Optional<BlockPos> getAttachedBlockPos(){
        return this.entityData.get(DATA_ATTACH_POS_ID);
    }

    private void setAttachedBlockPos(@Nullable BlockPos blockPos) {
        this.entityData.set(DATA_ATTACH_POS_ID, Optional.ofNullable(blockPos));
    }

    protected void tickExplosionTimer() {
        if(!this.level.isClientSide){
            int explosionTimer = this.getExplosionTimer();
            if(explosionTimer > 0){
                this.setExplosionTimer(explosionTimer - 1);
            } else if(explosionTimer < 0){
                this.setExplosionTimer(0);
            } else{
                this.explode();
            }
        }
    }

    protected void setExplosionTimer(int explosionTimer) {
        this.entityData.set(DATA_EXPLOSION_TIMER, explosionTimer);
    }

    protected int getExplosionTimer() {
        return this.entityData.get(DATA_EXPLOSION_TIMER);
    }

    protected void setMaxExplosionTimer(int maxExplosionTimer) {
        this.entityData.set(DATA_MAX_EXPLOSION_TIMER, maxExplosionTimer);
    }

    protected int getMaxExplosionTimer() {
        return this.entityData.get(DATA_MAX_EXPLOSION_TIMER);
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }
}
