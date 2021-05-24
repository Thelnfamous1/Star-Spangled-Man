package com.infamous.captain_america.common;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.capability.DroneControllerProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CaptainAmerica.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeCommonEvents {

    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(new ResourceLocation(CaptainAmerica.MODID, "drone_controller"), new DroneControllerProvider());
        }
    }
}
