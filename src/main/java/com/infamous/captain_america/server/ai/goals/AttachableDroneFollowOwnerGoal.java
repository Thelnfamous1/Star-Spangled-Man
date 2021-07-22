package com.infamous.captain_america.server.ai.goals;

import com.infamous.captain_america.common.entity.drone.IAttachableDrone;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;

public class AttachableDroneFollowOwnerGoal<T extends Mob & IAttachableDrone> extends DroneFollowOwnerGoal<T>{
    private final T attachableDrone;

    public AttachableDroneFollowOwnerGoal(T attachableDroneMob, double speedModifier, float startDistance, float stopDistance, float teleportDistance, boolean canFly) {
        super(attachableDroneMob, speedModifier, startDistance, stopDistance, teleportDistance, canFly);
        this.attachableDrone = attachableDroneMob;
    }

    @Override
    protected void handleOwnerIntersection() {
        //CaptainAmerica.LOGGER.info("Attempting to attach drone {} was attached to owner {} ", this.attachableDrone, this.owner);
        boolean attached = this.attachableDrone.attachDrone(this.owner);
        if(attached){
            //CaptainAmerica.LOGGER.info("Drone {} was attached to owner {} ", this.attachableDrone, this.owner);
            if(this.owner instanceof ServerPlayer){
                ((ServerPlayer)this.owner).sendMessage(new TranslatableComponent("action.redwing.attached").withStyle(ChatFormatting.RED), Util.NIL_UUID);
            }
        }
    }
}
