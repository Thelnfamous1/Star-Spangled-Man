package com.infamous.captain_america.common.capability.drone_controller;

import com.infamous.captain_america.common.entity.drone.IDrone;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public interface IDroneController {

    void copyValuesFrom(IDroneController droneController);

    void setDroneNBT(CompoundTag droneNBT);

    void setDroneUUID(@Nullable UUID droneUUID);

    CompoundTag getDroneNBT();

    @Nullable
    UUID getDroneUUID();

    default <T extends Entity & IDrone> boolean deployDrone(LivingEntity controller) {
        boolean hasDeployedDrone = this.getDeployedDrone(controller).isPresent();
        if(this.getDroneUUID() != null && hasDeployedDrone){
            return false;
        }

        if(this.canControlDrone(controller)
                && controller.level instanceof ServerLevel){

            if(this.getDroneNBT().isEmpty()
                    && this.getDroneUUID() == null){
                Optional<T> optionalDrone = this.createDrone(controller);
                if(optionalDrone.isPresent()){
                    T drone = optionalDrone.get();
                    boolean deployed = controller.level.addFreshEntity(drone);
                    if(deployed){
                        this.setDroneUUID(drone.getUUID());
                        return true;
                    }
                }
            } else if(!this.getDroneNBT().isEmpty()){
                Optional<Entity> optionalEntity = EntityType.create(this.getDroneNBT(), controller.level);
                if(optionalEntity.isPresent()){
                    Entity entity = optionalEntity.get();
                    if (entity instanceof IDrone) {
                        IDrone drone = (IDrone) entity;
                        drone.own(controller);
                        entity.setPos(controller.getX(), controller.getEyeY(), controller.getZ());
                        boolean deployed = ((ServerLevel)controller.level).addWithUUID(entity);
                        if(deployed){
                            this.setDroneUUID(entity.getUUID());
                            this.setDroneNBT(new CompoundTag());
                            return true;
                        }
                    }
                }
            }

            // If we get here, our deployed drone has perished
            // so we need to clear our stored data and try again
            if(this.getDroneUUID() != null && !hasDeployedDrone){
                this.resetDroneData();
                return this.deployDrone(controller);
            }
        }
        return false;
    }

    default boolean canControlDrone(LivingEntity controller){
        return !controller.level.isClientSide;
    }

    <T extends Entity & IDrone> Optional<T> createDrone(LivingEntity controller);

    default <T extends Entity & IDrone> boolean toggleRecallDrone(LivingEntity controller) {
        Optional<T> optionalDrone = this.getDeployedDrone(controller);
        if(optionalDrone.isPresent()){
            T drone = optionalDrone.get();
            drone.setRecalled(!drone.isRecalled());
            this.setDroneRecalled(drone.isRecalled());
            if(drone.isRecalled()){
                drone.setPatrolling(false);
                this.setDronePatrolling(false);
            }
            return true;
        }
        else{
            this.resetDroneData();
        }
        return false;
    }

    default <T extends Entity & IDrone> boolean toggleDronePatrol(LivingEntity controller) {
        Optional<T> optionalDrone = this.getDeployedDrone(controller);
        if(optionalDrone.isPresent()){
            T drone = optionalDrone.get();
            drone.setPatrolling(!drone.isPatrolling());
            this.setDronePatrolling(drone.isPatrolling());
            if(drone.isPatrolling()){
                drone.setRecalled(false);
                this.setDroneRecalled(false);
            }
            return true;
        }
        else{
            this.resetDroneData();
        }
        return false;
    }

    default <T extends Entity & IDrone> Optional<T> getDeployedDrone(LivingEntity controller){
        if(this.canControlDrone(controller)
                && controller.level instanceof ServerLevel
                && this.getDroneUUID() != null
                && this.getDroneNBT().isEmpty()){
            Entity entity = ((ServerLevel)controller.level).getEntity(this.getDroneUUID());
            if(entity instanceof IDrone){
                //noinspection unchecked
                T drone = (T) entity;
                return Optional.of(drone);
            }
        }
        return Optional.empty();
    }

    default void resetDroneData() {
        this.setDroneUUID(null);
        this.setDroneNBT(new CompoundTag());
        this.setDronePatrolling(false);
        this.setDroneRecalled(false);
    }

    default boolean attachDrone(CompoundTag droneNBT) {
        if (this.getDroneNBT().isEmpty()) {
            this.setDroneNBT(droneNBT);
            return true;
        }
        return false;
    }

    boolean isDroneRecalled();

    void setDroneRecalled(boolean droneRecalled);

    boolean isDronePatrolling();

    void setDronePatrolling(boolean dronePatrolling);
}
