package com.infamous.captain_america.common.capability.metal_arm;

import net.minecraft.world.item.ItemStack;

public interface IMetalArm {
    
    ItemStack getMetalArmOffHand();

    ItemStack getMetalArmMainHand();
    
    void setMetalArmOffHand(ItemStack offHand);
    
    void setMetalArmMainHand(ItemStack mainHand);
}
