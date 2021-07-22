package com.infamous.captain_america.common.capability.metal_arm;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

import Tag;

public class MetalArmStorage implements Capability.IStorage<IMetalArm> {

    @Nullable
    @Override
    public Tag writeNBT(Capability<IMetalArm> capability, IMetalArm instance, Direction side) {
        CompoundTag tag = new CompoundTag();
        tag.put("metalArmMainHand", instance.getMetalArmMainHand().save(new CompoundTag()));
        tag.put("metalArmOffHand", instance.getMetalArmOffHand().save(new CompoundTag()));
        return tag;
    }

    @Override
    public void readNBT(Capability<IMetalArm> capability, IMetalArm instance, Direction side, Tag nbt) {
        CompoundTag tag = (CompoundTag) nbt;

        if (tag.contains("metalArmMainHand", 10)) {
            instance.setMetalArmMainHand(ItemStack.of(tag.getCompound("metalArmMainHand")));
        }
        if (tag.contains("metalArmOffHand", 10)) {
            instance.setMetalArmOffHand(ItemStack.of(tag.getCompound("metalArmOffHand")));
        }
    }
}