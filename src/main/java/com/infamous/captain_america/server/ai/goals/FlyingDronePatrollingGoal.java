package com.infamous.captain_america.server.ai.goals;

import com.infamous.captain_america.common.entity.drone.IDrone;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.passive.IFlyingAnimal;

public class FlyingDronePatrollingGoal<T extends CreatureEntity & IDrone & IFlyingAnimal> extends FlyingDroneHoverGoal<T> {
    private final T drone;

    public FlyingDronePatrollingGoal(T droneCreature, double speedModifier) {
        super(droneCreature, speedModifier);
        this.drone = droneCreature;
    }

    @Override
    public boolean canUse() {
        return super.canUse()
                && this.drone.getNavigation().isDone()
                && this.drone.isPatrolling()
                && !this.drone.isRecalled();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse()
                && this.drone.getNavigation().isInProgress()
                && this.drone.isPatrolling()
                && !this.drone.isRecalled();
    }
}
