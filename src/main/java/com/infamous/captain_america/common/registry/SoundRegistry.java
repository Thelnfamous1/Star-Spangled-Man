package com.infamous.captain_america.common.registry;

import com.infamous.captain_america.CaptainAmerica;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundRegistry implements IRegistryManager<SoundEvent> {

    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CaptainAmerica.MODID);

    public static final RegistryObject<SoundEvent> FLIGHT_BOOST = SOUND_EVENTS.register("flight_boost",
            () -> new SoundEvent(new ResourceLocation(CaptainAmerica.MODID, "flight_boost")));


    @Override
    public String getRegistryTypeForLogger() {
        return "sounds";
    }

    @Override
    public DeferredRegister<SoundEvent> getDeferredRegister() {
        return SOUND_EVENTS;
    }
}
