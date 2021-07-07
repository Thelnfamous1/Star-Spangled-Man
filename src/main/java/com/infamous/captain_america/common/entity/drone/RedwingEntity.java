package com.infamous.captain_america.common.entity.drone;

import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.drone_controller.IDroneController;
import com.infamous.captain_america.common.entity.projectile.BulletEntity;
import com.infamous.captain_america.common.item.BulletItem;
import com.infamous.captain_america.common.registry.ItemRegistry;
import com.infamous.captain_america.server.ai.goals.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class RedwingEntity extends CreatureEntity implements IFlyingAnimal, ICrossbowUser, IAttachableDrone {
    protected static final DataParameter<Boolean> DATA_OWNED = EntityDataManager.defineId(RedwingEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Optional<UUID>> DATA_OWNER_UUID = EntityDataManager.defineId(RedwingEntity.class, DataSerializers.OPTIONAL_UUID);
    private boolean patrolling;
    private boolean recalled;

    public RedwingEntity(EntityType<? extends RedwingEntity> type, World world) {
        super(type, world);
        this.moveControl = new FlyingMovementController(this, 20, true);
        this.setPathfindingMalus(PathNodeType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathNodeType.DAMAGE_FIRE, -1.0F);
        this.setPathfindingMalus(PathNodeType.COCOA, -1.0F);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ARMOR, 20.0D)
                .add(Attributes.FLYING_SPEED, (double)1.0F)
                .add(Attributes.MOVEMENT_SPEED, (double)0.3F)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new AttachableDroneFollowOwnerGoal<>(this, 1.5D, 10.0F, 2.0F, 12.0F, true));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.5D, 5, 10.0F));
        this.goalSelector.addGoal(3, new FlyingDroneWanderGoal<>(this, 1.5D));
        this.goalSelector.addGoal(4, new FlyingDronePatrollingGoal<>(this, 1.5D));

        this.targetSelector.addGoal(1, new DroneOwnerHurtByTargetGoal<>(this));
        this.targetSelector.addGoal(2, new DroneOwnerHurtTargetGoal<>(this));
        this.targetSelector.addGoal(3, (new DroneHurtByTargetGoal<>(this)).setAlertOthers());
        this.targetSelector.addGoal(4, new DronePatrollingTargetGoal<>(this, MobEntity.class, false, (living) -> living instanceof IMob));
    }

    @Override
    protected PathNavigator createNavigation(World world) {
        FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, world) {
            public boolean isStableDestination(BlockPos pos) {
                return !this.level.getBlockState(pos.below()).isAir();
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
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        this.addDroneNBT(compoundNBT);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
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
        if (!this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getOwner() instanceof ServerPlayerEntity) {
            this.getOwner().sendMessage(this.getCombatTracker().getDeathMessage(), Util.NIL_UUID);
        }

        super.die(damageSource);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, IWorldReader worldReader) {
        return worldReader.getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    @Override
    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
        return false;
    }

    @Override
    protected void checkFallDamage(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
    }

    @Override
    public void performRangedAttack(LivingEntity target, float p_82196_2_) {
        BulletEntity bulletEntity = this.createBullet();
        this.shootCrossbowProjectile(target, ItemStack.EMPTY, bulletEntity, 0.0F);
        this.level.addFreshEntity(bulletEntity);
    }

    private BulletEntity createBullet() {
        BulletItem bulletItem = ItemRegistry.PISTOL_BULLET.get();
        ItemStack bulletStack = bulletItem.getDefaultInstance();
        BulletEntity bulletEntity = bulletItem.createProjectile(this.level, bulletStack, this);
        bulletEntity.setIgnoreInvulnerability(true);
        return bulletEntity;
    }

    @Override
    public void shootCrossbowProjectile(LivingEntity target, ItemStack stack, ProjectileEntity projectile, float inaccuracy) {
        this.shootCrossbowProjectile(this, target, projectile, inaccuracy, 1.6F);
    }

    @Override
    public void shootCrossbowProjectile(LivingEntity shooter, LivingEntity target, ProjectileEntity projectile, float inaccuracy, float velocity) {
        double xDiff = target.getX() - shooter.getX();
        double zDiff = target.getZ() - shooter.getZ();
        double horizDist = (double)MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);
        double yDiff = target.getY(0.3333333333333333D) - projectile.getY();
        Vector3f projectileShotVector = this.getProjectileShotVector(shooter, new Vector3d(xDiff, yDiff, zDiff), inaccuracy);
        projectile.shoot((double)projectileShotVector.x(), (double)projectileShotVector.y(), (double)projectileShotVector.z(), velocity, inaccuracy);
        shooter.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (shooter.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    @Override
    public void setChargingCrossbow(boolean p_213671_1_) {
        // NO-OP
    }

    @Override
    public void onCrossbowAttackPerformed() {
        // NO-OP
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

    protected boolean makeFlySound() {
        return true;
    }

    @Override
    protected float playFlySound(float p_191954_1_) {
        this.playSound(SoundEvents.PARROT_FLY, 0.15F, 1.0F);
        return super.playFlySound(p_191954_1_);
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
            CompoundNBT droneNBT = new CompoundNBT();
            droneNBT.putString("id", this.getEncodeId());
            this.saveWithoutId(droneNBT);
            if (droneControllerCap.attachDrone(droneNBT)) {
                this.remove();
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean isAffectedByPotions() {
        return false;
    }

    @Override
    public boolean canBeAffected(EffectInstance p_70687_1_) {
        return false;
    }
}
