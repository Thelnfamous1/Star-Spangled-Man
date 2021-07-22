package com.infamous.captain_america.common.entity.drone;

import net.minecraft.world.entity.LivingEntity;

public interface IAttachableDrone extends IDrone {

    boolean attachDrone(LivingEntity living);
}
