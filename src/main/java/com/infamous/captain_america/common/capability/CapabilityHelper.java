package com.infamous.captain_america.common.capability;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class CapabilityHelper {

    @Nullable
    public static IDroneController getDroneControllerCap(LivingEntity entity)
    {
        LazyOptional<IDroneController> lazyCap = entity.getCapability(DroneControllerProvider.DRONE_CONTROLLER_CAPABILITY);
        if (lazyCap.isPresent()) {
            return lazyCap.orElseThrow(() -> new IllegalStateException("Couldn't get the drone controller capability from " + entity + "!"));
        }
        return null;
    }
}
