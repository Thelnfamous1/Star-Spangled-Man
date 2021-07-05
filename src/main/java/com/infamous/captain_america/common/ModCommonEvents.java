package com.infamous.captain_america.common;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.advancements.CACriteriaTriggers;
import com.infamous.captain_america.common.capability.drone_controller.DroneControllerStorage;
import com.infamous.captain_america.common.capability.drone_controller.IDroneController;
import com.infamous.captain_america.common.capability.drone_controller.RedwingController;
import com.infamous.captain_america.common.capability.metal_arm.IMetalArm;
import com.infamous.captain_america.common.capability.metal_arm.MetalArm;
import com.infamous.captain_america.common.capability.metal_arm.MetalArmStorage;
import com.infamous.captain_america.common.capability.shield_thrower.IShieldThrower;
import com.infamous.captain_america.common.capability.shield_thrower.ShieldThrower;
import com.infamous.captain_america.common.capability.shield_thrower.ShieldThrowerStorage;
import com.infamous.captain_america.common.entity.drone.RedwingEntity;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.common.registry.EffectRegistry;
import com.infamous.captain_america.common.registry.EntityTypeRegistry;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = CaptainAmerica.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCommonEvents {


    @SubscribeEvent
    public static void setup(final FMLCommonSetupEvent event)
    {
        DeferredWorkQueue.runLater(
                CACriteriaTriggers::init
        );
        DeferredWorkQueue.runLater(
                NetworkHandler::init
        );
        CapabilityManager.INSTANCE.register(IDroneController.class, new DroneControllerStorage(), RedwingController::new);
        CapabilityManager.INSTANCE.register(IShieldThrower.class, new ShieldThrowerStorage(), ShieldThrower::new);
        CapabilityManager.INSTANCE.register(IMetalArm.class, new MetalArmStorage(), MetalArm::new);
    }

    @SubscribeEvent
    public static void createEntityAttributes(EntityAttributeCreationEvent event){
        event.put(EntityTypeRegistry.FALCON_REDWING.get(), RedwingEntity.createAttributes().build());
        event.put(EntityTypeRegistry.CAPTAIN_AMERICA_REDWING.get(), RedwingEntity.createAttributes().build());
    }

}