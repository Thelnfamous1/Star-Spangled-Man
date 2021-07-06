package com.infamous.captain_america.common.util;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.item.EXO7FalconItem;
import com.infamous.captain_america.common.registry.SoundRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;

public class FalconFlightHelper {

    public static boolean canHover(LivingEntity living){
        return !living.isFallFlying()
                && !living.isInWater()
                && !living.hasEffect(Effects.LEVITATION)
                && canFalconFly(living);
    }

    public static boolean canTakeOff(LivingEntity living){
        return !living.isOnGround()
                && !living.isFallFlying()
                && !living.isInWater()
                && !living.hasEffect(Effects.LEVITATION)
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

    public static void takeOff(PlayerEntity player) {
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

    public static void haltFlight(PlayerEntity player) {
        player.stopFallFlying();
    }

    /**
     * Boosts the fall-flying living entity similar to how a FireworkRocketEntity does
     * This needs to be called on both the client and the server
     * @param living The LivingEntity that is to be boosted using their EXO-7 Falcon
     */
    public static void boostFlight(LivingEntity living) {
        Vector3d lookAngle = living.getLookAngle();
        Vector3d deltaMovement = living.getDeltaMovement();
        double d0 = 1.5D;
        double d1 = 0.1D;
        living.setDeltaMovement(deltaMovement
                .add(lookAngle.x * 0.1D + (lookAngle.x * 1.5D - deltaMovement.x) * 0.5D,
                        lookAngle.y * 0.1D + (lookAngle.y * 1.5D - deltaMovement.y) * 0.5D,
                        lookAngle.z * 0.1D + (lookAngle.z * 1.5D - deltaMovement.z) * 0.5D)
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
        Vector3d lookAngle = living.getLookAngle();
        Vector3d deltaMovement = living.getDeltaMovement();

        double moveBoost = 1.5D;
        double lookBoost = 0.1D;
        double lookAngleY = lookAngle.y;
        if(invert){
            lookAngleY *= -1;
        }
        double newVerticalDelta = lookAngleY * lookBoost + (lookAngleY * moveBoost - deltaMovement.y) * 0.5D;

        living.setDeltaMovement(deltaMovement
                .add(0, newVerticalDelta, 0)
        );
        living.fallDistance = 0.0F;
    }

    public static void playFlightBoostSound(LivingEntity player) {
        player.playSound(SoundRegistry.FLIGHT_BOOST.get(), 1.0F, 1.0F);
    }
}
