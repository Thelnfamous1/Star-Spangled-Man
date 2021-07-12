package com.infamous.captain_america.common.entity.projectile;

import com.google.common.collect.Lists;
import com.infamous.captain_america.common.util.CALogicHelper;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This is basically AbstractArrowEntity, but with increased extensibility
 */
public abstract class CAProjectileEntity extends ProjectileEntity {
   private static final DataParameter<Byte> ID_FLAGS = EntityDataManager.defineId(CAProjectileEntity.class, DataSerializers.BYTE);
   private static final DataParameter<Byte> PIERCE_LEVEL = EntityDataManager.defineId(CAProjectileEntity.class, DataSerializers.BYTE);
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

   protected CAProjectileEntity(EntityType<? extends CAProjectileEntity> entityType, World world) {
      super(entityType, world);
   }

   protected CAProjectileEntity(EntityType<? extends CAProjectileEntity> entityType, double x, double y, double z, World world) {
      this(entityType, world);
      this.setPos(x, y, z);
   }

   protected CAProjectileEntity(EntityType<? extends CAProjectileEntity> entityType, LivingEntity shooter, World world) {
      this(entityType, shooter.getX(), shooter.getEyeY() - (double)0.1F, shooter.getZ(), world);
      this.setOwner(shooter);
      if (shooter instanceof PlayerEntity) {
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
         Vector3d deltaMovement = this.getDeltaMovement();
         if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            float horizontalDist = MathHelper.sqrt(getHorizontalDistanceSqr(deltaMovement));
            this.yRot = (float)(MathHelper.atan2(deltaMovement.x, deltaMovement.z) * (double)(180F / (float)Math.PI));
            this.xRot = (float)(MathHelper.atan2(deltaMovement.y, (double)horizontalDist) * (double)(180F / (float)Math.PI));
            this.yRotO = this.yRot;
            this.xRotO = this.xRot;
         }

         BlockPos blockpos = this.blockPosition();
         BlockState blockstate = this.level.getBlockState(blockpos);
         if (!blockstate.isAir(this.level, blockpos) && !noPhysics) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.level, blockpos);
            if (!voxelshape.isEmpty()) {
               Vector3d vector3d1 = this.position();

               for(AxisAlignedBB axisalignedbb : voxelshape.toAabbs()) {
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
      Vector3d deltaMovement = this.getDeltaMovement();
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
      float f1 = MathHelper.sqrt(getHorizontalDistanceSqr(deltaMovement));
      if (noPhysics) {
         this.yRot = (float)(MathHelper.atan2(-d3, -d0) * (double)(180F / (float)Math.PI));
      } else {
         this.yRot = (float)(MathHelper.atan2(d3, d0) * (double)(180F / (float)Math.PI));
      }

      this.xRot = (float)(MathHelper.atan2(d4, (double)f1) * (double)(180F / (float)Math.PI));
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
         Vector3d vector3d4 = this.getDeltaMovement();
         this.setDeltaMovement(vector3d4.x, vector3d4.y - (double)f3, vector3d4.z);
      }

      this.setPos(d5, d1, d2);
   }

   protected void tickCollision() {
      boolean noPhysics = this.isNoPhysics();
      Vector3d deltaMovement = this.getDeltaMovement();
      Vector3d fromPos = this.position();
      Vector3d toPos = fromPos.add(deltaMovement);
      RayTraceResult raytraceresult = this.level.clip(new RayTraceContext(fromPos, toPos, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
      if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
         toPos = raytraceresult.getLocation();
      }

      while(this.isAlive()) {
         EntityRayTraceResult entityraytraceresult = this.findHitEntity(fromPos, toPos);
         if (entityraytraceresult != null) {
            raytraceresult = entityraytraceresult;
         }

         if (raytraceresult instanceof EntityRayTraceResult) {
            Entity target = ((EntityRayTraceResult)raytraceresult).getEntity();
            Entity shooter = this.getOwner();
            if (target instanceof PlayerEntity && shooter instanceof PlayerEntity && !((PlayerEntity)shooter).canHarmPlayer((PlayerEntity)target)) {
               raytraceresult = null;
               entityraytraceresult = null;
            }
         }

         if (raytraceresult != null && raytraceresult.getType() != RayTraceResult.Type.MISS && !noPhysics && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
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
      return this.inGround && this.level.noCollision((new AxisAlignedBB(this.position(), this.position())).inflate(0.06D));
   }

   private void startFalling() {
      this.inGround = false;
      Vector3d vector3d = this.getDeltaMovement();
      this.setDeltaMovement(vector3d.multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F)));
      this.life = 0;
   }

   public void move(MoverType moverType, Vector3d vector3d) {
      super.move(moverType, vector3d);
      if (moverType != MoverType.SELF && this.shouldFall()) {
         this.startFalling();
      }

   }

   protected void tickDespawn() {
      ++this.life;
      if (this.life >= 1200) {
         this.remove();
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

   protected void onHitEntity(EntityRayTraceResult entityRTR) {
      super.onHitEntity(entityRTR);
      Entity target = entityRTR.getEntity();
      float deltaMoveLength = (float)this.getDeltaMovement().length();
      int damageAmount = MathHelper.ceil(MathHelper.clamp((double)deltaMoveLength * this.baseDamage, 0.0D, 2.147483647E9D));
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
               Vector3d vector3d = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale((double)this.knockback * 0.6D);
               if (vector3d.lengthSqr() > 0.0D) {
                  livingentity.push(vector3d.x, 0.1D, vector3d.z);
               }
            }

            if (!this.level.isClientSide && shooter instanceof LivingEntity) {
               EnchantmentHelper.doPostHurtEffects(livingentity, shooter);
               EnchantmentHelper.doPostDamageEffects((LivingEntity)shooter, livingentity);
            }

            this.doPostHurtEffects(livingentity);
            if (livingentity != shooter && livingentity instanceof PlayerEntity && shooter instanceof ServerPlayerEntity && !this.isSilent()) {
               ((ServerPlayerEntity)shooter).connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.ARROW_HIT_PLAYER, 0.0F));
            }

            updatePiercedAndKilled(target, shooter, livingentity);
         }

         this.playSound(this.soundEvent, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
         if (this.getPierceLevel() <= 0) {
            this.remove();
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
            this.remove();
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

      if (!this.level.isClientSide && shooter instanceof ServerPlayerEntity) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) shooter;
         if (this.piercedAndKilledEntities != null && this.shotFromCrossbow()) {
            CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(serverplayerentity, this.piercedAndKilledEntities);
         } else if (!target.isAlive() && this.shotFromCrossbow()) {
            CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(serverplayerentity, Arrays.asList(target));
         }
      }
   }

   protected void onHitBlock(BlockRayTraceResult blockRTR) {
      this.lastState = this.level.getBlockState(blockRTR.getBlockPos());
      super.onHitBlock(blockRTR);
      Vector3d vector3d = blockRTR.getLocation().subtract(this.getX(), this.getY(), this.getZ());
      this.setDeltaMovement(vector3d);
      Vector3d vector3d1 = vector3d.normalize().scale((double)0.05F);
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
   protected EntityRayTraceResult findHitEntity(Vector3d vector3d, Vector3d vector3d1) {
      return ProjectileHelper.getEntityHitResult(this.level, this, vector3d, vector3d1, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
   }

   protected boolean canHitEntity(Entity entity) {
      return super.canHitEntity(entity) && checkEntityIsPierced(entity);
   }

   protected boolean checkEntityIsPierced(Entity entity) {
      return this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(entity.getId());
   }

   public void addAdditionalSaveData(CompoundNBT compoundNBT) {
      super.addAdditionalSaveData(compoundNBT);
      compoundNBT.putShort("life", (short)this.life);
      if (this.lastState != null) {
         compoundNBT.put("inBlockState", NBTUtil.writeBlockState(this.lastState));
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

   public void readAdditionalSaveData(CompoundNBT compoundNBT) {
      super.readAdditionalSaveData(compoundNBT);
      this.life = compoundNBT.getShort("life");
      if (compoundNBT.contains("inBlockState", 10)) {
         this.lastState = NBTUtil.readBlockState(compoundNBT.getCompound("inBlockState"));
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
      if (p_212361_1_ instanceof PlayerEntity) {
         this.pickup = ((PlayerEntity)p_212361_1_).abilities.instabuild ? CAProjectileEntity.PickupStatus.CREATIVE_ONLY : CAProjectileEntity.PickupStatus.ALLOWED;
      }

   }

   public void playerTouch(PlayerEntity player) {
      if (!this.level.isClientSide && (this.inGround || this.isNoPhysics()) && this.shakeTime <= 0) {
         boolean flag = this.pickup == CAProjectileEntity.PickupStatus.ALLOWED || this.pickup == CAProjectileEntity.PickupStatus.CREATIVE_ONLY && player.abilities.instabuild || this.isNoPhysics() && this.getOwner().getUUID() == player.getUUID();
         if (this.pickup == CAProjectileEntity.PickupStatus.ALLOWED && !player.inventory.add(this.getPickupItem())) {
            flag = false;
         }

         if (flag) {
            player.take(this, 1);
            this.remove();
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

   protected float getEyeHeight(Pose pose, EntitySize entitySize) {
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

   public IPacket<?> getAddEntityPacket() {
      Entity entity = this.getOwner();
      return new SSpawnObjectPacket(this, entity == null ? 0 : entity.getId());
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