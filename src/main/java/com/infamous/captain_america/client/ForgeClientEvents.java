package com.infamous.captain_america.client;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.keybindings.CAKeyBinding;
import com.infamous.captain_america.client.network.packet.CFlightPacket;
import com.infamous.captain_america.client.sound.HoverSound;
import com.infamous.captain_america.client.util.CARenderHelper;
import com.infamous.captain_america.client.util.LaserBeamHelper;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.capability.metal_arm.IMetalArm;
import com.infamous.captain_america.common.capability.shield_thrower.IShieldThrower;
import com.infamous.captain_america.common.entity.drone.IVisualLinker;
import com.infamous.captain_america.common.item.GogglesItem;
import com.infamous.captain_america.common.item.MetalArmItem;
import com.infamous.captain_america.common.item.VibraniumShieldItem;
import com.infamous.captain_america.common.item.gauntlet.AbstractGauntletItem;
import com.infamous.captain_america.common.item.gauntlet.ControlGauntletItem;
import com.infamous.captain_america.common.item.gauntlet.WeaponGauntletItem;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.common.registry.EffectRegistry;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = CaptainAmerica.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEvents {
    private static final ResourceLocation GOGGLES_OVERLAY = new ResourceLocation(CaptainAmerica.MODID, "textures/misc/goggles_overlay.png");

    public static PointOfView PREVIOUS_CONTROLLED_POV = PointOfView.FIRST_PERSON;
    public static boolean LOCAL_EAGLE_EYES;
    private static boolean LOCAL_BOOSTING;
    private static boolean LOCAL_HOVERING;
    public static boolean LOCAL_LASER;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event){
        if(event.phase == TickEvent.Phase.END){
            Minecraft minecraft = Minecraft.getInstance();
            ClientPlayerEntity clientPlayer = minecraft.player;
            if(clientPlayer != null){
                // HANDLE BOOST
                if(minecraft.options.keySprint.isDown() && FalconFlightHelper.canBoostFlight(clientPlayer)){
                    if(!LOCAL_BOOSTING){
                        LOCAL_BOOSTING = true;
                        //FalconFlightHelper.playFlightBoostSound(clientPlayer);
                        NetworkHandler.INSTANCE.sendToServer(new CFlightPacket(CFlightPacket.Action.TAKEOFF_FLIGHT));
                    }
                    CaptainAmerica.LOGGER.debug("Client player {} wants to boost their EXO-7 Falcon flight!", clientPlayer.getDisplayName().getString());
                    NetworkHandler.INSTANCE.sendToServer(new CFlightPacket(CFlightPacket.Action.BOOST_FLIGHT));
                } else{
                    LOCAL_BOOSTING = false;
                }
                // HANDLE HALT FLIGHT
                if(minecraft.options.keyShift.isDown()
                        && FalconFlightHelper.isFlying(clientPlayer)
                        && !AbstractGauntletItem.isHoldingThisInBothHands(clientPlayer)){
                    NetworkHandler.INSTANCE.sendToServer(new CFlightPacket(CFlightPacket.Action.HALT_FLIGHT));
                }

                // HANDLE HOVERING
                IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(clientPlayer);
                if(falconAbilityCap != null && falconAbilityCap.isHovering() && FalconFlightHelper.canHover(clientPlayer)){
                    if(!LOCAL_HOVERING){
                        LOCAL_HOVERING = true;
                        minecraft.getSoundManager().play(new HoverSound(clientPlayer));
                    }
                } else{
                    LOCAL_HOVERING = false;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onFOVEvent(FOVUpdateEvent event){
        PlayerEntity player = event.getEntity();
        if(player == Minecraft.getInstance().player){
            float newFOV = event.getNewfov();
            if(FalconFlightHelper.isFlying(player) && LOCAL_BOOSTING){
                newFOV *= 1.1F;
            }
            Optional<ItemStack> optionalGoggles = GogglesItem.getGoggles(player);
            if(optionalGoggles.isPresent()
                    && GogglesItem.isHUDEnabled(optionalGoggles.get())
                    && LOCAL_EAGLE_EYES){
                newFOV *= 0.25F;
            }
            event.setNewfov(newFOV);
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event){
        Minecraft minecraft = Minecraft.getInstance();
        ClientPlayerEntity clientPlayer = minecraft.player;
        if(clientPlayer != null){
            int key = event.getKey();
            CAKeyBinding.handleAllKeys(key, clientPlayer);
        }
    }

    @SubscribeEvent
    public void onRenderNameplate(RenderNameplateEvent event) {
        if (Minecraft.getInstance().getCameraEntity() instanceof IVisualLinker
                && event.getEntity() == Minecraft.getInstance().player) {
            if (Minecraft.getInstance().hasSingleplayerServer()) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderXP(RenderGameOverlayEvent.Pre event){
        Minecraft minecraft = Minecraft.getInstance();
        PlayerEntity player = minecraft.player;
        if(player == null) return;
        if(minecraft.getCameraEntity() instanceof IVisualLinker){
            event.setCanceled(true);
        } else{
            renderShieldThrowMeter(event, player);
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderFog(EntityViewRenderEvent.FogColors event){
        float fogRed = event.getRed();
        float fogGreen = event.getGreen();
        float fogBlue = event.getBlue();
        ActiveRenderInfo info = event.getInfo();

        Entity renderEntity = info.getEntity();
        if(!(renderEntity instanceof LivingEntity)) return;
        LivingEntity livingRenderEntity = (LivingEntity) renderEntity;

        IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(livingRenderEntity);
        if(falconAbilityCap == null) return;

        Optional<ItemStack> optionalGoggles = GogglesItem.getGoggles(livingRenderEntity);

        boolean hasHUDNightVision =
                livingRenderEntity.hasEffect(EffectRegistry.HUD_NIGHT_VISION.get())
                && optionalGoggles.isPresent()
                && GogglesItem.isHUDEnabled(optionalGoggles.get());

        if (hasHUDNightVision){
            float nightVisionScale = 1.0F; // Pretend we have an active NIGHT_VISION effect with a duration greater than 10 seconds
            float f10 = Math.min(1.0F / fogRed, Math.min(1.0F / fogGreen, 1.0F / fogBlue));
            // Forge: fix MC-4647 and MC-10480
            if (Float.isInfinite(f10)) f10 = Math.nextAfter(f10, 0.0);
            fogRed = fogRed * (1.0F - nightVisionScale) + fogRed * f10 * nightVisionScale;
            fogGreen = fogGreen * (1.0F - nightVisionScale) + fogGreen * f10 * nightVisionScale;
            fogBlue = fogBlue * (1.0F - nightVisionScale) + fogBlue * f10 * nightVisionScale;
            event.setRed(fogRed);
            event.setGreen(fogGreen);
            event.setBlue(fogBlue);
        }
    }

    private static void renderShieldThrowMeter(RenderGameOverlayEvent.Pre event, PlayerEntity player) {
        IShieldThrower shieldThrowerCap = CapabilityHelper.getShieldThrowerCap(player);
        if (shieldThrowerCap != null
                && shieldThrowerCap.getShieldChargingScale() > 0.0F
                && VibraniumShieldItem.hasVibraniumShield(player)
                && shouldReplaceElement(event)) {
            event.setCanceled(true);
            CARenderHelper.renderShieldThrowMeter(shieldThrowerCap, event.getMatrixStack());
        }
    }

    private static boolean shouldReplaceElement(RenderGameOverlayEvent event) {
        return event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE
                || event.getType() == RenderGameOverlayEvent.ElementType.JUMPBAR;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void postRenderLiving(RenderLivingEvent.Post<?, ?> event){
        LivingEntity living = event.getEntity();
        LivingRenderer<?, ?> livingRenderer = event.getRenderer();
        if(livingRenderer.getModel() instanceof BipedModel){
            BipedModel<?> bipedModel = (BipedModel<?>) livingRenderer.getModel();
            IMetalArm metalArmCap = CapabilityHelper.getMetalArmCap(living);
            if(metalArmCap != null){
                ItemStack leftArmStack = getStackForHandSide(living, HandSide.LEFT, metalArmCap);
                ItemStack rightArmStack = getStackForHandSide(living, HandSide.RIGHT, metalArmCap);

                if(bipedModel.leftArm.visible && MetalArmItem.isMetalArmStack(leftArmStack)){
                    bipedModel.leftArm.visible = false; // only render the arm from the metal arm layer
                }
                if(bipedModel.rightArm.visible && MetalArmItem.isMetalArmStack(rightArmStack)){
                    bipedModel.rightArm.visible = false; // only render the arm from the metal arm layer
                }
            }
        }
    }

    private static ItemStack getStackForHandSide(LivingEntity living, HandSide handSide, IMetalArm metalArmCap) {
        HandSide mainSide = living.getMainArm();
        ItemStack metalArmStack;
        if(mainSide == handSide){
            metalArmStack = metalArmCap.getMetalArmMainHand();
        } else{
            metalArmStack = metalArmCap.getMetalArmOffHand();
        }
        return metalArmStack;
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if (Minecraft.getInstance().getCameraEntity() instanceof IVisualLinker) {
            event.setCanceled(true);
        }
    }


    @SubscribeEvent
    public static void renderWorld(RenderWorldLastEvent event){
        if (Minecraft.getInstance().level != null) {
            List<AbstractClientPlayerEntity> players = Minecraft.getInstance().level.players();

            ClientPlayerEntity localPlayer = Minecraft.getInstance().player;
            if(localPlayer != null){
                handleLaserRendering(event, players, localPlayer);
                handleRedwingCamera(localPlayer);
                if(GogglesItem.getGoggles(localPlayer).isPresent()){
                    renderGogglesOverlay(event, localPlayer);
                }
            }


        }
    }

    private static void renderGogglesOverlay(RenderWorldLastEvent event, ClientPlayerEntity localPlayer) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.enableTexture();
        minecraft.getTextureManager().bind(GOGGLES_OVERLAY);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // These values follow the values used in OverlayRenderer#renderWater
        float scaledNegYRot = -localPlayer.yRot / 64.0F;
        float scaledXRot = localPlayer.xRot / 64.0F;

        final float brightness = 1.0F;
        final float alpha = 0.5F;

        Matrix4f matrix4f = event.getMatrixStack().last().pose();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        bufferBuilder.vertex(-1.0F, -1.0F, -0.5F).color(brightness, brightness, brightness, alpha).uv(4.0F + scaledNegYRot, 4.0F + scaledXRot).endVertex();
        bufferBuilder.vertex(1.0F, -1.0F, -0.5F).color(brightness, brightness, brightness, alpha).uv(0.0F + scaledNegYRot, 4.0F + scaledXRot).endVertex();
        bufferBuilder.vertex(1.0F, 1.0F, -0.5F).color(brightness, brightness, brightness, alpha).uv(0.0F + scaledNegYRot, 0.0F + scaledXRot).endVertex();
        bufferBuilder.vertex(-1.0F, 1.0F, -0.5F).color(brightness, brightness, brightness, alpha).uv(4.0F + scaledNegYRot, 0.0F + scaledXRot).endVertex();
        bufferBuilder.end();
        WorldVertexBufferUploader.end(bufferBuilder);
        RenderSystem.disableBlend();
    }

    private static void handleRedwingCamera(ClientPlayerEntity localPlayer) {
        Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
        if (cameraEntity instanceof IVisualLinker) {
            IVisualLinker visualLinker = (IVisualLinker) cameraEntity;
            boolean shouldResetCamera =
                    !visualLinker.hasVisualLink()
                            || !cameraEntity.isAlive()
                            || !ControlGauntletItem.isHoldingThis(localPlayer)
                            || !GogglesItem.getGoggles(localPlayer).isPresent();
            if (shouldResetCamera && !localPlayer.isSpectator()) {
                Minecraft.getInstance().setCameraEntity(localPlayer);
                if(PREVIOUS_CONTROLLED_POV != null){
                    PointOfView previousControlledPOV = PointOfView.values()[PREVIOUS_CONTROLLED_POV.ordinal()];
                    Minecraft.getInstance().options.setCameraType(previousControlledPOV);
                }
            }
        }
    }

    private static void handleLaserRendering(RenderWorldLastEvent event, List<AbstractClientPlayerEntity> players, ClientPlayerEntity localPlayer) {
        for (PlayerEntity player : players) {
            if (player.distanceToSqr(localPlayer) > 500){
                continue;
            }

            if (WeaponGauntletItem.isStackOfThis(player.getUseItem()) && LOCAL_LASER) {
                LaserBeamHelper.renderBeam(event, player, Minecraft.getInstance().getFrameTime());
            }
        }
    }

    @SubscribeEvent
    public static void preRenderPlayer(RenderPlayerEvent.Pre event){
        PlayerEntity player = event.getPlayer();
        ItemStack useItem = player.getUseItem();
        Hand useHand = player.getUsedItemHand();
        boolean notSwinging = !player.swinging;
        boolean isUsingGauntlet = AbstractGauntletItem.isStackOfThis(useItem);
        if (isUsingGauntlet && notSwinging) {
            PlayerRenderer playerRenderer = event.getRenderer();
            PlayerModel<?> playerModel = playerRenderer.getModel();
            if(player.getMainArm() == HandSide.RIGHT){
                if(useHand == Hand.MAIN_HAND){
                    playerModel.rightArmPose = BipedModel.ArmPose.CROSSBOW_HOLD;
                } else{
                    playerModel.leftArmPose = BipedModel.ArmPose.CROSSBOW_HOLD;
                }
            } else{
                if(useHand == Hand.MAIN_HAND){
                    playerModel.leftArmPose = BipedModel.ArmPose.CROSSBOW_HOLD;
                } else{
                    playerModel.rightArmPose = BipedModel.ArmPose.CROSSBOW_HOLD;
                }
            }
        }
    }

}
