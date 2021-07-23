package com.infamous.captain_america.common.capability.metal_arm;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MetalArmProvider implements ICapabilitySerializable<CompoundTag> {

    @CapabilityInject(IMetalArm.class)
    public static final Capability<IMetalArm> METAL_ARM_CAPABILITY = null;

    private LazyOptional<IMetalArm> instance = LazyOptional.of(MetalArm::new);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == METAL_ARM_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        IMetalArm instance = this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!"));
        CompoundTag tag = new CompoundTag();
        tag.put("metalArmMainHand", instance.getMetalArmMainHand().save(new CompoundTag()));
        tag.put("metalArmOffHand", instance.getMetalArmOffHand().save(new CompoundTag()));
        return tag;    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        IMetalArm instance = this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!"));

        if (nbt.contains("metalArmMainHand", 10)) {
            instance.setMetalArmMainHand(ItemStack.of(((CompoundTag) nbt).getCompound("metalArmMainHand")));
        }
        if (nbt.contains("metalArmOffHand", 10)) {
            instance.setMetalArmOffHand(ItemStack.of(((CompoundTag) nbt).getCompound("metalArmOffHand")));
        }
    }
}