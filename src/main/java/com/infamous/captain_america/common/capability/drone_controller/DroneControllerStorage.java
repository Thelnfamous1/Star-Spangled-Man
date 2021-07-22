package com.infamous.captain_america.common.capability.drone_controller;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

import Tag;

public class DroneControllerStorage implements Capability.IStorage<IDroneController> {

    @Nullable
    @Override
    public Tag writeNBT(Capability<IDroneController> capability, IDroneController instance, Direction side) {
        CompoundTag tag = new CompoundTag();
        tag.put("DroneNBT", instance.getDroneNBT());
        if(instance.getDroneUUID() != null){
            tag.putUUID("DroneUUID", instance.getDroneUUID());
        }
        return tag;
    }

    @Override
    public void readNBT(Capability<IDroneController> capability, IDroneController instance, Direction side, Tag nbt) {
        CompoundTag tag = (CompoundTag) nbt;
        instance.setDroneNBT(tag.getCompound("DroneNBT"));
        if(tag.hasUUID("DroneUUID")){
            instance.setDroneUUID(tag.getUUID("DroneUUID"));
        }
    }
}