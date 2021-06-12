package com.infamous.captain_america.server.ai.goals;

import com.infamous.captain_america.common.entity.drone.IDrone;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.passive.IFlyingAnimal;

public class FlyingDroneWanderGoal<T extends CreatureEntity & IDrone & IFlyingAnimal> extends FlyingDroneHoverGoal<T> {

    public FlyingDroneWanderGoal(T droneCreature, double speedModifier) {
        super(droneCreature, speedModifier);
      }

      public boolean canUse() {
         return this.drone.getNavigation().isDone()
                 //&& this.drone.getRandom().nextInt(10) == 0
                 && !this.drone.isPatrolling()
                 && !this.drone.isRecalled();
      }

      public boolean canContinueToUse() {
         return this.drone.getNavigation().isInProgress()
                 && !this.drone.isPatrolling()
                 && !this.drone.isRecalled();
      }

}