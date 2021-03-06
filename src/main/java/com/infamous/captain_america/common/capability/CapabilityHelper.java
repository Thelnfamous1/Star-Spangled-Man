package com.infamous.captain_america.common.capability;

import com.infamous.captain_america.common.capability.drone_controller.DroneControllerProvider;
import com.infamous.captain_america.common.capability.drone_controller.IDroneController;
import com.infamous.captain_america.common.capability.falcon_ability.FalconAbilityProvider;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.capability.metal_arm.IMetalArm;
import com.infamous.captain_america.common.capability.metal_arm.MetalArmProvider;
import com.infamous.captain_america.common.capability.shield_thrower.IShieldThrower;
import com.infamous.captain_america.common.capability.shield_thrower.ShieldThrowerProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class CapabilityHelper {

    @Nullable
    public static IDroneController getDroneControllerCap(Entity entity)
    {
        LazyOptional<IDroneController> lazyCap = entity.getCapability(DroneControllerProvider.DRONE_CONTROLLER_CAPABILITY);
        if (lazyCap.isPresent()) {
            return lazyCap.orElseThrow(() -> new IllegalStateException("Couldn't get the drone controller capability from " + entity + "!"));
        }
        return null;
    }

    @Nullable
    public static IMetalArm getMetalArmCap(Entity entity){
        LazyOptional<IMetalArm> lazyCap = entity.getCapability(MetalArmProvider.METAL_ARM_CAPABILITY);
        if (lazyCap.isPresent()) {
            return lazyCap.orElseThrow(() -> new IllegalStateException("Couldn't get the metal arm capability from " + entity + "!"));
        }
        return null;
    }

    @Nullable
    public static IShieldThrower getShieldThrowerCap(Entity entity)
    {
        LazyOptional<IShieldThrower> lazyCap = entity.getCapability(ShieldThrowerProvider.SHIELD_THROWER_CAPABILITY);
        if (lazyCap.isPresent()) {
            return lazyCap.orElseThrow(() -> new IllegalStateException("Couldn't get the shield thrower capability from " + entity + "!"));
        }
        return null;
    }

    @Nullable
    public static IFalconAbility getFalconAbilityCap(Entity entity)
    {
        LazyOptional<IFalconAbility> lazyCap = entity.getCapability(FalconAbilityProvider.FALCON_ABILITY_CAPABILITY);
        if (lazyCap.isPresent()) {
            return lazyCap.orElseThrow(() -> new IllegalStateException("Couldn't get the falcon ability capability from " + entity + "!"));
        }
        return null;
    }
}
