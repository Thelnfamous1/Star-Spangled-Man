package com.infamous.captain_america.common.registry;

import com.infamous.captain_america.CaptainAmerica;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundRegistry {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CaptainAmerica.MODID);

    public static final RegistryObject<SoundEvent> FLIGHT_BOOST = SOUND_EVENTS.register("flight_boost",
            () -> new SoundEvent(new ResourceLocation(CaptainAmerica.MODID, "flight_boost")));


    public static void register(IEventBus modBusEvent) {
        CaptainAmerica.LOGGER.info("Registering sounds!");
        SOUND_EVENTS.register(modBusEvent);
        CaptainAmerica.LOGGER.info("Finished registering sounds!");
    }
}
