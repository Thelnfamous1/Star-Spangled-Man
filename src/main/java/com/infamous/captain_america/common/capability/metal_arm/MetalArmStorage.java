package com.infamous.captain_america.common.capability.metal_arm;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class MetalArmStorage implements Capability.IStorage<IMetalArm> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<IMetalArm> capability, IMetalArm instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        tag.put("metalArmMainHand", instance.getMetalArmMainHand().save(new CompoundNBT()));
        tag.put("metalArmOffHand", instance.getMetalArmOffHand().save(new CompoundNBT()));
        return tag;
    }

    @Override
    public void readNBT(Capability<IMetalArm> capability, IMetalArm instance, Direction side, INBT nbt) {
        CompoundNBT tag = (CompoundNBT) nbt;

        if (tag.contains("metalArmMainHand", 10)) {
            instance.setMetalArmMainHand(ItemStack.of(tag.getCompound("metalArmMainHand")));
        }
        if (tag.contains("metalArmOffHand", 10)) {
            instance.setMetalArmOffHand(ItemStack.of(tag.getCompound("metalArmOffHand")));
        }
    }
}