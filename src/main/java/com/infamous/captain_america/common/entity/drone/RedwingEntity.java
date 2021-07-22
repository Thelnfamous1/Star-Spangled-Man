package com.infamous.captain_america.common.entity.drone;

import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.drone_controller.IDroneController;
import com.infamous.captain_america.common.entity.projectile.BulletEntity;
import com.infamous.captain_america.common.util.CALogicHelper;
import com.infamous.captain_america.server.ai.goals.*;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.RangedAttackMob;

public class RedwingEntity extends PathfinderMob implements FlyingAnimal, RangedAttackMob, IAttachableDrone, IVisualLinker {
    protected static final EntityDataAccessor<Boolean> DATA_OWNED = SynchedEntityData.defineId(RedwingEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(RedwingEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Boolean> DATA_VISUAL_LINK = SynchedEntityData.defineId(RedwingEntity.class, EntityDataSerializers.BOOLEAN);
    public static final double AI_SPEED_MODIFIER = 3.0D;
    private boolean patrolling;
    private boolean recalled;

    public float flap;
    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    private float flapping = 1.0F;
    private float nextFlap = 1.0F;

    public RedwingEntity(EntityType<? extends RedwingEntity> type, Level world) {
        super(type, world);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ARMOR, 20.0D)
                .add(Attributes.FLYING_SPEED, (double)1.0F)
                .add(Attributes.MOVEMENT_SPEED, (double)0.3F)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new AttachableDroneFollowOwnerGoal<>(this, AI_SPEED_MODIFIER, 10.0F, 2.0F, 12.0F, true));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, AI_SPEED_MODIFIER, 5, 10.0F));
        this.goalSelector.addGoal(3, new FlyingDroneWanderGoal<>(this, AI_SPEED_MODIFIER));
        this.goalSelector.addGoal(4, new FlyingDronePatrollingGoal<>(this, AI_SPEED_MODIFIER));

        this.targetSelector.addGoal(1, new DroneOwnerHurtByTargetGoal<>(this));
        this.targetSelector.addGoal(2, new DroneOwnerHurtTargetGoal<>(this));
        this.targetSelector.addGoal(3, (new DroneHurtByTargetGoal<>(this)).setAlertOthers());
        this.targetSelector.addGoal(4, new DronePatrollingTargetGoal<>(this, Mob.class, false, (living) -> living instanceof Enemy));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.calculateFlapping();
    }

    private void calculateFlapping() {
        this.oFlap = this.flap;
        this.oFlapSpeed = this.flapSpeed;
        this.flapSpeed = (float)((double)this.flapSpeed + (double)(!this.onGround && !this.isPassenger() ? 4 : -1) * 0.3D);
        this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
        if (!this.onGround && this.flapping < 1.0F) {
            this.flapping = 1.0F;
        }

        this.flapping = (float)((double)this.flapping * 0.9D);
        Vec3 vec3 = this.getDeltaMovement();
        if (!this.onGround && vec3.y < 0.0D) {
            this.setDeltaMovement(vec3.multiply(1.0D, 0.6D, 1.0D));
        }

        this.flap += this.flapping * 2.0F;
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, world) {
            public boolean isStableDestination(BlockPos pos) {
                return !this.level.isEmptyBlock(pos.below());
            }
        };
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(true);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_OWNED, false);
        entityData.define(DATA_OWNER_UUID, Optional.empty());
        entityData.define(DATA_VISUAL_LINK, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        this.addDroneNBT(compoundNBT);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        this.readDroneNBT(compoundNBT);
    }


    @Override
    public boolean canAttack(LivingEntity target) {
        return !this.isOwnedBy(target) && super.canAttack(target);
    }

    @Override
    public Team getTeam() {
        if (this.isOwned()) {
            LivingEntity livingentity = this.getOwner();
            if (livingentity != null) {
                return livingentity.getTeam();
            }
        }

        return super.getTeam();
    }

    @Override
    public boolean isAlliedTo(Entity target) {
        if (this.isOwned()) {
            LivingEntity owner = this.getOwner();
            if (target == owner) {
                return true;
            }

            if (owner != null) {
                return owner.isAlliedTo(target);
            }
        }

        return super.isAlliedTo(target);
    }

    @Override
    public void die(DamageSource damageSource) {
        if (!this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getOwner() instanceof ServerPlayer) {
            this.getOwner().sendMessage(this.getCombatTracker().getDeathMessage(), Util.NIL_UUID);
        }

        super.die(damageSource);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader worldReader) {
        return worldReader.isEmptyBlock(pos) ? 10.0F : 0.0F;
    }

    @Override
    public boolean causeFallDamage(float p_148989_, float p_148990_, DamageSource p_148991_) {
        return false;
    }

    @Override
    protected void checkFallDamage(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
    }

    @Override
    public void performRangedAttack(LivingEntity target, float p_82196_2_) {
        BulletEntity bulletEntity = CALogicHelper.createBullet(this);
        CALogicHelper.shootBulletAtTarget(this, target, bulletEntity, 0.0F, 1.6F);
        this.level.addFreshEntity(bulletEntity);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundEvents.PARROT_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PARROT_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.PARROT_DEATH;
    }


    protected boolean isFlapping() {
        return this.flyDist > this.nextFlap;
    }
    @Override
    protected void onFlap() {
        this.playSound(SoundEvents.PARROT_FLY, 0.15F, 1.0F);
        this.nextFlap = this.flyDist + this.flapSpeed / 2.0F;
    }

    // IDRONE METHODS

    @Override
    public boolean isOwned() {
        return this.entityData.get(DATA_OWNED);
    }

    @Override
    public void setOwned(boolean tame) {
        this.entityData.set(DATA_OWNED, tame);
    }

    @Override
    public boolean isPatrolling() {
        return this.patrolling;
    }

    @Override
    public void setPatrolling(boolean patrolling) {
        this.patrolling = patrolling;
    }

    @Override
    public boolean isRecalled() {
        return this.recalled;
    }

    @Override
    public void setRecalled(boolean recalled) {
        this.recalled = recalled;
    }

    @Override
    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNER_UUID).orElse((UUID)null);
    }

    @Override
    public void setOwnerUUID(@Nullable UUID ownerUUID) {
        this.entityData.set(DATA_OWNER_UUID, Optional.ofNullable(ownerUUID));
    }

    @Override
    @Nullable
    public LivingEntity getOwner() {
        UUID uuid = this.getOwnerUUID();
        return uuid == null ? null : this.level.getPlayerByUUID(uuid);
    }

    // IATTACHABLEDRONE METHODS

    @Override
    public boolean attachDrone(LivingEntity living) {
        IDroneController droneControllerCap = CapabilityHelper.getDroneControllerCap(living);
        if(droneControllerCap != null && !this.level.isClientSide){
            CompoundTag droneNBT = new CompoundTag();
            droneNBT.putString("id", this.getEncodeId());
            this.saveWithoutId(droneNBT);
            if (droneControllerCap.attachDrone(droneNBT)) {
                this.discard();
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean isFlying() {
        return !this.onGround;
    }

    @Override
    public boolean isAffectedByPotions() {
        return false;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return false;
    }

    @Override
    public boolean hasVisualLink() {
        return this.entityData.get(DATA_VISUAL_LINK);
    }

    @Override
    public void setVisualLink(boolean visualLink) {
        this.entityData.set(DATA_VISUAL_LINK, visualLink);
    }
}
