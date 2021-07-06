package com.infamous.captain_america.common.capability.falcon_ability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FalconAbilityProvider implements ICapabilitySerializable<INBT> {

    @CapabilityInject(IFalconAbility.class)
    public static final Capability<IFalconAbility> FALCON_ABILITY_CAPABILITY = null;

    private LazyOptional<IFalconAbility> instance = LazyOptional.of(FALCON_ABILITY_CAPABILITY::getDefaultInstance);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == FALCON_ABILITY_CAPABILITY ? instance.cast() : LazyOptional.empty();    }

    @Override
    public INBT serializeNBT() {
        return FALCON_ABILITY_CAPABILITY.getStorage().writeNBT(FALCON_ABILITY_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        FALCON_ABILITY_CAPABILITY.getStorage().readNBT(FALCON_ABILITY_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null, nbt);
    }
}