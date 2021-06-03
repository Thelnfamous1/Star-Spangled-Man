package com.infamous.captain_america.common.entity.drone;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IDrone {

    default void readDroneNBT(CompoundNBT compoundNBT) {
        UUID uuid = null;
        if (compoundNBT.hasUUID("Owner")) {
            uuid = compoundNBT.getUUID("Owner");
        }

        if (uuid != null) {
            this.setOwnerUUID(uuid);
            this.setOwned(true);
        }
    }

    default void addDroneNBT(CompoundNBT compoundNBT) {
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
        if (target instanceof WolfEntity) {
            WolfEntity wolfentity = (WolfEntity)target;
            return !wolfentity.isTame() || wolfentity.getOwner() != owner;
        } else if (target instanceof PlayerEntity && owner instanceof PlayerEntity && !((PlayerEntity)owner).canHarmPlayer((PlayerEntity)target)) {
            return false;
        } else if (target instanceof AbstractHorseEntity && ((AbstractHorseEntity)target).isTamed()) {
            return false;
        } else if(target instanceof TameableEntity){
            return !((TameableEntity)target).isTame();
        } else{
            return !(target instanceof IDrone) || ((IDrone)target).isOwned();
        }
    }

    default boolean isOwnedBy(LivingEntity living) {
        return living == this.getOwner();
    }
}
