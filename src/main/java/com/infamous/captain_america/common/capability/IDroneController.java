package com.infamous.captain_america.common.capability;

import com.infamous.captain_america.common.entity.IDrone;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public interface IDroneController {

    void setDroneNBT(CompoundNBT droneNBT);

    void setDroneUUID(UUID droneUUID);

    CompoundNBT getDroneNBT();

    @Nullable
    UUID getDroneUUID();

    default <T extends MobEntity & IDrone> boolean deployDrone(LivingEntity controller) {
        if(this.canControlDrone(controller)){
            if(this.getDroneNBT().isEmpty() && this.getDroneUUID() == null){
                T drone = this.createDrone(controller);
                boolean deployed = controller.level.addFreshEntity(drone);
                if(deployed){
                    this.setDroneUUID(drone.getUUID());
                    return true;
                }
            } else if(!this.getDroneNBT().isEmpty()){
                Optional<Entity> optionalEntity = EntityType.create(this.getDroneNBT(), controller.level);
                if(optionalEntity.isPresent()){
                    Entity entity = optionalEntity.get();
                    if (entity instanceof IDrone) {
                        IDrone drone = (IDrone) entity;
                        drone.own(controller);
                        entity.setPos(controller.getX(), controller.getY() + 2.0D, controller.getZ());
                        boolean deployed = ((ServerWorld)controller.level).addWithUUID(entity);
                        if(deployed){
                            this.setDroneUUID(entity.getUUID());
                            this.setDroneNBT(new CompoundNBT());
                            return true;
                        } else{
                            this.setDroneUUID(null);
                            this.setDroneNBT(new CompoundNBT());
                        }
                    } else {
                        this.setDroneUUID(null);
                        this.setDroneNBT(new CompoundNBT());
                    }
                } else {
                    this.setDroneUUID(null);
                    this.setDroneNBT(new CompoundNBT());
                }
            }
        }
        return false;
    }

    boolean canControlDrone(LivingEntity controller);

    <T extends MobEntity & IDrone> T createDrone(LivingEntity controller);

    default boolean toggleRecallDrone(LivingEntity controller) {
        if(this.canControlDrone(controller)){
            if(this.isDeployed()){
                Entity entity = ((ServerWorld)controller.level).getEntity(this.getDroneUUID());
                if(entity instanceof IDrone){
                    IDrone drone = (IDrone) entity;
                    drone.setRecalled(!drone.isRecalled());
                    return true;
                } else {
                    this.setDroneUUID(null);
                    return false;
                }
            }
        }
        return false;
    }

    default boolean toggleDronePatrol(LivingEntity controller) {
        if(this.canControlDrone(controller)){
            if(this.isDeployed()){
                Entity entity = ((ServerWorld)controller.level).getEntity(this.getDroneUUID());
                if(entity instanceof IDrone){
                    IDrone drone = (IDrone) entity;
                    drone.setPatrolling(!drone.isPatrolling());
                    return true;
                } else {
                    this.setDroneUUID(null);
                    return false;
                }
            }
        }
        return false;
    }

    default boolean isDeployed() {
        return this.getDroneNBT().isEmpty() && this.getDroneUUID() != null;
    }

    default boolean attachDrone(CompoundNBT droneNBT) {
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
