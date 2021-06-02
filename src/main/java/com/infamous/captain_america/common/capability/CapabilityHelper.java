package com.infamous.captain_america.common.capability;

import com.infamous.captain_america.common.capability.drone_controller.DroneControllerProvider;
import com.infamous.captain_america.common.capability.drone_controller.IDroneController;
import com.infamous.captain_america.common.capability.shield_thrower.IShieldThrower;
import com.infamous.captain_america.common.capability.shield_thrower.ShieldThrowerProvider;
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

    @Nullable
    public static IShieldThrower getShieldThrowerCap(LivingEntity entity)
    {
        LazyOptional<IShieldThrower> lazyCap = entity.getCapability(ShieldThrowerProvider.SHIELD_THROWER_CAPABILITY);
        if (lazyCap.isPresent()) {
            return lazyCap.orElseThrow(() -> new IllegalStateException("Couldn't get the shield thrower capability from " + entity + "!"));
        }
        return null;
    }
}
