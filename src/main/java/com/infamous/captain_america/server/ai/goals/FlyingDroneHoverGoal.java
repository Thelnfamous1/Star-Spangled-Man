package com.infamous.captain_america.server.ai.goals;

import com.infamous.captain_america.common.entity.drone.IDrone;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class FlyingDroneHoverGoal<T extends PathfinderMob & IDrone & FlyingAnimal> extends Goal {
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
       Vec3 vector3d = this.findPos();
       if (vector3d != null) {
          this.drone.getNavigation().moveTo(this.drone.getNavigation().createPath(new BlockPos(vector3d), 1), this.speedModifier);
       }
    }

    @Nullable
    public Vec3 findPos() {
       Vec3 viewVector = this.drone.getViewVector(0.0F);

       int i = 8;
        Vec3 vec32 = HoverRandomPos.getPos(this.drone, 8, 7, viewVector.x, viewVector.z, ((float)Math.PI / 2F), 3, 1);
        return vec32 != null ? vec32 : AirAndWaterRandomPos.getPos(this.drone, 8, 4, -2, viewVector.x, viewVector.z, (double)((float)Math.PI / 2F));
    }
}
