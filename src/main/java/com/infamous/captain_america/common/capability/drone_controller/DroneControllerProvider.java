package com.infamous.captain_america.common.capability.drone_controller;

import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DroneControllerProvider implements ICapabilitySerializable<Tag> {

    @CapabilityInject(IDroneController.class)
    public static final Capability<IDroneController> DRONE_CONTROLLER_CAPABILITY = null;

    private LazyOptional<IDroneController> instance = LazyOptional.of(DRONE_CONTROLLER_CAPABILITY::getDefaultInstance);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == DRONE_CONTROLLER_CAPABILITY ? instance.cast() : LazyOptional.empty();    }

    @Override
    public Tag serializeNBT() {
        return DRONE_CONTROLLER_CAPABILITY.getStorage().writeNBT(DRONE_CONTROLLER_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null);
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        DRONE_CONTROLLER_CAPABILITY.getStorage().readNBT(DRONE_CONTROLLER_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null, nbt);
    }
}