package com.infamous.captain_america.common.util;

import com.infamous.captain_america.common.items.EXO7FalconItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;

public class FalconFlightHelper {

    public static boolean canFlyUp(PlayerEntity player){
        return !player.isFallFlying()
                && !player.isInWater()
                && !player.hasEffect(Effects.LEVITATION)
                && canFalconFly(player);
    }

    public static boolean canTakeOff(PlayerEntity player){
        return !player.isOnGround()
                && !player.isFallFlying()
                && !player.isInWater()
                && !player.hasEffect(Effects.LEVITATION)
                && canFalconFly(player);
    }

    public static boolean canBoostFlight(PlayerEntity player) {
        return isFlying(player);
    }

    public static boolean canFalconFly(PlayerEntity player) {
        ItemStack chestplateStack = player.getItemBySlot(EquipmentSlotType.CHEST);
        return chestplateStack.getItem() instanceof EXO7FalconItem
                && EXO7FalconItem.isFlightEnabled(chestplateStack);
    }

    public static void takeOff(PlayerEntity player) {
        player.startFallFlying();
    }

    public static boolean isFlying(PlayerEntity playerEntity){
        return playerEntity.isFallFlying() && canFalconFly(playerEntity);
    }

    public static void haltFlight(PlayerEntity player) {
        player.stopFallFlying();
    }

    /**
     * Propels the fall-flying player similar to how a FireworkRocketEntity does
     * This needs to be called on both the client and the server
     * @param player The PlayerEntity that is to be propelled using their EXO-7 Falcon
     */
    public static void boostFlight(PlayerEntity player) {
        Vector3d lookAngle = player.getLookAngle();
        double d0 = 1.5D;
        double d1 = 0.1D;
        Vector3d deltaMovement = player.getDeltaMovement();
        player.setDeltaMovement(deltaMovement
                .add(lookAngle.x * 0.1D + (lookAngle.x * 1.5D - deltaMovement.x) * 0.5D,
                        lookAngle.y * 0.1D + (lookAngle.y * 1.5D - deltaMovement.y) * 0.5D,
                        lookAngle.z * 0.1D + (lookAngle.z * 1.5D - deltaMovement.z) * 0.5D)
        );
    }

    public static void flyUp(PlayerEntity player){
        Vector3d deltaMovement = player.getDeltaMovement();
        Vector3d lookAngle = player.getLookAngle();

        double newVerticalDelta = lookAngle.y * 0.1D + (lookAngle.y * 1.5D - deltaMovement.y) * 0.5D;
        player.setDeltaMovement(deltaMovement
                .add(0, newVerticalDelta, 0)
        );
        player.fallDistance = 0.0F;
    }
}
