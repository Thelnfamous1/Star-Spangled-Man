package com.infamous.captain_america.common.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class DroneControllerStorage implements Capability.IStorage<IDroneController> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<IDroneController> capability, IDroneController instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        tag.put("DroneNBT", instance.getDroneNBT());
        if(instance.getDroneUUID() != null){
            tag.putUUID("DroneUUID", instance.getDroneUUID());
        }
        return tag;
    }

    @Override
    public void readNBT(Capability<IDroneController> capability, IDroneController instance, Direction side, INBT nbt) {
        CompoundNBT tag = (CompoundNBT) nbt;
        instance.setDroneNBT(tag.getCompound("DroneNBT"));
        if(tag.hasUUID("DroneUUID")){
            instance.setDroneUUID(tag.getUUID("DroneUUID"));
        }
    }
}