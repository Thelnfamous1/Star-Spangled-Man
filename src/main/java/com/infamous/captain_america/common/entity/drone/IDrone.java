package com.infamous.captain_america.common.entity.drone;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IDrone {

    default void readDroneNBT(CompoundTag compoundNBT) {
        UUID uuid = null;
        if (compoundNBT.hasUUID("Owner")) {
            uuid = compoundNBT.getUUID("Owner");
        }

        if (uuid != null) {
            this.setOwnerUUID(uuid);
            this.setOwned(true);
        }
    }

    default void addDroneNBT(CompoundTag compoundNBT) {
        if (this.getOwnerUUID() != null) {
            compoundNBT.putUUID("Owner", this.getOwnerUUID());
        }
    }

    boolean isOwned();

    void setOwned(boolean tame);

    boolean isPatrolling();

    void setPatrolling(boolean patrolling);

    boolean isRecalled();

    void setRecalled(boolean recalled);

    @Nullable
    UUID getOwnerUUID();

    void setOwnerUUID(@Nullable UUID ownerUUID);

    default void own(LivingEntity living) {
        this.setOwned(true);
        this.setOwnerUUID(living.getUUID());
    }

    @Nullable
    LivingEntity getOwner();

    default boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (target instanceof Wolf) {
            Wolf wolfentity = (Wolf)target;
            return !wolfentity.isTame() || wolfentity.getOwner() != owner;
        } else if (target instanceof Player && owner instanceof Player && !((Player)owner).canHarmPlayer((Player)target)) {
            return false;
        } else if (target instanceof AbstractHorse && ((AbstractHorse)target).isTamed()) {
            return false;
        } else if(target instanceof TamableAnimal){
            return !((TamableAnimal)target).isTame();
        } else{
            return !(target instanceof IDrone) || ((IDrone)target).isOwned();
        }
    }

    default boolean isOwnedBy(LivingEntity living) {
        return living == this.getOwner();
    }
}
