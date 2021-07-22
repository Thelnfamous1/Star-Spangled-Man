package com.infamous.captain_america.server.ai.goals;

import com.infamous.captain_america.common.entity.drone.IDrone;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class DroneOwnerHurtTargetGoal<T extends Mob & IDrone> extends TargetGoal {
   private final T drone;
   private LivingEntity ownerLastHurt;
   private int timestamp;

   public DroneOwnerHurtTargetGoal(T droneMob) {
      super(droneMob, false);
      this.drone = droneMob;
      this.setFlags(EnumSet.of(Goal.Flag.TARGET));
   }

   public boolean canUse() {
      if (this.drone.isOwned() && !this.drone.isRecalled()) {
         LivingEntity owner = this.drone.getOwner();
         if (owner == null) {
            return false;
         } else {
            this.ownerLastHurt = owner.getLastHurtMob();
            int i = owner.getLastHurtMobTimestamp();
            return i != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT) && this.drone.wantsToAttack(this.ownerLastHurt, owner);
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean canContinueToUse() {
      return super.canContinueToUse() && !this.drone.isRecalled();
   }

   public void start() {
      this.mob.setTarget(this.ownerLastHurt);
      LivingEntity owner = this.drone.getOwner();
      if (owner != null) {
         this.timestamp = owner.getLastHurtMobTimestamp();
      }

      super.start();
   }
}