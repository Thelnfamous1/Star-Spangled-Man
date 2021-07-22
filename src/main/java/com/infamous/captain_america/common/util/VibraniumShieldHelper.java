package com.infamous.captain_america.common.util;

import com.infamous.captain_america.common.entity.projectile.VibraniumShieldEntity;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class VibraniumShieldHelper {

    public static void take(LivingEntity pickerUpper, Entity pickingUp, int pickUpAmount) {
        if (!pickingUp.isRemoved() && !pickerUpper.level.isClientSide && (pickingUp instanceof VibraniumShieldEntity)) {
            ((ServerLevel)pickerUpper.level).getChunkSource().broadcast(pickingUp, new ClientboundTakeItemEntityPacket(pickingUp.getId(), pickerUpper.getId(), pickUpAmount));
        }

    }

    public static boolean checkRicochetBlock(Entity projectile, Vec3 deltaMovementIn){
        Level world = projectile.level;
        Vec3 projectilePosVec = projectile.position();
        Vec3 projectileNextPosVec = projectilePosVec.add(deltaMovementIn);

        BlockHitResult blockHitResult = world.clip(new ClipContext(projectilePosVec, projectileNextPosVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, projectile));
        return blockHitResult.getType() != HitResult.Type.MISS;
    }

    public static boolean checkRicochetEntityWithBlockCheck(Entity projectile, Vec3 deltaMovementIn, Entity target) {
        Level world = projectile.level;
        Vec3 projectilePosVec = projectile.position();
        Vec3 projectileNextPosVec = projectilePosVec.add(deltaMovementIn);

        BlockHitResult blockHitResult = world.clip(new ClipContext(projectilePosVec, projectileNextPosVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, projectile));
        if (blockHitResult.getType() != HitResult.Type.MISS) {
            projectileNextPosVec = blockHitResult.getLocation();
        }

        return checkRicochetEntity(projectilePosVec, projectileNextPosVec, target);
    }

    public static boolean checkRicochetEntity(Vec3 projectilePosVec, Vec3 projectileFinishMovePosVec, Entity target) {
        AABB targetBoundingBox = target.getBoundingBox().inflate((double)0.3F);
        Optional<Vec3> clipVec = targetBoundingBox.clip(projectilePosVec, projectileFinishMovePosVec);
        return clipVec.isPresent();
    }

}
