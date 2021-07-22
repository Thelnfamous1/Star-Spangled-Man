package com.infamous.captain_america.common.util;

import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.item.EXO7FalconItem;
import com.infamous.captain_america.common.registry.SoundRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class FalconFlightHelper {

    public static boolean canHover(LivingEntity living){
        return !living.isFallFlying()
                && !living.isInWater()
                && !living.hasEffect(MobEffects.LEVITATION)
                && !living.isPassenger()
                && !living.onClimbable()
                && canFalconFly(living);
    }

    public static boolean canTakeOff(LivingEntity living){
        return !living.isOnGround()
                && !living.isFallFlying()
                && !living.isInWater()
                && !living.hasEffect(MobEffects.LEVITATION)
                && canFalconFly(living);
    }

    public static boolean canBoostFlight(LivingEntity living) {
        return isFlying(living);
    }

    public static boolean canFalconFly(LivingEntity living) {
        ItemStack chestPlateStack = getEXO7FalconStack(living);
        return hasEXO7Falcon(living) &&
                EXO7FalconItem.isFlightEnabled(chestPlateStack);
    }

    public static boolean hasEXO7Falcon(LivingEntity living) {
        ItemStack chestplateStack = getEXO7FalconStack(living);
        return EXO7FalconItem.isEXO7FalconStack(chestplateStack);
    }

    public static boolean toggleEXO7Falcon(LivingEntity living) {
        ItemStack chestplateStack = getEXO7FalconStack(living);
        boolean isEnabled = EXO7FalconItem.isFlightEnabled(chestplateStack);
        EXO7FalconItem.setFlightEnabled(chestplateStack, !isEnabled);
        return !isEnabled;
    }

    public static void toggleEXO7FalconTo(LivingEntity livingEntity, boolean toggleTo){
        ItemStack chestplateStack = getEXO7FalconStack(livingEntity);
        EXO7FalconItem.setFlightEnabled(chestplateStack, toggleTo);
    }

    public static ItemStack getEXO7FalconStack(LivingEntity living) {
        return living.getItemBySlot(EXO7FalconItem.SLOT);
    }

    public static boolean isFlipFlying(LivingEntity living){
        IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(living);
        if(falconAbilityCap == null) return false;

        return isFlying(living) && falconAbilityCap.isFlipping();
    }

    //TODO: Check for the isRolling value of the cap rather than vanilla spin attack flag
    public static boolean isRollFlying(LivingEntity living){
        return isFlying(living) && living.isAutoSpinAttack();
    }

    public static boolean isLaterallyFlying(LivingEntity living){
        return isFlying(living) && hasLateralImpulse(living);
    }

    private static boolean hasLateralImpulse(LivingEntity living) {
        return living.xxa != 0.0F;
    }

    public static void takeOff(Player player) {
        player.startFallFlying();
    }

    public static boolean isFlying(LivingEntity living){
        return living.isFallFlying() && canFalconFly(living);
    }

    public static boolean isDiving(LivingEntity living) {
        ItemStack falconStack = getEXO7FalconStack(living);
        return living.isFallFlying()
                && EXO7FalconItem.isEXO7FalconStack(falconStack)
                && !EXO7FalconItem.isFlightEnabled(falconStack);
    }

    public static boolean canFlipFly(LivingEntity living){
        return living.isFallFlying() && hasEXO7Falcon(living);
    }

    public static void haltFlight(Player player) {
        player.stopFallFlying();
    }

    /**
     * Boosts the fall-flying living entity similar to how a FireworkRocketEntity does
     * This needs to be called on both the client and the server
     * @param living The LivingEntity that is to be boosted using their EXO-7 Falcon
     */
    public static void boostFlight(LivingEntity living) {
        Vec3 lookAngle = living.getLookAngle();
        Vec3 motion = living.getDeltaMovement();
        double speed = 1.5D;
        double accel = 0.1D;
        living.setDeltaMovement(motion
                .add(lookAngle.x * accel + (lookAngle.x * speed - motion.x) * 0.5D,
                        lookAngle.y * accel + (lookAngle.y * speed - motion.y) * 0.5D,
                        lookAngle.z * accel + (lookAngle.z * speed - motion.z) * 0.5D)
        );
    }

    /**
     * Allows the non-fall flying living entity to fly vertically based on their look angle
     * The higher the living entity looks above straight ahead, the faster they fly upwards
     * The lower the living entity looks below straight ahead, the faster they fly downwards
     * Looking straight ahead allows a living entity to hover
     * Note that this follows the same logic for the y delta movement as FalconFlightHelper#boostFlight
     * @param living The LivingEntity that is to fly vertically using their EXO-7 Falcon
     * @param invert If true, inverts the vertical flight movement via multiplication by -1
     */
    public static void verticallyFly(LivingEntity living, boolean invert){
        Vec3 lookAngle = living.getLookAngle();
        Vec3 deltaMovement = living.getDeltaMovement();

        double speed = 1.5D;
        double accel = 0.1D;
        double lookAngleY = lookAngle.y;
        if(invert){
            lookAngleY *= -1;
        }
        double newVerticalDelta = lookAngleY * accel + (lookAngleY * speed - deltaMovement.y) * 0.5D;

        living.setDeltaMovement(deltaMovement
                .add(0, newVerticalDelta, 0)
        );
        living.fallDistance = 0.0F;
    }

    public static void playFlightBoostSound(LivingEntity player) {
        player.playSound(SoundRegistry.FLIGHT_BOOST.get(), 1.0F, 1.0F);
    }

    public static void animatePropulsion(LivingEntity living){
        Optional<EXO7FalconItem> optionalItem = EXO7FalconItem.getEXO7FalconItem(living);
        if(optionalItem.isPresent()) {
            ParticleOptions propulsionParticle = optionalItem.get().getPropulsionParticle();

            Vec3 particleSpawnPos = getPropulsionParticleSpawnPos(living);

            spawnParticles(living, propulsionParticle,  particleSpawnPos.x, particleSpawnPos.y, particleSpawnPos.z);
        }
    }

    // See PlayerEntity#getRopeHoldPosition which inspired this
    private static Vec3 getPropulsionParticleSpawnPos(LivingEntity living) {
        double xOffset = 0.0D;
        double yOffset = living.getEyeHeight() * 0.5D;
        double zOffset = -0.2D;
        float xRot = living.getXRot() * ((float)Math.PI / 180F);
        float yBodyRot = living.yBodyRot * ((float)Math.PI / 180F);
        if (!living.isFallFlying() && !living.isAutoSpinAttack()) {
            if (living.isVisuallySwimming()) {
                return living.position()
                        .add((new Vec3(xOffset, yOffset, zOffset))
                                .xRot(-xRot)
                                .yRot(-yBodyRot));
            } else {
                if(living.isCrouching()){
                    zOffset += -0.2D;
                }
                return living.position()
                        .add((new Vec3(xOffset, yOffset, zOffset))
                                .yRot(-yBodyRot));
            }
        } else {
            float zRot = calculateZRot(living);
            return living.position()
                    .add((new Vec3(xOffset, yOffset, zOffset))
                            .zRot(-zRot)
                            .xRot(-xRot)
                            .yRot(-yBodyRot));
        }
    }

    private static void spawnParticles(LivingEntity living, ParticleOptions propulsionParticle, double x, double y, double z) {
        if(living.level instanceof ServerLevel){
            int anInt = 1;
            double aDouble = 0.0D;
            ServerLevel serverWorld = (ServerLevel) living.level;
            serverWorld.sendParticles(propulsionParticle, x, y, z, anInt, 0.0D, 0.0D, 0.0D, aDouble);
        } else{
            living.level.addParticle(propulsionParticle, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    public static float calculateZRot(LivingEntity living) {
        Vec3 viewVector = living.getViewVector(0.0F);
        Vec3 deltaMove = living.getDeltaMovement();
        double deltaMoveHDS = deltaMove.horizontalDistanceSqr();
        double viewVectorHDS = viewVector.horizontalDistanceSqr();
        float zRot;
        if (deltaMoveHDS > 0.0D && viewVectorHDS > 0.0D) {
            double d3 = (deltaMove.x * viewVector.x + deltaMove.z * viewVector.z) / Math.sqrt(deltaMoveHDS * viewVectorHDS);
            double d4 = deltaMove.x * viewVector.z - deltaMove.z * viewVector.x;
            zRot = (float)(Math.signum(d4) * Math.acos(d3));
        } else {
            zRot = 0.0F;
        }
        return zRot;
    }
}
