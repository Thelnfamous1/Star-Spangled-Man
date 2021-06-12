package com.infamous.captain_america.common.capability.drone_controller;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.entity.drone.IDrone;
import com.infamous.captain_america.common.entity.drone.RedwingEntity;
import com.infamous.captain_america.common.item.EXO7FalconItem;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class RedwingController implements IDroneController {
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
    public void copyValuesFrom(IDroneController droneController) {
        this.droneNBT = droneController.getDroneNBT();
        this.droneUUID = droneController.getDroneUUID();
        this.droneRecalled = droneController.isDroneRecalled();
        this.dronePatrolling = droneController.isDronePatrolling();
    }

    @Override
    public void setDroneNBT(CompoundNBT droneNBT) {
        this.droneNBT = droneNBT;
    }

    @Override
    public void setDroneUUID(@Nullable UUID droneUUID) {
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
        return FalconFlightHelper.hasEXO7Falcon(controller)
                && !controller.level.isClientSide;
    }

    @Override
    public <T extends Entity & IDrone> Optional<T> createDrone(LivingEntity controller) {
        CaptainAmerica.LOGGER.info("Creating drone!");
        ItemStack chestStack = controller.getItemBySlot(EXO7FalconItem.SLOT);
        Optional<EntityType<? extends RedwingEntity>> optionalEntityType =
                EXO7FalconItem.getRedwingType(chestStack);
        if(optionalEntityType.isPresent()){
            RedwingEntity redwingEntity = optionalEntityType.get().create(controller.level);
            if (redwingEntity != null) {
                redwingEntity.setPos(controller.getX(), controller.getEyeY(), controller.getZ());
                redwingEntity.own(controller);
                CaptainAmerica.LOGGER.info("Created drone!");
                //noinspection unchecked
                return (Optional<T>) Optional.of(redwingEntity);
            }
        }
        CaptainAmerica.LOGGER.info("Failed to create drone!");
        return Optional.empty();
    }

    @Override
    public boolean isDroneRecalled() {
        return this.droneRecalled;
    }

    @Override
    public void setDroneRecalled(boolean droneRecalled) {
        this.droneRecalled = droneRecalled;
    }

    @Override
    public boolean isDronePatrolling() {
        return this.dronePatrolling;
    }

    @Override
    public void setDronePatrolling(boolean dronePatrolling) {
        this.dronePatrolling = dronePatrolling;
    }

}
