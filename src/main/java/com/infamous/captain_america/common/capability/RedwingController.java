package com.infamous.captain_america.common.capability;

import com.infamous.captain_america.common.entity.IDrone;
import com.infamous.captain_america.common.entity.RedwingEntity;
import com.infamous.captain_america.common.item.EXO7FalconItem;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;

import java.util.Optional;
import java.util.UUID;

public class RedwingController implements IDroneController{
    private CompoundNBT droneNBT;
    private UUID droneUUID;
    private boolean droneRecalled;
    private boolean dronePatrolling;

    public RedwingController(){
        this.droneNBT = new CompoundNBT();
        this.droneUUID = Util.NIL_UUID;
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

    @Override
    public UUID getDroneUUID() {
        return this.droneUUID;
    }

    @Override
    public boolean canControlDrone(LivingEntity controller) {
        return FalconFlightHelper.hasEXO7Falcon(controller) && !controller.level.isClientSide;
    }

    @Override
    public <T extends Entity & IDrone> Optional<T> createDrone(LivingEntity controller) {
        ItemStack chestStack = controller.getItemBySlot(EXO7FalconItem.SLOT);
        Optional<EntityType<? extends RedwingEntity>> optionalEntityType =
                EXO7FalconItem.getRedwingType(chestStack);
        if(optionalEntityType.isPresent()){
            RedwingEntity redwingEntity = optionalEntityType.get().create(controller.level);
            if (redwingEntity != null) {
                redwingEntity.setPos(controller.getX(), controller.getEyeY(), controller.getZ());
                redwingEntity.own(controller);
                //noinspection unchecked
                return (Optional<T>) Optional.of(redwingEntity);
            }
        }
        return Optional.empty();
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
