package com.infamous.captain_america.client;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.layers.EXO7FalconLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Map;

@Mod.EventBusSubscriber(modid = CaptainAmerica.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEventHandler {

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event){
        addEXO7FalconLayers();
        CAItemModelProperties.register();
    }

    private static void addEXO7FalconLayers() {
        Minecraft minecraft = Minecraft.getInstance();
        EntityRendererManager manager = minecraft.getEntityRenderDispatcher();
        Map<String, PlayerRenderer> skinMap = manager.getSkinMap();
        for(PlayerRenderer playerRenderer : skinMap.values()){
            playerRenderer.addLayer(new EXO7FalconLayer<>(playerRenderer));
        }
    }

}
