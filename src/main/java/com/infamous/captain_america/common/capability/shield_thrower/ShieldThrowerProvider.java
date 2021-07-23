package com.infamous.captain_america.common.capability.shield_thrower;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShieldThrowerProvider implements ICapabilitySerializable<CompoundTag> {

    @CapabilityInject(IShieldThrower.class)
    public static final Capability<IShieldThrower> SHIELD_THROWER_CAPABILITY = null;

    private LazyOptional<IShieldThrower> instance = LazyOptional.of(ShieldThrower::new);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == SHIELD_THROWER_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
    }
}