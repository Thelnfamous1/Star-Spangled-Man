package com.infamous.captain_america.client;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.keybindings.CAKeyBinding;
import com.infamous.captain_america.client.network.packet.CFlightPacket;
import com.infamous.captain_america.client.util.RenderHelper;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.metal_arm.IMetalArm;
import com.infamous.captain_america.common.capability.shield_thrower.IShieldThrower;
import com.infamous.captain_america.common.item.MetalArmItem;
import com.infamous.captain_america.common.item.VibraniumShieldItem;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ElytraSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CaptainAmerica.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEvents {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event){
        CAKeyBinding.handleAllKeys(event.getKey());
    }

    @SubscribeEvent
    public static void onRenderXP(RenderGameOverlayEvent event){
        Minecraft minecraft = Minecraft.getInstance();
        PlayerEntity player = minecraft.player;
        if(player == null) return;
        IShieldThrower shieldThrowerCap = CapabilityHelper.getShieldThrowerCap(player);
        if (shieldThrowerCap != null
                && shieldThrowerCap.getShieldChargingScale() > 0.0F
                && VibraniumShieldItem.hasVibraniumShield(player)
                && shouldReplaceElement(event)) {
            event.setCanceled(true);
            RenderHelper.renderShieldThrowMeter(shieldThrowerCap, event.getMatrixStack());
        }
    }

    private static boolean shouldReplaceElement(RenderGameOverlayEvent event) {
        return event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE
                || event.getType() == RenderGameOverlayEvent.ElementType.JUMPBAR;
    }

    @SubscribeEvent
    public static void onElytraSoundPlayed(PlaySoundEvent event){
        if(event.getSound() instanceof ElytraSound){
            ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
            if(clientPlayer == null) return;

            if (FalconFlightHelper.canBoostFlight(clientPlayer)) {
                FalconFlightHelper.playFlightBoostSound(clientPlayer);
                NetworkHandler.INSTANCE.sendToServer(new CFlightPacket(CFlightPacket.Action.TAKEOFF_FLIGHT));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderLiving(RenderLivingEvent.Post<LivingEntity, EntityModel<LivingEntity>> event){
        LivingEntity living = event.getEntity();
        LivingRenderer<LivingEntity, EntityModel<LivingEntity>> livingRenderer = event.getRenderer();
        if(livingRenderer.getModel() instanceof BipedModel){
            BipedModel<LivingEntity> bipedModel = (BipedModel<LivingEntity>) livingRenderer.getModel();
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

}
