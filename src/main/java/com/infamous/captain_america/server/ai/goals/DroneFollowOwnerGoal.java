package com.infamous.captain_america.server.ai.goals;

import com.infamous.captain_america.common.entity.drone.IDrone;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import java.util.EnumSet;

public class DroneFollowOwnerGoal<T extends MobEntity & IDrone> extends Goal {
   private final T drone;
   protected LivingEntity owner;
   private final IWorldReader level;
   private final double speedModifier;
   private final PathNavigator navigation;
   private int timeToRecalcPath;
   private final float stopDistance;
   private final float startDistance;
   private final float teleportDistanceSq;
   private float oldWaterCost;
   private final boolean canFly;

   public DroneFollowOwnerGoal(T droneMob, double speedModifier, float startDistance, float stopDistance, float teleportDistance, boolean canFly) {
      this.drone = droneMob;
      this.level = droneMob.level;
      this.speedModifier = speedModifier;
      this.navigation = droneMob.getNavigation();
      this.startDistance = startDistance;
      this.stopDistance = stopDistance;
      this.teleportDistanceSq = teleportDistance * teleportDistance;
      this.canFly = canFly;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      if (!(droneMob.getNavigation() instanceof GroundPathNavigator) && !(droneMob.getNavigation() instanceof FlyingPathNavigator)) {
         throw new IllegalArgumentException("Unsupported mob type for DroneFollowOwnerGoal");
      }
   }

   public boolean canUse() {
      LivingEntity livingentity = this.drone.getOwner();
      if (livingentity == null) {
         return false;
      } else if (livingentity.isSpectator()) {
         return false;
      } else if (this.drone.isPatrolling()) {
         return false;
      } else if (this.ownerIsWithinStartDistanceSq(livingentity) && !this.drone.isRecalled()) {
         return false;
      } else {
         this.owner = livingentity;
         return true;
      }
   }

   private boolean ownerIsWithinStartDistanceSq(LivingEntity livingentity) {
      return this.drone.distanceToSqr(livingentity) < (double) (this.startDistance * this.startDistance);
   }

   public boolean canContinueToUse() {
      if (this.navigation.isDone()) {
         return false;
      } else if (this.drone.isPatrolling()) {
         return false;
      } else {
         return !this.ownerIsWithinStopDistanceSq() || this.drone.isRecalled();
      }
   }

   private boolean ownerIsWithinStopDistanceSq() {
      return this.drone.distanceToSqr(this.owner) <= (double) (this.stopDistance * this.stopDistance);
   }

   public void start() {
      this.timeToRecalcPath = 0;
      this.oldWaterCost = this.drone.getPathfindingMalus(PathNodeType.WATER);
      this.drone.setPathfindingMalus(PathNodeType.WATER, 0.0F);
   }

   public void stop() {
      this.owner = null;
      this.navigation.stop();
      this.drone.setPathfindingMalus(PathNodeType.WATER, this.oldWaterCost);
   }

   public void tick() {
      this.drone.getLookControl().setLookAt(this.owner, 10.0F, (float)this.drone.getMaxHeadXRot());
      if (--this.timeToRecalcPath <= 0) {
         this.timeToRecalcPath = 10;
         if (!this.drone.isLeashed() && !this.drone.isPassenger()) {
            if(drone.isRecalled()){
               if(this.drone.getBoundingBox().inflate((double)0.2F).intersects(this.owner.getBoundingBox())){
                  this.handleOwnerIntersection();
               }
            }
            if (this.shouldTeleportToOwner()) {
               this.teleportToOwner();
            } else {
               double speedModifier = this.getSpeedModifier();
               this.navigation.moveTo(this.owner, speedModifier);
            }

         }
      }
   }

   protected boolean shouldTeleportToOwner() {
      return this.drone.distanceToSqr(this.owner) >= this.teleportDistanceSq;
   }

   protected double getSpeedModifier() {
      return this.speedModifier;
   }

   protected void handleOwnerIntersection() {

   }

   private void teleportToOwner() {
      BlockPos blockpos = this.owner.blockPosition();

      for(int i = 0; i < 10; ++i) {
         int j = this.randomIntInclusive(-3, 3);
         int k = this.randomIntInclusive(-1, 1);
         int l = this.randomIntInclusive(-3, 3);
         boolean flag = this.maybeTeleportTo(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
         if (flag) {
            return;
         }
      }

   }

   private boolean maybeTeleportTo(int p_226328_1_, int p_226328_2_, int p_226328_3_) {
      if (Math.abs((double)p_226328_1_ - this.owner.getX()) < 2.0D && Math.abs((double)p_226328_3_ - this.owner.getZ()) < 2.0D) {
         return false;
      } else if (!this.canTeleportTo(new BlockPos(p_226328_1_, p_226328_2_, p_226328_3_))) {
         return false;
      } else {
         this.drone.moveTo((double)p_226328_1_ + 0.5D, (double)p_226328_2_, (double)p_226328_3_ + 0.5D, this.drone.yRot, this.drone.xRot);
         this.navigation.stop();
         return true;
      }
   }

   private boolean canTeleportTo(BlockPos pos) {
      PathNodeType pathnodetype = WalkNodeProcessor.getBlockPathTypeStatic(this.level, pos.mutable());
      boolean cannotTeleportToAir = !this.canFly || pathnodetype != PathNodeType.OPEN;
      if (pathnodetype != PathNodeType.WALKABLE && cannotTeleportToAir) {
         return false;
      } else {
         BlockState blockstate = this.level.getBlockState(pos.below());
         if (!this.canFly && blockstate.getBlock() instanceof LeavesBlock) {
            return false;
         } else {
            BlockPos blockpos = pos.subtract(this.drone.blockPosition());
            return this.level.noCollision(this.drone, this.drone.getBoundingBox().move(blockpos));
         }
      }
   }

   private int randomIntInclusive(int p_226327_1_, int p_226327_2_) {
      return this.drone.getRandom().nextInt(p_226327_2_ - p_226327_1_ + 1) + p_226327_1_;
   }
}