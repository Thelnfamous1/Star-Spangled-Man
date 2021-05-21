package com.infamous.captain_america.client;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.items.EXO7FalconItem;
import com.infamous.captain_america.common.registry.ItemRegistry;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;

public class CAItemModelProperties {

    public static void register(){
        CaptainAmerica.LOGGER.info("Registering item model properties!");

        ItemModelsProperties.register(ItemRegistry.FALCONS_WINGSUIT.get(),
                new ResourceLocation("broken"),
                (itemStack, clientWorld, livingEntity) -> EXO7FalconItem.isFlightEnabled(itemStack) ? 0.0F : 1.0F);

        ItemModelsProperties.register(ItemRegistry.CAPTAIN_AMERICA_WINGSUIT.get(),
                new ResourceLocation("broken"),
                (itemStack, clientWorld, livingEntity) -> EXO7FalconItem.isFlightEnabled(itemStack) ? 0.0F : 1.0F);

        CaptainAmerica.LOGGER.info("Finished registering item model properties!");
    }
}