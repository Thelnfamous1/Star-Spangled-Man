package com.infamous.captain_america.server.ai.goals;

import com.infamous.captain_america.common.entity.drone.IDrone;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class DroneOwnerHurtByTargetGoal<T extends Mob & IDrone> extends TargetGoal {
   private final T drone;
   private LivingEntity ownerLastHurtBy;
   private int timestamp;

   public DroneOwnerHurtByTargetGoal(T droneMob) {
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
            this.ownerLastHurtBy = owner.getLastHurtByMob();
            int i = owner.getLastHurtByMobTimestamp();
            return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT) && this.drone.wantsToAttack(this.ownerLastHurtBy, owner);
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
      this.mob.setTarget(this.ownerLastHurtBy);
      LivingEntity owner = this.drone.getOwner();
      if (owner != null) {
         this.timestamp = owner.getLastHurtByMobTimestamp();
      }

      super.start();
   }
}