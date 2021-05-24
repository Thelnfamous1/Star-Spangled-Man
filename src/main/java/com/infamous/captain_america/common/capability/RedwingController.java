package com.infamous.captain_america.common.capability;

import com.infamous.captain_america.common.entity.IDrone;
import com.infamous.captain_america.common.entity.RedwingEntity;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.UUID;

public class RedwingController implements IDroneController{
    private CompoundNBT droneNBT;
    private UUID droneUUID;
    private boolean droneRecalled;
    private boolean dronePatrolling;

    public RedwingController(){
        this.droneNBT = new CompoundNBT();
        this.droneUUID = null;
        this.droneRecalled = false;
        this.dronePatrolling = false;
    }

    @Override
    public void setDroneNBT(CompoundNBT droneNBT) {
        this.droneNBT = droneNBT;
    }

    @Override
    public void setDroneUUID(UUID droneUUID) {
        this.droneUUID = droneUUID;
    }

    @Override
    public CompoundNBT getDroneNBT() {
        return this.droneNBT;
    }

    @Nullable
    @Override
    public UUID getDroneUUID() {
        return this.droneUUID;
    }

    @Override
    public boolean canControlDrone(LivingEntity controller) {
        return FalconFlightHelper.hasEXO7Falcon(controller) && !controller.level.isClientSide;
    }

    @Override
    public <T extends MobEntity & IDrone> T createDrone(LivingEntity controller) {
        return (T) new RedwingEntity(controller);
    }

    @Override
    public boolean isDroneRecalled() {
        return this.droneRecalled;
    }

    @Override
    public void setDroneRecalled(boolean droneRecalled) {
        this.droneRecalled = droneRecalled;
        if(droneRecalled){
            this.dronePatrolling = false;
        }
    }

    @Override
    public boolean isDronePatrolling() {
        return this.dronePatrolling;
    }

    @Override
    public void setDronePatrolling(boolean dronePatrolling) {
        this.dronePatrolling = dronePatrolling;
        if(dronePatrolling){
            this.droneRecalled = false;
        }
    }

}
