package com.infamous.captain_america.common.capability.drone_controller;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DroneControllerProvider implements ICapabilitySerializable<CompoundTag> {

    @CapabilityInject(IDroneController.class)
    public static final Capability<IDroneController> DRONE_CONTROLLER_CAPABILITY = null;

    private LazyOptional<IDroneController> instance = LazyOptional.of(RedwingController::new);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == DRONE_CONTROLLER_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        IDroneController instance = this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!"));
        CompoundTag tag = new CompoundTag();
        tag.put("DroneNBT", instance.getDroneNBT());
        if(instance.getDroneUUID() != null){
            tag.putUUID("DroneUUID", instance.getDroneUUID());
        }
        return tag;    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        IDroneController instance = this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!"));
        instance.setDroneNBT(nbt.getCompound("DroneNBT"));
        if(nbt.hasUUID("DroneUUID")){
            instance.setDroneUUID(nbt.getUUID("DroneUUID"));
        }
    }
}