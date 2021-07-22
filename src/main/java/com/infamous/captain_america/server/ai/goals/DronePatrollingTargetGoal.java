package com.infamous.captain_america.server.ai.goals;

import com.infamous.captain_america.common.entity.drone.IDrone;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class DronePatrollingTargetGoal<T extends Mob & IDrone, E extends LivingEntity> extends NearestAttackableTargetGoal<E> {
   public static final boolean MUST_REACH = false;
   public static final int RANDOM_INTERVAL = 10;
   private final T drone;

   public DronePatrollingTargetGoal(T droneMob, Class<E> targetType, boolean mustSee, @Nullable Predicate<LivingEntity> targetConditions) {
      super(droneMob, targetType, RANDOM_INTERVAL, mustSee, MUST_REACH, targetConditions);
      this.drone = droneMob;
   }

   public boolean canUse() {
      return this.drone.isPatrolling() && !this.drone.isRecalled() && super.canUse();
   }

   public boolean canContinueToUse() {
      boolean test = this.targetConditions != null ? this.targetConditions.test(this.mob, this.target) : super.canContinueToUse();
      return test && this.drone.isPatrolling() && !this.drone.isRecalled();
   }
}