package com.infamous.captain_america.server.ai.goals;

import com.infamous.captain_america.common.entity.IDrone;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;

public class DronePatrollingGoal<T extends CreatureEntity & IDrone> extends WaterAvoidingRandomWalkingGoal {
    private final T drone;

    public DronePatrollingGoal(T droneCreature, double speedModifier) {
        super(droneCreature, speedModifier);
        this.drone = droneCreature;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && this.drone.isPatrolling() && !this.drone.isRecalled();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.drone.isPatrolling() && !this.drone.isRecalled();
    }
}
