package com.infamous.captain_america.client;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.keybindings.CAKeyBinding;
import com.infamous.captain_america.client.layer.EXO7FalconLayer;
import com.infamous.captain_america.client.renderer.RedwingRenderer;
import com.infamous.captain_america.client.renderer.model.VibraniumShieldTextures;
import com.infamous.captain_america.client.renderer.VibraniumShieldRenderer;
import com.infamous.captain_america.common.registry.EntityTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Map;

@Mod.EventBusSubscriber(modid = CaptainAmerica.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEventHandler {

    @SubscribeEvent
    public static void onStitch(TextureStitchEvent.Pre event) {
        if (event.getMap().location().equals(AtlasTexture.LOCATION_BLOCKS)) {
            event.addSprite(VibraniumShieldTextures.VIBRANIUM_SHIELD.texture());
            event.addSprite(VibraniumShieldTextures.CAPTAIN_AMERICA_SHIELD.texture());
        }
    }

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event){
        addEXO7FalconLayers();
        registerKeyBindings();
        registerEntityRenderers();
        CAItemModelProperties.register();
    }

    private static void registerEntityRenderers() {
        CaptainAmerica.LOGGER.info("Registering entity renderers!");
        RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistry.FALCON_REDWING.get(), RedwingRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistry.CAPTAIN_AMERICA_REDWING.get(), RedwingRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistry.CAPTAIN_AMERICA_SHIELD.get(), VibraniumShieldRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistry.VIBRANIUM_SHIELD.get(), VibraniumShieldRenderer::new);
        CaptainAmerica.LOGGER.info("Finished registering entity renderers!");
    }

    private static void registerKeyBindings() {
        CaptainAmerica.LOGGER.info("Registering key bindings!");
        ClientRegistry.registerKeyBinding(CAKeyBinding.keyHover);
        ClientRegistry.registerKeyBinding(CAKeyBinding.keyBoostFlight);
        ClientRegistry.registerKeyBinding(CAKeyBinding.keyHaltFlight);
        ClientRegistry.registerKeyBinding(CAKeyBinding.keyDeployRedwing);
        ClientRegistry.registerKeyBinding(CAKeyBinding.keyToggleRedwingRecall);
        ClientRegistry.registerKeyBinding(CAKeyBinding.keyTogglePatrolRedwing);
        ClientRegistry.registerKeyBinding(CAKeyBinding.keyBoomerangThrowShield);
        ClientRegistry.registerKeyBinding(CAKeyBinding.keyRicochetThrowShield);
        CaptainAmerica.LOGGER.info("Finished registering key bindings!");
    }

    private static void addEXO7FalconLayers() {
        CaptainAmerica.LOGGER.info("Adding EXO-7 Falcon Layer to skin maps!");
        Minecraft minecraft = Minecraft.getInstance();
        EntityRendererManager manager = minecraft.getEntityRenderDispatcher();
        Map<String, PlayerRenderer> skinMap = manager.getSkinMap();
        for(PlayerRenderer playerRenderer : skinMap.values()){
            playerRenderer.addLayer(new EXO7FalconLayer<>(playerRenderer));
        }
        CaptainAmerica.LOGGER.info("Finished adding EXO-7 Falcon Layer to skin maps!");
    }

}
