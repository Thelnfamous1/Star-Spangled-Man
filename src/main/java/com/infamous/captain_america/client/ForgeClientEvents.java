package com.infamous.captain_america.client;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.keybindings.CAKeyBinding;
import com.infamous.captain_america.client.network.packet.CFlightPacket;
import com.infamous.captain_america.client.util.CARenderHelper;
import com.infamous.captain_america.client.util.LaserBeamHelper;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.capability.metal_arm.IMetalArm;
import com.infamous.captain_america.common.capability.shield_thrower.IShieldThrower;
import com.infamous.captain_america.common.item.GogglesItem;
import com.infamous.captain_america.common.item.MetalArmItem;
import com.infamous.captain_america.common.item.VibraniumShieldItem;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.common.registry.EffectRegistry;
import com.infamous.captain_america.common.util.FalconAbilityKey;
import com.infamous.captain_america.common.util.FalconAbilityValue;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = CaptainAmerica.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEvents {

    private static boolean LOCAL_BOOSTING;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event){
        if(event.phase == TickEvent.Phase.END){
            Minecraft minecraft = Minecraft.getInstance();
            ClientPlayerEntity clientPlayer = minecraft.player;
            if(clientPlayer != null){
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
            }
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderXP(RenderGameOverlayEvent.Pre event){
        Minecraft minecraft = Minecraft.getInstance();
        PlayerEntity player = minecraft.player;
        if(player == null) return;
        renderShieldThrowMeter(event, player);
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

        ItemStack goggles = GogglesItem.getGoggles(livingRenderEntity);

        boolean hasHUDNightVision =
                livingRenderEntity.hasEffect(EffectRegistry.HUD_NIGHT_VISION.get())
                && !goggles.isEmpty()
                && GogglesItem.isHUDEnabled(goggles);

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
    public static void renderWorld(RenderWorldLastEvent event){
        List<AbstractClientPlayerEntity> players = null;
        if (Minecraft.getInstance().level != null) {
            players = Minecraft.getInstance().level.players();

            PlayerEntity localPlayer = Minecraft.getInstance().player;
            if(localPlayer != null){
                for (PlayerEntity player : players) {
                    if (player.distanceToSqr(localPlayer) > 500){
                        continue;
                    }

                    IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(player);
                    if (falconAbilityCap != null && falconAbilityCap.isShootingLaser()) {
                        LaserBeamHelper.renderBeam(event, player, Minecraft.getInstance().getFrameTime());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void preRenderPlayer(RenderPlayerEvent.Pre event){
        PlayerEntity player = event.getPlayer();
        IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(player);
        if (falconAbilityCap != null && falconAbilityCap.isShootingLaser()) {
            PlayerRenderer playerRenderer = event.getRenderer();
            PlayerModel<?> playerModel = playerRenderer.getModel();
            if(player.getMainArm() == HandSide.RIGHT){
                BipedModel.ArmPose rightArmPose = playerModel.rightArmPose;
                if(rightArmPose == BipedModel.ArmPose.EMPTY){
                    playerModel.rightArmPose = BipedModel.ArmPose.CROSSBOW_HOLD;
                }
            } else{
                BipedModel.ArmPose leftArmPose = playerModel.leftArmPose;
                if(leftArmPose == BipedModel.ArmPose.EMPTY){
                    playerModel.leftArmPose = BipedModel.ArmPose.CROSSBOW_HOLD;
                }
            }
        }
    }

}
