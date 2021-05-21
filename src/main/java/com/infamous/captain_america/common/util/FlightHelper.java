package com.infamous.captain_america.common.util;

import com.infamous.captain_america.common.items.EXO7FalconItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;

public class FlightHelper {

    public static boolean canTakeOff(PlayerEntity player){
        return !player.isOnGround()
                && !player.isFallFlying()
                && !player.isInWater()
                && !player.hasEffect(Effects.LEVITATION)
                && canFalconFly(player);
    }

    public static boolean canPropel(PlayerEntity player) {
        return isFalconFlying(player);
    }

    public static boolean canFalconFly(PlayerEntity player) {
        ItemStack chestplateStack = player.getItemBySlot(EquipmentSlotType.CHEST);
        return chestplateStack.getItem() instanceof EXO7FalconItem
                && EXO7FalconItem.isFlightEnabled(chestplateStack);
    }

    public static void takeOffFalconFlight(PlayerEntity player) {
        player.startFallFlying();
    }

    public static boolean isFalconFlying(PlayerEntity playerEntity){
        return playerEntity.isFallFlying() && canFalconFly(playerEntity);
    }

    public static void haltFalconFlight(PlayerEntity player) {
        player.stopFallFlying();
    }

    /**
     * Propels the fall-flying player similar to how a FireworkRocketEntity does
     * This needs to be called on both the client and the server
     * @param player The PlayerEntity that is to be propelled using their EXO-7 Falcon
     */
    public static void propelFalconFlight(PlayerEntity player) {
        Vector3d lookAngle = player.getLookAngle();
        double d0 = 1.5D;
        double d1 = 0.1D;
        Vector3d deltaMovement = player.getDeltaMovement();
        player.setDeltaMovement(deltaMovement
                        .add(lookAngle.x * 0.1D + (lookAngle.x * 1.5D - deltaMovement.x) * 0.5D,
                                lookAngle.y * 0.1D + (lookAngle.y * 1.5D - deltaMovement.y) * 0.5D,
                                lookAngle.z * 0.1D + (lookAngle.z * 1.5D - deltaMovement.z) * 0.5D));
    }
}
