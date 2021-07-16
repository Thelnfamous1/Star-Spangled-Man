package com.infamous.captain_america.common.util;

import com.google.common.collect.Lists;
import com.infamous.captain_america.common.entity.projectile.BulletEntity;
import com.infamous.captain_america.common.entity.projectile.CAProjectileEntity;
import com.infamous.captain_america.common.item.BulletItem;
import com.infamous.captain_america.common.registry.ItemRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.DyeColor;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.List;
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

    public static ItemStack createFirework(DyeColor dyeColor, int flightDuration){
        ItemStack rocketStack = new ItemStack(Items.FIREWORK_ROCKET, 1);
        ItemStack starStack = new ItemStack(Items.FIREWORK_STAR);
        CompoundNBT explosionTag = starStack.getOrCreateTagElement("Explosion");
        List<Integer> colors = Lists.newArrayList();
        colors.add(dyeColor.getFireworkColor());
        explosionTag.putIntArray("Colors", colors);
        explosionTag.putByte("Type", (byte) FireworkRocketItem.Shape.BURST.getId());
        CompoundNBT fireworksTag = rocketStack.getOrCreateTagElement("Fireworks");
        ListNBT explosionsNBT = new ListNBT();
        CompoundNBT explosionTag1 = starStack.getTagElement("Explosion");
        if (explosionTag1 != null) {
            explosionsNBT.add(explosionTag1);
        }

        fireworksTag.putByte("Flight", (byte)flightDuration);
        if (!explosionsNBT.isEmpty()) {
            fireworksTag.put("Explosions", explosionsNBT);
        }

        return rocketStack;
    }

    public static BulletEntity createBullet(LivingEntity shooter) {
        BulletItem bulletItem = ItemRegistry.SMALL_CALIBER_BULLET.get();
        ItemStack bulletStack = bulletItem.getDefaultInstance();
        BulletEntity bulletEntity = bulletItem.createBullet(shooter.level, bulletStack, shooter);
        bulletEntity.setIgnoreInvulnerability(true);
        return bulletEntity;
    }

    public static void shootBulletAtTarget(LivingEntity shooter, LivingEntity target, BulletEntity bulletEntity, float inaccuracy, float velocity) {
        double xDiff = target.getX() - shooter.getX();
        double zDiff = target.getZ() - shooter.getZ();
        //double horizDist = (double)MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);
        double yDiff = target.getY(0.3333333333333333D) - bulletEntity.getY();
        Vector3f projectileShotVector = getProjectileShotVectorToTarget(shooter, new Vector3d(xDiff, yDiff, zDiff), inaccuracy);
        bulletEntity.shoot((double)projectileShotVector.x(), (double)projectileShotVector.y(), (double)projectileShotVector.z(), velocity, inaccuracy);
        playShootBulletSound(shooter);
    }

    public static void playShootBulletSound(LivingEntity shooter) {
        shooter.playSound(SoundEvents.FIREWORK_ROCKET_SHOOT, 1.0F, 1.0F / (shooter.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    public static void shootBullet(LivingEntity shooter, BulletEntity bulletEntity, float inaccuracy, float velocity) {
        Vector3f projectileShotVector = getProjectileShotVector(shooter, inaccuracy);
        bulletEntity.shoot((double)projectileShotVector.x(), (double)projectileShotVector.y(), (double)projectileShotVector.z(), velocity, inaccuracy);
        playShootBulletSound(shooter);
    }

    private static Vector3f getProjectileShotVector(LivingEntity shooter, float inaccuracy){
        Vector3d vector3d1 = shooter.getUpVector(1.0F);
        Quaternion quaternion = new Quaternion(new Vector3f(vector3d1), inaccuracy, true);
        Vector3d vector3d = shooter.getViewVector(1.0F);
        Vector3f vector3f = new Vector3f(vector3d);
        vector3f.transform(quaternion);
        return vector3f;
    }

    private static Vector3f getProjectileShotVectorToTarget(LivingEntity shooter, Vector3d vector3d, float inaccuracy) {
        Vector3d normalizedShotVector = vector3d.normalize();
        Vector3d vector3d1 = normalizedShotVector.cross(new Vector3d(0.0D, 1.0D, 0.0D));
        if (vector3d1.lengthSqr() <= 1.0E-7D) {
            vector3d1 = normalizedShotVector.cross(shooter.getUpVector(1.0F));
        }

        Quaternion quaternion = new Quaternion(new Vector3f(vector3d1), 90.0F, true);
        Vector3f vector3f = new Vector3f(normalizedShotVector);
        vector3f.transform(quaternion);
        Quaternion quaternion1 = new Quaternion(vector3f, inaccuracy, true);
        Vector3f vector3f1 = new Vector3f(normalizedShotVector);
        vector3f1.transform(quaternion1);
        return vector3f1;
    }

}
