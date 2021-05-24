package com.infamous.captain_america.common.util;

import com.infamous.captain_america.common.item.EXO7FalconItem;
import com.infamous.captain_america.common.registry.SoundRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
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
        return hasEXO7Falcon(living) &&
                EXO7FalconItem.isFlightEnabled(living.getItemBySlot(EquipmentSlotType.CHEST));
    }

    public static boolean hasEXO7Falcon(LivingEntity living) {
        ItemStack chestplateStack = living.getItemBySlot(EquipmentSlotType.CHEST);
        return chestplateStack.getItem() instanceof EXO7FalconItem;
    }

    public static void takeOff(PlayerEntity player) {
        player.startFallFlying();
    }

    public static boolean isFlying(LivingEntity living){
        return living.isFallFlying() && canFalconFly(living);
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
     */
    public static void hover(LivingEntity living){
        Vector3d lookAngle = living.getLookAngle();
        Vector3d deltaMovement = living.getDeltaMovement();

        double d0 = 1.5D;
        double d1 = 0.1D;
        double newVerticalDelta = lookAngle.y * 0.1D + (lookAngle.y * 1.5D - deltaMovement.y) * 0.5D;
        living.setDeltaMovement(deltaMovement
                .add(0, newVerticalDelta, 0)
        );
        living.fallDistance = 0.0F;
    }

    public static void playFlightBoostSound(LivingEntity player) {
        player.playSound(SoundRegistry.FLIGHT_BOOST.get(), 1.0F, 1.0F);
    }
}
