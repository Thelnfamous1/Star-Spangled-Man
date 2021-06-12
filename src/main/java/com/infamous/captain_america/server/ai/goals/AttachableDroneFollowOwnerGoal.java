package com.infamous.captain_america.server.ai.goals;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.entity.drone.IAttachableDrone;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;

public class AttachableDroneFollowOwnerGoal<T extends MobEntity & IAttachableDrone> extends DroneFollowOwnerGoal<T>{
    private final T attachableDrone;

    public AttachableDroneFollowOwnerGoal(T attachableDroneMob, double speedModifier, float startDistance, float stopDistance, float teleportDistance, boolean canFly) {
        super(attachableDroneMob, speedModifier, startDistance, stopDistance, teleportDistance, canFly);
        this.attachableDrone = attachableDroneMob;
    }

    @Override
    protected void handleOwnerIntersection() {
        CaptainAmerica.LOGGER.info("Attempting to attach drone {} was attached to owner {} ", this.attachableDrone, this.owner);
        boolean attached = this.attachableDrone.attachDrone(this.owner);
        if(attached){
            CaptainAmerica.LOGGER.info("Drone {} was attached to owner {} ", this.attachableDrone, this.owner);
            if(this.owner instanceof ServerPlayerEntity){
                ((ServerPlayerEntity)this.owner).sendMessage(new TranslationTextComponent("action.redwing.attached"), Util.NIL_UUID);
            }
        }
    }
}
