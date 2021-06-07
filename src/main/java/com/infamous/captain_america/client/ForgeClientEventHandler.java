package com.infamous.captain_america.client;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.keybindings.CAKeyBinding;
import com.infamous.captain_america.client.network.packet.CFlightPacket;
import com.infamous.captain_america.client.util.RenderHelper;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.shield_thrower.IShieldThrower;
import com.infamous.captain_america.common.item.VibraniumShieldItem;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ElytraSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CaptainAmerica.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEventHandler {

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
}
