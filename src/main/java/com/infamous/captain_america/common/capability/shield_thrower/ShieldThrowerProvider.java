package com.infamous.captain_america.common.capability.shield_thrower;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShieldThrowerProvider implements ICapabilitySerializable<INBT> {

    @CapabilityInject(IShieldThrower.class)
    public static final Capability<IShieldThrower> SHIELD_THROWER_CAPABILITY = null;

    private LazyOptional<IShieldThrower> instance = LazyOptional.of(SHIELD_THROWER_CAPABILITY::getDefaultInstance);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == SHIELD_THROWER_CAPABILITY ? instance.cast() : LazyOptional.empty();    }

    @Override
    public INBT serializeNBT() {
        return SHIELD_THROWER_CAPABILITY.getStorage().writeNBT(SHIELD_THROWER_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        SHIELD_THROWER_CAPABILITY.getStorage().readNBT(SHIELD_THROWER_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null, nbt);
    }
}