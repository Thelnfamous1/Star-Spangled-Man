package com.infamous.captain_america.common.capability.metal_arm;

import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MetalArmProvider implements ICapabilitySerializable<Tag> {

    @CapabilityInject(IMetalArm.class)
    public static final Capability<IMetalArm> METAL_ARM_CAPABILITY = null;

    private LazyOptional<IMetalArm> instance = LazyOptional.of(METAL_ARM_CAPABILITY::getDefaultInstance);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == METAL_ARM_CAPABILITY ? instance.cast() : LazyOptional.empty();    }

    @Override
    public Tag serializeNBT() {
        return METAL_ARM_CAPABILITY.getStorage().writeNBT(METAL_ARM_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null);
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        METAL_ARM_CAPABILITY.getStorage().readNBT(METAL_ARM_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null, nbt);
    }
}