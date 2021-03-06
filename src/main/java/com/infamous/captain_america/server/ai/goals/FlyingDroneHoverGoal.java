package com.infamous.captain_america.server.ai.goals;

import com.infamous.captain_america.common.entity.drone.IDrone;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class FlyingDroneHoverGoal<T extends CreatureEntity & IDrone & IFlyingAnimal> extends Goal {
    protected final T drone;
    private final double speedModifier;

    public FlyingDroneHoverGoal(T droneCreature, double speedModifier) {
        this.drone = droneCreature;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return !this.drone.isVehicle();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse();
    }

    public void start() {
       Vector3d vector3d = this.findPos();
       if (vector3d != null) {
          this.drone.getNavigation().moveTo(this.drone.getNavigation().createPath(new BlockPos(vector3d), 1), this.speedModifier);
       }
    }

    @Nullable
    public Vector3d findPos() {
       Vector3d viewVector = this.drone.getViewVector(0.0F);

       int i = 8;
       Vector3d vector3d2 = RandomPositionGenerator.getAboveLandPos(this.drone, 8, 7, viewVector, ((float)Math.PI / 2F), 2, 1);
       return vector3d2 != null ? vector3d2 : RandomPositionGenerator.getAirPos(this.drone, 8, 4, -2, viewVector, (double)((float)Math.PI / 2F));
    }
}
