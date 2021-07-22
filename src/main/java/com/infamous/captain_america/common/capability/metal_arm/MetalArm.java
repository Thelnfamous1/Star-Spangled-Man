package com.infamous.captain_america.common.capability.metal_arm;

import net.minecraft.world.item.ItemStack;

public class MetalArm implements IMetalArm {
    
    private ItemStack metalArmOffHand = ItemStack.EMPTY;
    private ItemStack metalArmMainHand = ItemStack.EMPTY;
    
    @Override
    public ItemStack getMetalArmOffHand() {
        return this.metalArmOffHand;
    }

    @Override
    public ItemStack getMetalArmMainHand() {
        return this.metalArmMainHand;
    }

    @Override
    public void setMetalArmOffHand(ItemStack offHand) {
        this.metalArmOffHand = offHand;
    }

    @Override
    public void setMetalArmMainHand(ItemStack mainHand) {
        this.metalArmMainHand = mainHand;
    }
}
