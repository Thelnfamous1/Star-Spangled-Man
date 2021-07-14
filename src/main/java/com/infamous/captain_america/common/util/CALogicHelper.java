package com.infamous.captain_america.common.util;

import com.infamous.captain_america.common.entity.projectile.CAProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.UUID;

public class CALogicHelper {
    public static final UUID LASER_BEAM_REACH_DISTANCE_MODIFIER_UUID = UUID.fromString("0755016e-270a-48c4-93c0-832a69dc48ab");
    public static final double RAYTRACE_DISTANCE = 16;

    public static void extendReachDistance(LivingEntity livingEntity){
        ModifiableAttributeInstance reachAttribute = livingEntity.getAttribute(ForgeMod.REACH_DISTANCE.get());
        if(reachAttribute != null && reachAttribute.getModifier(LASER_BEAM_REACH_DISTANCE_MODIFIER_UUID) == null){
            if(reachAttribute.getValue() < RAYTRACE_DISTANCE){
                double modifierValue = RAYTRACE_DISTANCE - reachAttribute.getValue();
                AttributeModifier laserModifier = new AttributeModifier(LASER_BEAM_REACH_DISTANCE_MODIFIER_UUID, "Laser reach boost", modifierValue, AttributeModifier.Operation.ADDITION);
                reachAttribute.addTransientModifier(laserModifier);
            }
        }
    }

    public static void retractReachDistance(LivingEntity livingEntity){
        ModifiableAttributeInstance reachAttribute = livingEntity.getAttribute(ForgeMod.REACH_DISTANCE.get());
        if(reachAttribute != null){
            if(reachAttribute.getModifier(LASER_BEAM_REACH_DISTANCE_MODIFIER_UUID) != null){
                reachAttribute.removeModifier(LASER_BEAM_REACH_DISTANCE_MODIFIER_UUID);
            }
        }
    }

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

    public static DamageSource arrow(CAProjectileEntity p_76353_0_, @Nullable Entity p_76353_1_) {
        return (new IndirectEntityDamageSource("arrow", p_76353_0_, p_76353_1_)).setProjectile();
    }

    public static Hand getOppositeHand(Hand handIn){
        return handIn == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
    }

}
