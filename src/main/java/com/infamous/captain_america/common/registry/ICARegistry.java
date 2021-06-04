package com.infamous.captain_america.common.registry;

import com.infamous.captain_america.CaptainAmerica;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface ICARegistry<T extends IForgeRegistryEntry<T>> {

    String getRegistryTypeForLogger();

    DeferredRegister<T> getDeferredRegister();

    default void register(IEventBus modEventBus){
        CaptainAmerica.LOGGER.info(this.getStartedMessage());
        this.getDeferredRegister().register(modEventBus);
        CaptainAmerica.LOGGER.info(this.getFinishedMessage());
    }

    default String getFinishedMessage(){
        return "Registering " + this.getRegistryTypeForLogger() + "!";
    }

    default String getStartedMessage(){
        return "Finished registering " + this.getRegistryTypeForLogger() + "!";
    }
}
