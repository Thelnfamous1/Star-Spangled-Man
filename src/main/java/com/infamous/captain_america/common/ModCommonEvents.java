package com.infamous.captain_america.common;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.capability.RedwingController;
import com.infamous.captain_america.common.capability.DroneControllerStorage;
import com.infamous.captain_america.common.capability.IDroneController;
import com.infamous.captain_america.common.entity.RedwingEntity;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.common.registry.EntityTypeRegistry;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = CaptainAmerica.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCommonEvents {


    @SubscribeEvent
    public static void setup(final FMLCommonSetupEvent event)
    {
        NetworkHandler.init();
        CapabilityManager.INSTANCE.register(IDroneController.class, new DroneControllerStorage(), RedwingController::new);
    }

    @SubscribeEvent
    public static void createEntityAttributes(EntityAttributeCreationEvent event){
        event.put(EntityTypeRegistry.FALCON_REDWING.get(), RedwingEntity.createAttributes().build());
        event.put(EntityTypeRegistry.CAPTAIN_AMERICA_REDWING.get(), RedwingEntity.createAttributes().build());
    }
}