package com.infamous.captain_america.common.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class CALogicHelper {
    public static final double RAYTRACE_DISTANCE = 16;

    public static RayTraceResult getLaserRayTrace(LivingEntity shooter){
        World world = shooter.level;
        BlockRayTraceResult blockRTR = (BlockRayTraceResult) shooter.pick(RAYTRACE_DISTANCE, 1.0F, false);
        Vector3d startVec = shooter.getEyePosition(1.0F);
        Vector3d lookVec = shooter.getViewVector(1.0F);
        Vector3d endVec = startVec.add(lookVec.x * RAYTRACE_DISTANCE, lookVec.y * RAYTRACE_DISTANCE, lookVec.z * RAYTRACE_DISTANCE);
        if (blockRTR.getType() != RayTraceResult.Type.MISS)
            endVec = blockRTR.getLocation();

        AxisAlignedBB targetAreaBoundingBox = shooter.getBoundingBox().expandTowards(lookVec.scale(RAYTRACE_DISTANCE)).inflate(1.0D);
        EntityRayTraceResult entityRTR = ProjectileHelper.getEntityHitResult(world, shooter, startVec, endVec, targetAreaBoundingBox, entity -> !entity.isSpectator() && entity.isPickable());

        if (entityRTR != null) {
            return entityRTR;
        } else{
            return blockRTR;
        }
    }
}
