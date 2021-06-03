package com.infamous.captain_america.common.util;

import com.infamous.captain_america.common.entity.projectile.VibraniumShieldEntity2;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.play.server.SCollectItemPacket;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;

public class VibraniumShieldHelper {

    public static void take(LivingEntity pickerUpper, Entity pickingUp, int pickUpAmount) {
        if (!pickingUp.removed && !pickerUpper.level.isClientSide && (pickingUp instanceof VibraniumShieldEntity2)) {
            ((ServerWorld)pickerUpper.level).getChunkSource().broadcast(pickingUp, new SCollectItemPacket(pickingUp.getId(), pickerUpper.getId(), pickUpAmount));
        }

    }

    public static boolean checkRicochetBlock(Entity projectile, Vector3d deltaMovementIn){
        World world = projectile.level;
        Vector3d projectilePosVec = projectile.position();
        Vector3d projectileNextPosVec = projectilePosVec.add(deltaMovementIn);

        BlockRayTraceResult blockHitResult = world.clip(new RayTraceContext(projectilePosVec, projectileNextPosVec, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, projectile));
        return blockHitResult.getType() != RayTraceResult.Type.MISS;
    }

    public static boolean checkRicochetEntityWithBlockCheck(Entity projectile, Vector3d deltaMovementIn, Entity target) {
        World world = projectile.level;
        Vector3d projectilePosVec = projectile.position();
        Vector3d projectileNextPosVec = projectilePosVec.add(deltaMovementIn);

        BlockRayTraceResult blockHitResult = world.clip(new RayTraceContext(projectilePosVec, projectileNextPosVec, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, projectile));
        if (blockHitResult.getType() != RayTraceResult.Type.MISS) {
            projectileNextPosVec = blockHitResult.getLocation();
        }

        return checkRicochetEntity(projectilePosVec, projectileNextPosVec, target);
    }

    public static boolean checkRicochetEntity(Vector3d projectilePosVec, Vector3d projectileFinishMovePosVec, Entity target) {
        AxisAlignedBB targetBoundingBox = target.getBoundingBox().inflate((double)0.3F);
        Optional<Vector3d> clipVec = targetBoundingBox.clip(projectilePosVec, projectileFinishMovePosVec);
        return clipVec.isPresent();
    }

}
