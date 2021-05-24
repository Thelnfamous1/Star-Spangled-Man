package com.infamous.captain_america.server.ai.goals;

import java.util.EnumSet;

import com.infamous.captain_america.common.entity.IDrone;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;

public class DroneOwnerHurtByTargetGoal<T extends MobEntity & IDrone> extends TargetGoal {
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
            return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, EntityPredicate.DEFAULT) && this.drone.wantsToAttack(this.ownerLastHurtBy, owner);
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