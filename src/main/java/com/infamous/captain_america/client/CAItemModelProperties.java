package com.infamous.captain_america.client;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.item.EXO7FalconItem;
import com.infamous.captain_america.common.registry.ItemRegistry;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class CAItemModelProperties {

    public static void register(){
        CaptainAmerica.LOGGER.info("Registering item model properties!");

        ItemProperties.register(ItemRegistry.FALCON_WINGSUIT.get(),
                new ResourceLocation("broken"),
                (itemStack, clientWorld, livingEntity, var) -> EXO7FalconItem.isBroken(itemStack) ? 1.0F : 0.0F);

        ItemProperties.register(ItemRegistry.CAPTAIN_AMERICA_WINGSUIT.get(),
                new ResourceLocation("broken"),
                (itemStack, clientWorld, livingEntity, var) -> EXO7FalconItem.isBroken(itemStack) ? 1.0F : 0.0F);

        ItemProperties.register(ItemRegistry.CAPTAIN_AMERICA_SHIELD.get(),
                new ResourceLocation("blocking"),
                (stack, clientWorld, livingEntity, var) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == stack ? 1.0F : 0.0F);

        ItemProperties.register(ItemRegistry.VIBRANIUM_SHIELD.get(),
                new ResourceLocation("blocking"),
                (stack, clientWorld, livingEntity, var) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == stack ? 1.0F : 0.0F);

        ItemProperties.register(ItemRegistry.US_AGENT_SHIELD.get(),
                new ResourceLocation("blocking"),
                (stack, clientWorld, livingEntity, var) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == stack ? 1.0F : 0.0F);

        CaptainAmerica.LOGGER.info("Finished registering item model properties!");
    }
}
