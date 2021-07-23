package com.infamous.captain_america.common.entity.projectile;

import com.google.common.collect.Lists;
import com.infamous.captain_america.common.advancements.CACriteriaTriggers;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.common.util.VibraniumShieldHelper;
import com.infamous.captain_america.server.network.packet.SShieldPacket;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class VibraniumShieldEntity extends Projectile {
   private static final EntityDataAccessor<Byte> ID_FLAGS = SynchedEntityData.defineId(VibraniumShieldEntity.class, EntityDataSerializers.BYTE);

   private static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.defineId(VibraniumShieldEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<ItemStack> DATA_SHIELD_ITEM = SynchedEntityData.defineId(VibraniumShieldEntity.class, EntityDataSerializers.ITEM_STACK);
   private static final EntityDataAccessor<Byte> DATA_THROW_TYPE = SynchedEntityData.defineId(VibraniumShieldEntity.class, EntityDataSerializers.BYTE);
   private static final EntityDataAccessor<Byte> ID_LOYALTY = SynchedEntityData.defineId(VibraniumShieldEntity.class, EntityDataSerializers.BYTE);

   public static final int MAX_LIFE_TICKS = 1200;

   @Nullable
   private BlockState lastHitBlockState;
   private int lastHitEntityId = -1;

   @Nullable
   private BlockState inBlockState;
   protected boolean inGround;
   protected int inGroundTime;
   public VibraniumShieldEntity.PickupStatus pickup = VibraniumShieldEntity.PickupStatus.ALLOWED;
   private int life;
   private double baseDamage = 2.0D;
   private int knockback;
   private SoundEvent soundEvent = this.getDefaultHitGroundSoundEvent();

   private boolean canRecall;
   public int clientSideReturnShieldTickCount;

   private IntOpenHashSet hitIgnoreEntityIds;
   private List<Entity> hitEntities;

   public VibraniumShieldEntity(EntityType<? extends VibraniumShieldEntity> entityType, Level world) {
      super(entityType, world);
   }

   public VibraniumShieldEntity(EntityType<? extends VibraniumShieldEntity> entityType, double x, double y, double z, Level world) {
      this(entityType, world);
      this.setPos(x, y, z);
   }

   public VibraniumShieldEntity(EntityType<? extends VibraniumShieldEntity> entityType, LivingEntity shooter, Level world, VibraniumShieldEntity.ThrowType throwType) {
      this(entityType, shooter.getX(), shooter.getEyeY() - (double)0.1F, shooter.getZ(), world);
      this.setOwner(shooter);
      if (shooter instanceof Player) {
         this.pickup = VibraniumShieldEntity.PickupStatus.ALLOWED;
      }
      this.setThrowTypeData((byte) throwType.ordinal());
   }

   public void setSoundEvent(SoundEvent soundEvent) {
      this.soundEvent = soundEvent;
   }

   @Override
   public boolean shouldRender(double x, double y, double z) {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public boolean shouldRenderAtSqrDistance(double distanceSq) {
      double bbSizeScaled = this.getBoundingBox().getSize() * 10.0D;
      if (Double.isNaN(bbSizeScaled)) {
         bbSizeScaled = 1.0D;
      }

      bbSizeScaled = bbSizeScaled * 64.0D * getViewScale();
      return distanceSq < bbSizeScaled * bbSizeScaled;
   }

   @Override
   protected void defineSynchedData() {
      this.entityData.define(ID_FOIL, false);
      this.entityData.define(DATA_SHIELD_ITEM, ItemStack.EMPTY);
      this.entityData.define(DATA_THROW_TYPE, (byte)0);
      this.entityData.define(ID_LOYALTY, (byte)0);
      this.entityData.define(ID_FLAGS, (byte)0);
   }

   @Override
   public void shoot(double x, double y, double z, float velocity, float offset) {
      super.shoot(x, y, z, velocity, offset);
      this.life = 0;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void lerpTo(double x, double y, double z, float yRot, float xRot, int p_180426_9_, boolean p_180426_10_) {
      this.setPos(x, y, z);
      this.setRot(yRot, xRot);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void lerpMotion(double x, double y, double z) {
      super.lerpMotion(x, y, z);
      this.life = 0;
   }

   @Override
   public void tick() {
      this.tickLoyalty();

      super.tick();

      Vec3 deltaMovement = this.getDeltaMovement();
      if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
         double horizontalDist = deltaMovement.horizontalDistance();
         this.setYRot((float)(Mth.atan2(deltaMovement.x, deltaMovement.z) * (double)(180F / (float)Math.PI)));
         this.setXRot((float)(Mth.atan2(deltaMovement.y, (double)horizontalDist) * (double)(180F / (float)Math.PI)));
         this.yRotO = this.getYRot();
         this.xRotO = this.getXRot();
      }

      BlockPos blockpos = this.blockPosition();
      BlockState blockstate = this.level.getBlockState(blockpos);
      if (!blockstate.isAir() && !noPhysics) {
         VoxelShape collisionShape = blockstate.getCollisionShape(this.level, blockpos);
         if (!collisionShape.isEmpty()) {
            Vec3 position = this.position();

            for(AABB subBox : collisionShape.toAabbs()) {
               if (subBox.move(blockpos).contains(position)) {
                  this.inGround = true;
                  break;
               }
            }
         }
      }

      if (this.isInWaterOrRain()) {
         this.clearFire();
      }

      if (this.inGround && !noPhysics) {
         if (this.inBlockState != blockstate && this.shouldFall()) {
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
   }

   private void tickLoyalty() {
      if (this.inGroundTime > 4) {
         this.canRecall = true;
      }

      Entity owner = this.getOwner();
      if ((this.canRecall || this.isNoPhysics()) && owner != null) {
         int loyaltyLevel = this.getLoyalty();
         if (loyaltyLevel > 0 && !this.isAcceptibleReturnOwner()) {
            if (!this.level.isClientSide && this.pickup == VibraniumShieldEntity.PickupStatus.ALLOWED) {
               this.spawnAtLocation(this.getPickupItem(), 0.1F);
            }

            this.discard();
         } else if (loyaltyLevel > 0) {
            this.setNoPhysics(true);
            Vec3 distanceVec = new Vec3(owner.getX() - this.getX(), owner.getEyeY() - this.getY(), owner.getZ() - this.getZ());
            this.setPosRaw(this.getX(), this.getY() + distanceVec.y * 0.015D * (double)loyaltyLevel, this.getZ());
            if (this.level.isClientSide) {
               this.yOld = this.getY();
            }

            double returnSpeedFactor = 0.05D * (double)loyaltyLevel;
            this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(distanceVec.normalize().scale(returnSpeedFactor)));
            if (this.clientSideReturnShieldTickCount == 0) {
               this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
            }

            ++this.clientSideReturnShieldTickCount;
         }
      }
   }

   private boolean isAcceptibleReturnOwner() {
      Entity owner = this.getOwner();
      if (owner != null && owner.isAlive()) {
         return !(owner instanceof ServerPlayer) || !owner.isSpectator();
      } else {
         return false;
      }
   }

   private void tickCollision() {
      boolean noPhysics = this.isNoPhysics();
      Vec3 deltaMovement = this.getDeltaMovement();
      Vec3 posVec = this.position();
      Vec3 nextPosVec = posVec.add(deltaMovement);

      HitResult rayTraceResult = this.level.clip(new ClipContext(posVec, nextPosVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
      if (rayTraceResult.getType() != HitResult.Type.MISS) {
         nextPosVec = rayTraceResult.getLocation();
      }

      EntityHitResult entityRTR = this.findHitEntity(posVec, nextPosVec);
      if (entityRTR != null) {
         rayTraceResult = entityRTR;
      }

      if (rayTraceResult instanceof EntityHitResult) {
         Entity rayTraceEntity = ((EntityHitResult)rayTraceResult).getEntity();
         Entity owner = this.getOwner();
         if (rayTraceEntity instanceof Player && owner instanceof Player && !((Player)owner).canHarmPlayer((Player)rayTraceEntity)) {
            rayTraceResult = null;
         }
      }

      if (rayTraceResult != null && rayTraceResult.getType() != HitResult.Type.MISS && !noPhysics && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, rayTraceResult)) {
         this.onHit(rayTraceResult);
         this.hasImpulse = true;
      }
   }

   private void tickMovement() {
      boolean noPhysics = this.isNoPhysics();
      Vec3 deltaMovement = this.getDeltaMovement();

      double xDeltaMove = deltaMovement.x;
      double yDeltaMove = deltaMovement.y;
      double zDeltaMove = deltaMovement.z;
      if (this.isCritShield()) {
         for(int i = 0; i < 4; ++i) {
            this.level.addParticle(ParticleTypes.CRIT, this.getX() + xDeltaMove * (double)i / 4.0D, this.getY() + yDeltaMove * (double)i / 4.0D, this.getZ() + zDeltaMove * (double)i / 4.0D, -xDeltaMove, -yDeltaMove + 0.2D, -zDeltaMove);
         }
      }

      double xMoveTo = this.getX() + xDeltaMove;
      double yMoveTo = this.getY() + yDeltaMove;
      double zMoveTo = this.getZ() + zDeltaMove;
      double f1 = deltaMovement.horizontalDistance();
      if (noPhysics) {
         this.setYRot((float)(Mth.atan2(-xDeltaMove, -zDeltaMove) * (double)(180F / (float)Math.PI)));
      } else {
         this.setYRot((float)(Mth.atan2(xDeltaMove, zDeltaMove) * (double)(180F / (float)Math.PI)));
      }

      this.setXRot((float)(Mth.atan2(yDeltaMove, (double)f1) * (double)(180F / (float)Math.PI)));
      this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
      this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
      float inertia = this.getInertia();
      float gravity = getGravity();
      if (this.isInWater()) {
         for(int j = 0; j < 4; ++j) {
            float particleSpeedFactor = 0.25F;
            this.level.addParticle(ParticleTypes.BUBBLE, xMoveTo - xDeltaMove * particleSpeedFactor, yMoveTo - yDeltaMove * particleSpeedFactor, zMoveTo - zDeltaMove * particleSpeedFactor, xDeltaMove, yDeltaMove, zDeltaMove);
         }

         inertia = this.getWaterInertia();
      }

      this.setDeltaMovement(deltaMovement.scale((double)inertia));
      if (!this.isNoGravity() && !noPhysics) {
         Vec3 deltaMovement1 = this.getDeltaMovement();
         this.setDeltaMovement(deltaMovement1.x, deltaMovement1.y - (double)gravity, deltaMovement1.z);
      }

      this.setPos(xMoveTo, yMoveTo, zMoveTo);
   }

   private boolean shouldFall() {
      return this.inGround && this.level.noCollision((new AABB(this.position(), this.position())).inflate(0.06D));
   }

   private void startFalling() {
      this.inGround = false;
      Vec3 deltaMovement = this.getDeltaMovement();
      float fallFactor = 0.2F;
      this.setDeltaMovement(deltaMovement.multiply((double)(this.random.nextFloat() * fallFactor), (double)(this.random.nextFloat() * fallFactor), (double)(this.random.nextFloat() * fallFactor)));
      this.life = 0;
   }

   @Override
   public void move(MoverType moverType, Vec3 moveVec) {
      super.move(moverType, moveVec);
      if (moverType != MoverType.SELF && this.shouldFall()) {
         this.startFalling();
      }

   }

   protected void tickDespawn() {
      int loyaltyLevel = this.entityData.get(ID_LOYALTY);
      if (this.pickup != VibraniumShieldEntity.PickupStatus.ALLOWED || loyaltyLevel <= 0) {
         ++this.life;
         if (this.life >= MAX_LIFE_TICKS) {
            if (this.pickup == PickupStatus.ALLOWED) {
               this.spawnAtLocation(this.getPickupItem(), 0.1F);
            }
            this.discard();
         }
      }
   }

   @Override
   protected void onHitEntity(EntityHitResult entityRTR) {
      super.onHitEntity(entityRTR);
      Entity hitEntity = entityRTR.getEntity();
      float deltaMoveLength = (float)this.getDeltaMovement().length();
      int damage = Mth.ceil(Mth.clamp((double)deltaMoveLength * this.baseDamage, 0.0D, 2.147483647E9D));

      this.initializeHitEntities();

      if (this.isCritShield()) {
         long critBonus = (long)this.random.nextInt(damage / 2 + 2);
         damage = (int)Math.min(critBonus + (long)damage, 2147483647L);
      }

      Entity owner = this.getOwner();
      DamageSource damagesource;
      if (owner == null) {
         damagesource = DamageSource.thrown(this, this);
      } else {
         damagesource = DamageSource.thrown(this, owner);
         if (owner instanceof LivingEntity) {
            ((LivingEntity)owner).setLastHurtMob(hitEntity);
         }
      }

      int hitFireTicks = hitEntity.getRemainingFireTicks();
      if (this.isOnFire()) {
         hitEntity.setSecondsOnFire(5);
      }

      boolean doHurt = hitEntity != owner && hitEntity.hurt(damagesource, (float) damage);

      if (doHurt) {
         if (hitEntity instanceof LivingEntity) {
            LivingEntity livingHit = (LivingEntity)hitEntity;

            if (this.knockback > 0) {
               Vec3 knockbackVector = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale((double)this.knockback * 0.6D);
               if (knockbackVector.lengthSqr() > 0.0D) {
                  livingHit.push(knockbackVector.x, 0.1D, knockbackVector.z);
               }
            }

            if (!this.level.isClientSide && owner instanceof LivingEntity) {
               EnchantmentHelper.doPostHurtEffects(livingHit, owner);
               EnchantmentHelper.doPostDamageEffects((LivingEntity)owner, livingHit);
            }

            this.doPostHurtEffects(livingHit);

            if (livingHit instanceof Player && owner instanceof ServerPlayer && !this.isSilent()) {
               ServerPlayer serverPlayer = (ServerPlayer) owner;
               NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SShieldPacket(SShieldPacket.Action.SHIELD_HIT_PLAYER));
            }

            this.updateHitEntities(livingHit);
         }

      } else {
         hitEntity.setRemainingFireTicks(hitFireTicks);
         this.checkNotMoving();
      }

      if(hitEntity != owner){
         this.playSound(this.soundEvent, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
         this.handleThrowImpact(entityRTR);
      }
   }

   private void checkNotMoving() {
      if (!this.level.isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
         if (this.pickup == PickupStatus.ALLOWED) {
            this.spawnAtLocation(this.getPickupItem(), 0.1F);
         }

         this.discard();
      }
   }

   @Override
   protected void onHitBlock(BlockHitResult blockRTR) {
      super.onHitBlock(blockRTR);
      this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
      if(blockRTR.getDirection().getAxis() == Direction.Axis.Y){
         this.inBlockState = this.level.getBlockState(blockRTR.getBlockPos());
         Vec3 posDiff = blockRTR.getLocation().subtract(this.getX(), this.getY(), this.getZ());
         this.setDeltaMovement(posDiff);
         Vec3 vector3d1 = posDiff.normalize().scale((double)0.05F);
         this.setPosRaw(this.getX() - vector3d1.x, this.getY() - vector3d1.y, this.getZ() - vector3d1.z);
         this.inGround = true;
         this.setCritShield(false);
         this.setSoundEvent(SoundEvents.SHIELD_BLOCK);
         this.resetHitEntities();
      } else{
         //TODO: Marker
         this.handleThrowImpact(blockRTR);
         //this.checkNotMoving();
      }
   }



   private void handleThrowImpact(HitResult rayTraceResult) {
      if(!this.inGround){
         switch (VibraniumShieldEntity.ThrowType.byOrdinal(this.getThrowTypeData())){
            case BOOMERANG_THROW:
               this.handleBoomerang(rayTraceResult);
               break;
            case RICOCHET_THROW:
               this.handleRicochet(rayTraceResult);
               break;
         }
      }
   }

   private void handleBoomerang(HitResult rayTraceResult) {
      if(rayTraceResult instanceof EntityHitResult){
         Entity entity = ((EntityHitResult) rayTraceResult).getEntity();
         if(this.lastHitEntityId != entity.getId()){
            this.lastHitEntityId = entity.getId();
            this.boomerang();
         }
         this.lastHitBlockState = null;
      }
      else if(rayTraceResult instanceof BlockHitResult){
         BlockPos blockPos = ((BlockHitResult) rayTraceResult).getBlockPos();
         if(this.lastHitBlockState != this.level.getBlockState(blockPos)) {
            this.lastHitBlockState = this.level.getBlockState(blockPos);
            this.boomerang();
         }
         this.lastHitEntityId = -1;
      }
   }

   private void boomerang() {
      this.setDeltaMovement(this.getDeltaMovement().scale(-1.0D));
      this.setYRot(this.getYRot() + 180.0F);
      this.yRotO += 180.0F;
   }

   private void handleRicochet(HitResult rayTraceResult) {
      if(rayTraceResult instanceof EntityHitResult){
         Entity entity = ((EntityHitResult) rayTraceResult).getEntity();
         if(this.lastHitEntityId != entity.getId()){
            this.lastHitEntityId = entity.getId();
            this.ricochetOffEntity(entity);
         }
         this.lastHitBlockState = null;
      } else if(rayTraceResult instanceof BlockHitResult){
         BlockPos blockPos = ((BlockHitResult) rayTraceResult).getBlockPos();
         if(this.lastHitBlockState != this.level.getBlockState(blockPos)){
            this.lastHitBlockState = this.level.getBlockState(blockPos);
            this.ricochetOffBlock();
         }
         this.lastHitEntityId = -1;
      }
   }

   private void ricochetOffBlock() {
      Vec3 deltaMovement = this.getDeltaMovement();
      Vec3 xMovement = new Vec3(deltaMovement.x, 0, 0);
      Vec3 yMovement = new Vec3(0, deltaMovement.y, 0);
      Vec3 zMovement = new Vec3(0, 0, deltaMovement.z);
      if(VibraniumShieldHelper.checkRicochetBlock(this, xMovement)){
         this.setDeltaMovement(deltaMovement.multiply(-1, 1, 1));
      }
      if(VibraniumShieldHelper.checkRicochetBlock(this, yMovement)){
         this.setDeltaMovement(deltaMovement.multiply(1, -1, 1));
      }
      if(VibraniumShieldHelper.checkRicochetBlock(this, zMovement)){
         this.setDeltaMovement(deltaMovement.multiply(1, 1, -1));
      }
   }

   private void ricochetOffEntity(Entity entity){
      Vec3 deltaMovement = this.getDeltaMovement();
      Vec3 xMovement = new Vec3(deltaMovement.x, 0, 0);
      Vec3 yMovement = new Vec3(0, deltaMovement.y, 0);
      Vec3 zMovement = new Vec3(0, 0, deltaMovement.z);
      if(VibraniumShieldHelper.checkRicochetEntityWithBlockCheck(this, xMovement, entity)){
         this.setDeltaMovement(deltaMovement.multiply(-1, 1, 1));
      }
      if(VibraniumShieldHelper.checkRicochetEntityWithBlockCheck(this, yMovement, entity)){
         this.setDeltaMovement(deltaMovement.multiply(1, -1, 1));
      }
      if(VibraniumShieldHelper.checkRicochetEntityWithBlockCheck(this, zMovement, entity)){
         this.setDeltaMovement(deltaMovement.multiply(1, 1, -1));
      }
   }

   private void initializeHitEntities() {
      if (this.hitIgnoreEntityIds == null) {
         this.hitIgnoreEntityIds = new IntOpenHashSet(3);
      }

      if (this.hitEntities == null) {
         this.hitEntities = Lists.newArrayListWithCapacity(3);
      }
   }

   private void updateHitEntities(LivingEntity livingHit) {
      if (this.hitEntities != null
              && (this.hitIgnoreEntityIds == null || !this.hitIgnoreEntityIds.contains(livingHit.getId()))) {
         this.hitEntities.add(livingHit);
         if(this.hitIgnoreEntityIds != null){
            this.hitIgnoreEntityIds.add(livingHit.getId());
         }
      }

      Entity owner = this.getOwner();

      if (!this.level.isClientSide && owner instanceof ServerPlayer) {
         ServerPlayer serverPlayer = (ServerPlayer)owner;
         if (this.hitEntities != null) {
            CACriteriaTriggers.HIT_BY_SHIELD.trigger(serverPlayer, this.hitEntities);
         } else {
            CACriteriaTriggers.HIT_BY_SHIELD.trigger(serverPlayer, Arrays.asList(livingHit));
         }
      }
   }

   private void resetHitEntities() {
      if (this.hitEntities != null) {
         this.hitEntities.clear();
      }

      if (this.hitIgnoreEntityIds != null) {
         this.hitIgnoreEntityIds.clear();
      }

   }

   protected SoundEvent getDefaultHitGroundSoundEvent() {
      return SoundEvents.SHIELD_BLOCK;
   }

   protected final SoundEvent getHitGroundSoundEvent() {
      return this.soundEvent;
   }

   protected void doPostHurtEffects(LivingEntity p_184548_1_) {
   }

   @Nullable
   protected EntityHitResult findHitEntity(Vec3 posVec, Vec3 nextPosVec) {
      return ProjectileUtil.getEntityHitResult(this.level, this, posVec, nextPosVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compoundNBT) {
      super.addAdditionalSaveData(compoundNBT);
      ItemStack shieldItem = this.getShieldItem();
      if (!shieldItem.isEmpty()) {
         compoundNBT.put("ShieldItem", shieldItem.save(new CompoundTag()));
      }
      compoundNBT.putByte("ThrowType", this.getThrowTypeData());

      compoundNBT.putShort("life", (short)this.life);
      if (this.inBlockState != null) {
         compoundNBT.put("inBlockState", NbtUtils.writeBlockState(this.inBlockState));
      }

      compoundNBT.putBoolean("inGround", this.inGround);
      compoundNBT.putByte("pickup", (byte)this.pickup.ordinal());
      compoundNBT.putDouble("damage", this.baseDamage);
      compoundNBT.putBoolean("crit", this.isCritShield());
      compoundNBT.putString("SoundEvent", Registry.SOUND_EVENT.getKey(this.soundEvent).toString());


      compoundNBT.putBoolean("CanRecall", this.canRecall);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compoundNBT) {
      super.readAdditionalSaveData(compoundNBT);
      ItemStack shieldItem = ItemStack.of(compoundNBT.getCompound("ShieldItem"));
      if (!shieldItem.isEmpty()) {
         this.setShieldItem(shieldItem);
      }

      if(compoundNBT.contains("ThrowType", 99)){
         this.setThrowTypeData(compoundNBT.getByte("ThrowType"));
      }

      this.life = compoundNBT.getShort("life");
      if (compoundNBT.contains("inBlockState", 10)) {
         this.inBlockState = NbtUtils.readBlockState(compoundNBT.getCompound("inBlockState"));
      }

      this.inGround = compoundNBT.getBoolean("inGround");
      if (compoundNBT.contains("damage", 99)) {
         this.baseDamage = compoundNBT.getDouble("damage");
      }

      if (compoundNBT.contains("pickup", 99)) {
         this.pickup = VibraniumShieldEntity.PickupStatus.byOrdinal(compoundNBT.getByte("pickup"));
      } else if (compoundNBT.contains("player", 99)) {
         this.pickup = compoundNBT.getBoolean("player") ? VibraniumShieldEntity.PickupStatus.ALLOWED : VibraniumShieldEntity.PickupStatus.DISALLOWED;
      }

      this.setCritShield(compoundNBT.getBoolean("crit"));
      if (compoundNBT.contains("SoundEvent", 8)) {
         this.soundEvent = Registry.SOUND_EVENT.getOptional(new ResourceLocation(compoundNBT.getString("SoundEvent"))).orElse(this.getDefaultHitGroundSoundEvent());
      }


      this.canRecall = compoundNBT.getBoolean("CanRecall");
   }

   @Override
   public void setOwner(@Nullable Entity entity) {
      super.setOwner(entity);
      if (entity instanceof Player) {
         this.pickup = ((Player)entity).getAbilities().instabuild ? VibraniumShieldEntity.PickupStatus.CREATIVE_ONLY : VibraniumShieldEntity.PickupStatus.ALLOWED;
      }

   }

   @Override
   public void playerTouch(Player player) {
      boolean leftOwner = this.getLeftOwner();
      if (!this.level.isClientSide && (leftOwner || this.inGround || this.isNoPhysics())) {
         boolean canPickUp =
                 this.pickup == VibraniumShieldEntity.PickupStatus.ALLOWED
                         || this.pickup == VibraniumShieldEntity.PickupStatus.CREATIVE_ONLY && player.getAbilities().instabuild
                         || this.isNoPhysics() && this.getOwner() != null && this.getOwner().getUUID() == player.getUUID();
         ItemStack pickupItem = this.getPickupItem();

         boolean addedToHand = canPickUp
                 && (this.setShieldInHand(player, InteractionHand.OFF_HAND) || this.setShieldInHand(player, InteractionHand.MAIN_HAND));

         if (canPickUp && !addedToHand && !player.getInventory().add(pickupItem)) {
            canPickUp = false;
         }

         if (canPickUp) {
            VibraniumShieldHelper.take(player, this, 1);
            this.discard();
         }

      }
   }

   private boolean setShieldInHand(Player player, InteractionHand hand) {
      if(player.getItemInHand(hand).isEmpty()){
         player.setItemInHand(hand, this.getPickupItem());
         return true;
      }
      return false;
   }

   private Boolean getLeftOwner() {
      return ObfuscationReflectionHelper.getPrivateValue(Projectile.class, this, "field_234611_d_");
   }

   protected ItemStack getPickupItem(){
      return this.getShieldItem();
   }

   @Override
   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.NONE;
   }

   public void setBaseDamage(double baseDamage) {
      this.baseDamage = baseDamage;
   }

   public double getBaseDamage() {
      return this.baseDamage;
   }

   public void setKnockback(int knockback) {
      this.knockback = knockback;
   }

   @Override
   public boolean isAttackable() {
      return false;
   }

   @Override
   protected float getEyeHeight(Pose pose, EntityDimensions entitySize) {
      return 0.13F;
   }

   public void setCritShield(boolean critShield) {
      this.setFlag(1, critShield);
   }

   private void setFlag(int flagId, boolean flagBoolean) {
      byte b0 = this.entityData.get(ID_FLAGS);
      if (flagBoolean) {
         this.entityData.set(ID_FLAGS, (byte)(b0 | flagId));
      } else {
         this.entityData.set(ID_FLAGS, (byte)(b0 & ~flagId));
      }

   }

   public boolean isCritShield() {
      byte b0 = this.entityData.get(ID_FLAGS);
      return (b0 & 1) != 0;
   }

   public ItemStack getShieldItem() {
      return this.entityData.get(DATA_SHIELD_ITEM);
   }

   public void setShieldItem(ItemStack shieldStack) {
      this.entityData.set(DATA_SHIELD_ITEM, shieldStack);
      this.entityData.set(ID_FOIL, shieldStack.hasFoil());
      this.entityData.set(ID_LOYALTY, (byte) EnchantmentHelper.getLoyalty(shieldStack));
   }

   public Byte getThrowTypeData(){
      return this.entityData.get(DATA_THROW_TYPE);
   }

   public void setThrowTypeData(byte throwTypeOrdinal){
      this.entityData.set(DATA_THROW_TYPE, throwTypeOrdinal);
   }

   public boolean isFoil() {
      return this.entityData.get(ID_FOIL);
   }

   public byte getLoyalty() {
      return this.entityData.get(ID_LOYALTY);
   }

   public void setEnchantmentEffectsFromEntity(LivingEntity living, float damageIn) {
      int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER_ARROWS, living);
      int punchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH_ARROWS, living);
      this.setBaseDamage((double)(damageIn * 2.0F) + this.random.nextGaussian() * 0.25D + (double)((float)this.level.getDifficulty().getId() * 0.11F));
      if (powerLevel > 0) {
         this.setBaseDamage(this.getBaseDamage() + (double)powerLevel * 0.5D + 0.5D);
      }

      if (punchLevel > 0) {
         this.setKnockback(punchLevel);
      }

   }

   protected float getGravity() {
      return 0.005F;
   }

   protected float getInertia() {
      return 0.999F;
   }

   protected float getWaterInertia() {
      return 0.8F;
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

   @Override
   public Packet<?> getAddEntityPacket() {
      return NetworkHooks.getEntitySpawningPacket(this);
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

   public enum ThrowType {
      BOOMERANG_THROW,
      RICOCHET_THROW;

      public static VibraniumShieldEntity.ThrowType byOrdinal(int ordinal) {
         if (ordinal < 0 || ordinal > values().length) {
            ordinal = 0;
         }

         return values()[ordinal];
      }
   }
}