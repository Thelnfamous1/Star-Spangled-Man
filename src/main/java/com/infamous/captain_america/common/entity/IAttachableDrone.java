package com.infamous.captain_america.common.entity;

import net.minecraft.entity.LivingEntity;

public interface IAttachableDrone extends IDrone{

    boolean attachDrone(LivingEntity living);
}
