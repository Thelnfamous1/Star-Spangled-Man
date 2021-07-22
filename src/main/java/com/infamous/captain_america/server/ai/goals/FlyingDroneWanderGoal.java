package com.infamous.captain_america.server.ai.goals;

import com.infamous.captain_america.common.entity.drone.IDrone;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.FlyingAnimal;

public class FlyingDroneWanderGoal<T extends PathfinderMob & IDrone & FlyingAnimal> extends FlyingDroneHoverGoal<T> {

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