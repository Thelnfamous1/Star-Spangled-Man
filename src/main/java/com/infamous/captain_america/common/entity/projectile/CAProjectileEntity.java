package com.infamous.captain_america.common.entity.projectile;

import com.google.common.collect.Lists;
import com.infamous.captain_america.common.util.CALogicHelper;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

/**
 * This is basically AbstractArrowEntity, but with increased extensibility
 */
public abstract class CAProjectileEntity extends Projectile {
   private static final EntityDataAccessor<Byte> ID_FLAGS = SynchedEntityData.defineId(CAProjectileEntity.class, EntityDataSerializers.BYTE);
   private static final EntityDataAccessor<Byte> PIERCE_LEVEL = SynchedEntityData.defineId(CAProjectileEntity.class, EntityDataSerializers.BYTE);
   @Nullable
   private BlockState lastState;
   protected boolean inGround;
   protected int inGroundTime;
   public CAProjectileEntity.PickupStatus pickup = CAProjectileEntity.PickupStatus.DISALLOWED;
   public int shakeTime;
   private int life;
   private double baseDamage = 2.0D;
   private int knockback;
   private SoundEvent soundEvent = this.getDefaultHitGroundSoundEvent();
   private IntOpenHashSet piercingIgnoreEntityIds;
   private List<Entity> piercedAndKilledEntities;

   protected CAProjectileEntity(EntityType<? extends CAProjectileEntity> entityType, Level world) {
      super(entityType, world);
   }

   protected CAProjectileEntity(EntityType<? extends CAProjectileEntity> entityType, double x, double y, double z, Level world) {
      this(entityType, world);
      this.setPos(x, y, z);
   }

   protected CAProjectileEntity(EntityType<? extends CAProjectileEntity> entityType, LivingEntity shooter, Level world) {
      this(entityType, shooter.getX(), shooter.getEyeY() - (double)0.1F, shooter.getZ(), world);
      this.setOwner(shooter);
      if (shooter instanceof Player) {
         this.pickup = CAProjectileEntity.PickupStatus.ALLOWED;
      }

   }

   public void setSoundEvent(SoundEvent soundEvent) {
      this.soundEvent = soundEvent;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldRenderAtSqrDistance(double sqrDistance) {
      double scaledBBSize = this.getBoundingBox().getSize() * 10.0D;
      if (Double.isNaN(scaledBBSize)) {
         scaledBBSize = 1.0D;
      }

      scaledBBSize = scaledBBSize * 64.0D * getViewScale();
      return sqrDistance < scaledBBSize * scaledBBSize;
   }

   protected void defineSynchedData() {
      this.entityData.define(ID_FLAGS, (byte)0);
      this.entityData.define(PIERCE_LEVEL, (byte)0);
   }

   public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
      super.shoot(x, y, z, velocity, inaccuracy);
      this.life = 0;
   }

   @OnlyIn(Dist.CLIENT)
   public void lerpTo(double x, double y, double z, float yRot, float xRot, int p_180426_9_, boolean p_180426_10_) {
      this.setPos(x, y, z);
      this.setRot(yRot, xRot);
   }

   @OnlyIn(Dist.CLIENT)
   public void lerpMotion(double x, double y, double z) {
      super.lerpMotion(x, y, z);
      this.life = 0;
   }

   public void tick() {
      super.tick();
      boolean preTick = this.preBaseProjectileTick();
      if(preTick){
         boolean noPhysics = this.isNoPhysics();
         Vec3 deltaMovement = this.getDeltaMovement();
         if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            float horizontalDist = Mth.sqrt(getHorizontalDistanceSqr(deltaMovement));
            this.yRot = (float)(Mth.atan2(deltaMovement.x, deltaMovement.z) * (double)(180F / (float)Math.PI));
            this.xRot = (float)(Mth.atan2(deltaMovement.y, (double)horizontalDist) * (double)(180F / (float)Math.PI));
            this.yRotO = this.yRot;
            this.xRotO = this.xRot;
         }

         BlockPos blockpos = this.blockPosition();
         BlockState blockstate = this.level.getBlockState(blockpos);
         if (!blockstate.isAir(this.level, blockpos) && !noPhysics) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.level, blockpos);
            if (!voxelshape.isEmpty()) {
               Vec3 vector3d1 = this.position();

               for(AABB axisalignedbb : voxelshape.toAabbs()) {
                  if (axisalignedbb.move(blockpos).contains(vector3d1)) {
                     this.inGround = true;
                     break;
                  }
               }
            }
         }

         if (this.shakeTime > 0) {
            --this.shakeTime;
         }

         if (this.isInWaterOrRain()) {
            this.clearFire();
         }

         if (this.inGround && !noPhysics) {
            if (this.lastState != blockstate && this.shouldFall()) {
               this.startFalling();
            } else if (!this.level.isClientSide) {
               this.tickDespawn();
            }

            ++this.inGroundTime;
         } else {
            this.inGroundTime = 0;
            this.tickCollision();
            this.tickMovement();
            this.checkInsideBlocks();
         }
         this.postBaseProjectileTick();
      }
   }

   protected void tickMovement() {
      boolean noPhysics = this.isNoPhysics();
      Vec3 deltaMovement = this.getDeltaMovement();
      double d3 = deltaMovement.x;
      double d4 = deltaMovement.y;
      double d0 = deltaMovement.z;
      if (this.isCritArrow()) {
         for(int i = 0; i < 4; ++i) {
            this.level.addParticle(ParticleTypes.CRIT, this.getX() + d3 * (double)i / 4.0D, this.getY() + d4 * (double)i / 4.0D, this.getZ() + d0 * (double)i / 4.0D, -d3, -d4 + 0.2D, -d0);
         }
      }

      double d5 = this.getX() + d3;
      double d1 = this.getY() + d4;
      double d2 = this.getZ() + d0;
      float f1 = Mth.sqrt(getHorizontalDistanceSqr(deltaMovement));
      if (noPhysics) {
         this.yRot = (float)(Mth.atan2(-d3, -d0) * (double)(180F / (float)Math.PI));
      } else {
         this.yRot = (float)(Mth.atan2(d3, d0) * (double)(180F / (float)Math.PI));
      }

      this.xRot = (float)(Mth.atan2(d4, (double)f1) * (double)(180F / (float)Math.PI));
      this.xRot = lerpRotation(this.xRotO, this.xRot);
      this.yRot = lerpRotation(this.yRotO, this.yRot);
      float f2 = this.getInertia();
      float f3 = getGravity();
      if (this.isInWater()) {
         for(int j = 0; j < 4; ++j) {
            float f4 = 0.25F;
            this.level.addParticle(ParticleTypes.BUBBLE, d5 - d3 * 0.25D, d1 - d4 * 0.25D, d2 - d0 * 0.25D, d3, d4, d0);
         }

         f2 = this.getWaterInertia();
      }

      this.setDeltaMovement(deltaMovement.scale((double)f2));
      if (!this.isNoGravity() && !noPhysics) {
         Vec3 vector3d4 = this.getDeltaMovement();
         this.setDeltaMovement(vector3d4.x, vector3d4.y - (double)f3, vector3d4.z);
      }

      this.setPos(d5, d1, d2);
   }

   protected void tickCollision() {
      boolean noPhysics = this.isNoPhysics();
      Vec3 deltaMovement = this.getDeltaMovement();
      Vec3 fromPos = this.position();
      Vec3 toPos = fromPos.add(deltaMovement);
      HitResult raytraceresult = this.level.clip(new ClipContext(fromPos, toPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
      if (raytraceresult.getType() != HitResult.Type.MISS) {
         toPos = raytraceresult.getLocation();
      }

      while(this.isAlive()) {
         EntityHitResult entityraytraceresult = this.findHitEntity(fromPos, toPos);
         if (entityraytraceresult != null) {
            raytraceresult = entityraytraceresult;
         }

         if (raytraceresult instanceof EntityHitResult) {
            Entity target = ((EntityHitResult)raytraceresult).getEntity();
            Entity shooter = this.getOwner();
            if (target instanceof Player && shooter instanceof Player && !((Player)shooter).canHarmPlayer((Player)target)) {
               raytraceresult = null;
               entityraytraceresult = null;
            }
         }

         if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS && !noPhysics && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            this.onHit(raytraceresult);
            this.hasImpulse = true;
         }

         if (entityraytraceresult == null || this.getPierceLevel() <= 0) {
            break;
         }

         raytraceresult = null;
      }
   }

   protected boolean preBaseProjectileTick() {
      return true;
   }

   protected void postBaseProjectileTick() {

   }

   protected float getGravity() {
      return 0.05F;
   }

   protected float getInertia() {
      return 0.99F;
   }

   private boolean shouldFall() {
      return this.inGround && this.level.noCollision((new AABB(this.position(), this.position())).inflate(0.06D));
   }

   private void startFalling() {
      this.inGround = false;
      Vec3 vector3d = this.getDeltaMovement();
      this.setDeltaMovement(vector3d.multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F)));
      this.life = 0;
   }

   public void move(MoverType moverType, Vec3 vector3d) {
      super.move(moverType, vector3d);
      if (moverType != MoverType.SELF && this.shouldFall()) {
         this.startFalling();
      }

   }

   protected void tickDespawn() {
      ++this.life;
      if (this.life >= 1200) {
         this.discard();
      }

   }

   protected void resetPiercedEntities() {
      if (this.piercedAndKilledEntities != null) {
         this.piercedAndKilledEntities.clear();
      }

      if (this.piercingIgnoreEntityIds != null) {
         this.piercingIgnoreEntityIds.clear();
      }

   }

   protected void onHitEntity(EntityHitResult entityRTR) {
      super.onHitEntity(entityRTR);
      Entity target = entityRTR.getEntity();
      float deltaMoveLength = (float)this.getDeltaMovement().length();
      int damageAmount = Mth.ceil(Mth.clamp((double)deltaMoveLength * this.baseDamage, 0.0D, 2.147483647E9D));
      if (updatePierced(target)) return;

      if (this.isCritArrow()) {
         long j = (long)this.random.nextInt(damageAmount / 2 + 2);
         damageAmount = (int)Math.min(j + (long)damageAmount, 2147483647L);
      }

      Entity shooter = this.getOwner();
      DamageSource damagesource;
      if (shooter == null) {
         damagesource = CALogicHelper.arrow(this, this);
      } else {
         damagesource = CALogicHelper.arrow(this, shooter);
         if (shooter instanceof LivingEntity) {
            ((LivingEntity)shooter).setLastHurtMob(target);
         }
      }

      int remainingFireTicks = target.getRemainingFireTicks();
      if (this.isOnFire()) {
         target.setSecondsOnFire(5);
      }

      if (target.hurt(damagesource, (float)damageAmount)) {

         if (target instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)target;
            if (!this.level.isClientSide && this.getPierceLevel() <= 0) {
               livingentity.setArrowCount(livingentity.getArrowCount() + 1);
            }

            if (this.knockback > 0) {
               Vec3 vector3d = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale((double)this.knockback * 0.6D);
               if (vector3d.lengthSqr() > 0.0D) {
                  livingentity.push(vector3d.x, 0.1D, vector3d.z);
               }
            }

            if (!this.level.isClientSide && shooter instanceof LivingEntity) {
               EnchantmentHelper.doPostHurtEffects(livingentity, shooter);
               EnchantmentHelper.doPostDamageEffects((LivingEntity)shooter, livingentity);
            }

            this.doPostHurtEffects(livingentity);
            if (livingentity != shooter && livingentity instanceof Player && shooter instanceof ServerPlayer && !this.isSilent()) {
               ((ServerPlayer)shooter).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
            }

            updatePiercedAndKilled(target, shooter, livingentity);
         }

         this.playSound(this.soundEvent, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
         if (this.getPierceLevel() <= 0) {
            this.discard();
         }
      } else {
         target.setRemainingFireTicks(remainingFireTicks);
         this.setDeltaMovement(this.getDeltaMovement().scale(-0.1D));
         this.yRot += 180.0F;
         this.yRotO += 180.0F;
         if (!this.level.isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
            if (this.pickup == CAProjectileEntity.PickupStatus.ALLOWED) {
               this.spawnAtLocation(this.getPickupItem(), 0.1F);
            }

            this.remove();
         }
      }

   }

   private boolean updatePierced(Entity target) {
      if (this.getPierceLevel() > 0) {
         if (this.piercingIgnoreEntityIds == null) {
            this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
         }

         if (this.piercedAndKilledEntities == null) {
            this.piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
         }

         if (this.piercingIgnoreEntityIds.size() >= this.getPierceLevel() + 1) {
            this.discard();
            return true;
         }

         this.piercingIgnoreEntityIds.add(target.getId());
      }
      return false;
   }

   private void updatePiercedAndKilled(Entity target, Entity shooter, LivingEntity livingentity) {
      if (!target.isAlive() && this.piercedAndKilledEntities != null) {
         this.piercedAndKilledEntities.add(livingentity);
      }

      if (!this.level.isClientSide && shooter instanceof ServerPlayer) {
         ServerPlayer serverplayerentity = (ServerPlayer) shooter;
         if (this.piercedAndKilledEntities != null && this.shotFromCrossbow()) {
            CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(serverplayerentity, this.piercedAndKilledEntities);
         } else if (!target.isAlive() && this.shotFromCrossbow()) {
            CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(serverplayerentity, Arrays.asList(target));
         }
      }
   }

   protected void onHitBlock(BlockHitResult blockRTR) {
      this.lastState = this.level.getBlockState(blockRTR.getBlockPos());
      super.onHitBlock(blockRTR);
      Vec3 vector3d = blockRTR.getLocation().subtract(this.getX(), this.getY(), this.getZ());
      this.setDeltaMovement(vector3d);
      Vec3 vector3d1 = vector3d.normalize().scale((double)0.05F);
      this.setPosRaw(this.getX() - vector3d1.x, this.getY() - vector3d1.y, this.getZ() - vector3d1.z);
      this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
      this.inGround = true;
      this.shakeTime = 7;
      this.setCritArrow(false);
      this.setPierceLevel((byte)0);
      this.setSoundEvent(SoundEvents.ARROW_HIT);
      this.setShotFromCrossbow(false);
      this.resetPiercedEntities();
   }

   protected SoundEvent getDefaultHitGroundSoundEvent() {
      return SoundEvents.ARROW_HIT;
   }

   protected final SoundEvent getHitGroundSoundEvent() {
      return this.soundEvent;
   }

   protected void doPostHurtEffects(LivingEntity target) {
   }

   @Nullable
   protected EntityHitResult findHitEntity(Vec3 vector3d, Vec3 vector3d1) {
      return ProjectileUtil.getEntityHitResult(this.level, this, vector3d, vector3d1, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
   }

   protected boolean canHitEntity(Entity entity) {
      return super.canHitEntity(entity) && checkEntityIsPierced(entity);
   }

   protected boolean checkEntityIsPierced(Entity entity) {
      return this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(entity.getId());
   }

   public void addAdditionalSaveData(CompoundTag compoundNBT) {
      super.addAdditionalSaveData(compoundNBT);
      compoundNBT.putShort("life", (short)this.life);
      if (this.lastState != null) {
         compoundNBT.put("inBlockState", NbtUtils.writeBlockState(this.lastState));
      }

      compoundNBT.putByte("shake", (byte)this.shakeTime);
      compoundNBT.putBoolean("inGround", this.inGround);
      compoundNBT.putByte("pickup", (byte)this.pickup.ordinal());
      compoundNBT.putDouble("damage", this.baseDamage);
      compoundNBT.putBoolean("crit", this.isCritArrow());
      compoundNBT.putByte("PierceLevel", this.getPierceLevel());
      compoundNBT.putString("SoundEvent", Registry.SOUND_EVENT.getKey(this.soundEvent).toString());
      compoundNBT.putBoolean("ShotFromCrossbow", this.shotFromCrossbow());
   }

   public void readAdditionalSaveData(CompoundTag compoundNBT) {
      super.readAdditionalSaveData(compoundNBT);
      this.life = compoundNBT.getShort("life");
      if (compoundNBT.contains("inBlockState", 10)) {
         this.lastState = NbtUtils.readBlockState(compoundNBT.getCompound("inBlockState"));
      }

      this.shakeTime = compoundNBT.getByte("shake") & 255;
      this.inGround = compoundNBT.getBoolean("inGround");
      if (compoundNBT.contains("damage", 99)) {
         this.baseDamage = compoundNBT.getDouble("damage");
      }

      if (compoundNBT.contains("pickup", 99)) {
         this.pickup = CAProjectileEntity.PickupStatus.byOrdinal(compoundNBT.getByte("pickup"));
      } else if (compoundNBT.contains("player", 99)) {
         this.pickup = compoundNBT.getBoolean("player") ? CAProjectileEntity.PickupStatus.ALLOWED : CAProjectileEntity.PickupStatus.DISALLOWED;
      }

      this.setCritArrow(compoundNBT.getBoolean("crit"));
      this.setPierceLevel(compoundNBT.getByte("PierceLevel"));
      if (compoundNBT.contains("SoundEvent", 8)) {
         this.soundEvent = Registry.SOUND_EVENT.getOptional(new ResourceLocation(compoundNBT.getString("SoundEvent"))).orElse(this.getDefaultHitGroundSoundEvent());
      }

      this.setShotFromCrossbow(compoundNBT.getBoolean("ShotFromCrossbow"));
   }

   public void setOwner(@Nullable Entity p_212361_1_) {
      super.setOwner(p_212361_1_);
      if (p_212361_1_ instanceof Player) {
         this.pickup = ((Player)p_212361_1_).getAbilities().instabuild ? CAProjectileEntity.PickupStatus.CREATIVE_ONLY : CAProjectileEntity.PickupStatus.ALLOWED;
      }

   }

   public void playerTouch(Player player) {
      if (!this.level.isClientSide && (this.inGround || this.isNoPhysics()) && this.shakeTime <= 0) {
         boolean flag = this.pickup == CAProjectileEntity.PickupStatus.ALLOWED || this.pickup == CAProjectileEntity.PickupStatus.CREATIVE_ONLY && player.getAbilities().instabuild || this.isNoPhysics() && this.getOwner().getUUID() == player.getUUID();
         if (this.pickup == CAProjectileEntity.PickupStatus.ALLOWED && !player.getInventory().add(this.getPickupItem())) {
            flag = false;
         }

         if (flag) {
            player.take(this, 1);
            this.discard();
         }

      }
   }

   protected abstract ItemStack getPickupItem();

   protected boolean isMovementNoisy() {
      return false;
   }

   public void setBaseDamage(double baseDamage) {
      this.baseDamage = baseDamage;
   }

   public double getBaseDamage() {
      return this.baseDamage;
   }

   public void setKnockback(int p_70240_1_) {
      this.knockback = p_70240_1_;
   }

   public boolean isAttackable() {
      return false;
   }

   protected float getEyeHeight(Pose pose, EntityDimensions entitySize) {
      return 0.13F;
   }

   public void setCritArrow(boolean critArrow) {
      this.setFlag(1, critArrow);
   }

   public void setPierceLevel(byte pierceLevel) {
      this.entityData.set(PIERCE_LEVEL, pierceLevel);
   }

   private void setFlag(int id, boolean flag) {
      byte b0 = this.entityData.get(ID_FLAGS);
      if (flag) {
         this.entityData.set(ID_FLAGS, (byte)(b0 | id));
      } else {
         this.entityData.set(ID_FLAGS, (byte)(b0 & ~id));
      }

   }

   public boolean isCritArrow() {
      byte b0 = this.entityData.get(ID_FLAGS);
      return (b0 & 1) != 0;
   }

   public boolean shotFromCrossbow() {
      byte b0 = this.entityData.get(ID_FLAGS);
      return (b0 & 4) != 0;
   }

   public byte getPierceLevel() {
      return this.entityData.get(PIERCE_LEVEL);
   }

   public void setEnchantmentEffectsFromEntity(LivingEntity shooter, float p_190547_2_) {
      int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER_ARROWS, shooter);
      int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH_ARROWS, shooter);
      this.setBaseDamage((double)(p_190547_2_ * 2.0F) + this.random.nextGaussian() * 0.25D + (double)((float)this.level.getDifficulty().getId() * 0.11F));
      if (i > 0) {
         this.setBaseDamage(this.getBaseDamage() + (double)i * 0.5D + 0.5D);
      }

      if (j > 0) {
         this.setKnockback(j);
      }

      if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAMING_ARROWS, shooter) > 0) {
         this.setSecondsOnFire(100);
      }

   }

   protected float getWaterInertia() {
      return 0.6F;
   }

   public void setNoPhysics(boolean noPhysics) {
      this.noPhysics = noPhysics;
      this.setFlag(2, noPhysics);
   }

   public boolean isNoPhysics() {
      if (!this.level.isClientSide) {
         return this.noPhysics;
      } else {
         return (this.entityData.get(ID_FLAGS) & 2) != 0;
      }
   }

   public void setShotFromCrossbow(boolean shotFromCrossbow) {
      this.setFlag(4, shotFromCrossbow);
   }

   @Override
   public Packet<?> getAddEntityPacket() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public static enum PickupStatus {
      DISALLOWED,
      ALLOWED,
      CREATIVE_ONLY;

      public static CAProjectileEntity.PickupStatus byOrdinal(int p_188795_0_) {
         if (p_188795_0_ < 0 || p_188795_0_ > values().length) {
            p_188795_0_ = 0;
         }

         return values()[p_188795_0_];
      }
   }
}