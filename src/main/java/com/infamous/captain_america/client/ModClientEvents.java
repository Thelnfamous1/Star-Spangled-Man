package com.infamous.captain_america.client;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.keybindings.CAKeyBinding;
import com.infamous.captain_america.client.layer.EXO7FalconLayer;
import com.infamous.captain_america.client.layer.MetalArmLayer;
import com.infamous.captain_america.client.renderer.MissileRenderer;
import com.infamous.captain_america.client.renderer.RedwingRenderer;
import com.infamous.captain_america.client.renderer.TimedGrenadeRenderer;
import com.infamous.captain_america.client.renderer.VibraniumShieldRenderer;
import com.infamous.captain_america.client.renderer.model.CARenderMaterial;
import com.infamous.captain_america.common.registry.EntityTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Map;

import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import net.minecraftforge.fmlclient.registry.RenderingRegistry;

@Mod.EventBusSubscriber(modid = CaptainAmerica.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void onStitch(TextureStitchEvent.Pre event) {
        if (event.getMap().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
            CaptainAmerica.LOGGER.info("Stitching shield textures!");
            event.addSprite(CARenderMaterial.VIBRANIUM_SHIELD.texture());
            event.addSprite(CARenderMaterial.CAPTAIN_AMERICA_SHIELD.texture());
            event.addSprite(CARenderMaterial.US_AGENT_SHIELD.texture());
            CaptainAmerica.LOGGER.info("Finished stitching shield textures!");
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onClientSetup(final FMLClientSetupEvent event){
        Minecraft minecraft = Minecraft.getInstance();
        EntityRenderDispatcher manager = minecraft.getEntityRenderDispatcher();
        addLayersToSkinMaps(manager);
        addLayersToBipeds(manager);
        registerKeyBindings();
        registerEntityRenderers();
        CAItemModelProperties.register();
    }

    private static void registerEntityRenderers() {
        CaptainAmerica.LOGGER.info("Registering entity renderers!");
        RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistry.BULLET.get(), (manager) -> new ThrownItemRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistry.MISSILE.get(), MissileRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistry.TIMED_GRENADE.get(), TimedGrenadeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistry.FALCON_REDWING.get(), RedwingRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistry.CAPTAIN_AMERICA_REDWING.get(), RedwingRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistry.CAPTAIN_AMERICA_SHIELD.get(), VibraniumShieldRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistry.VIBRANIUM_SHIELD.get(), VibraniumShieldRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistry.US_AGENT_SHIELD.get(), VibraniumShieldRenderer::new);
        CaptainAmerica.LOGGER.info("Finished registering entity renderers!");
    }

    private static void registerKeyBindings() {
        CaptainAmerica.LOGGER.info("Registering key bindings!");
        ClientRegistry.registerKeyBinding(CAKeyBinding.keyFlightAbility);
        ClientRegistry.registerKeyBinding(CAKeyBinding.keyDroneAbility);
        ClientRegistry.registerKeyBinding(CAKeyBinding.keyHUDAbility);
        ClientRegistry.registerKeyBinding(CAKeyBinding.keyVerticalFlight);
        ClientRegistry.registerKeyBinding(CAKeyBinding.keyVerticalFlightInverted);
        ClientRegistry.registerKeyBinding(CAKeyBinding.keyBoomerangThrowShield);
        ClientRegistry.registerKeyBinding(CAKeyBinding.keyRicochetThrowShield);
        ClientRegistry.registerKeyBinding(CAKeyBinding.keyToggleFlight);
        ClientRegistry.registerKeyBinding(CAKeyBinding.keyToggleHUD);
        CaptainAmerica.LOGGER.info("Finished registering key bindings!");
    }

    private static void addLayersToSkinMaps(EntityRenderDispatcher manager) {
        CaptainAmerica.LOGGER.info("Adding layers to skin maps!");
        Map<String, PlayerRenderer> skinMap = manager.getSkinMap();
        for(PlayerRenderer playerRenderer : skinMap.values()){
            boolean smallArms = skinMap.get("slim") == playerRenderer;
            playerRenderer.addLayer(new EXO7FalconLayer<>(playerRenderer));
            playerRenderer.addLayer(new MetalArmLayer<>(playerRenderer, new PlayerModel<>(0.0F, smallArms)));
            //playerRenderer.addLayer(new LaserLayer<>(playerRenderer));
        }
        CaptainAmerica.LOGGER.info("Finished adding layers to skin maps!");
    }

    @SuppressWarnings("unchecked")
    private static <T extends Mob, M extends HumanoidModel<T>> void addLayersToBipeds(EntityRenderDispatcher manager) {
        CaptainAmerica.LOGGER.info("Adding layers to bipeds!");
        for(EntityRenderer<?> entityRenderer : manager.renderers.values()){
            if(entityRenderer instanceof HumanoidMobRenderer){
                HumanoidMobRenderer<T, M> bipedRenderer = (HumanoidMobRenderer<T, M>) entityRenderer;
                bipedRenderer.addLayer(new MetalArmLayer<>(bipedRenderer, new HumanoidModel<>(0.0F)));
            }
        }
        CaptainAmerica.LOGGER.info("Finished adding layers to bipeds!");
    }

}
