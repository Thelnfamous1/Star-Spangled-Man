package com.infamous.captain_america.client;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.keybindings.CAKeyBinding;
import com.infamous.captain_america.client.util.RenderHelper;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.shield_thrower.IShieldThrower;
import com.infamous.captain_america.common.item.VibraniumShieldItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CaptainAmerica.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEventHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event){
        if(event.getKey() == CAKeyBinding.keyHover.getKey().getValue()){
            CAKeyBinding.keyHover.handleKey();
        }
        if(event.getKey() == CAKeyBinding.keyHaltFlight.getKey().getValue()){
            CAKeyBinding.keyHaltFlight.handleKey();
        }
        if(event.getKey() == CAKeyBinding.keyBoostFlight.getKey().getValue()){
            CAKeyBinding.keyBoostFlight.handleKey();
        }
        if(event.getKey() == CAKeyBinding.keyDeployRedwing.getKey().getValue()){
            CAKeyBinding.keyDeployRedwing.handleKey();
        }
        if(event.getKey() == CAKeyBinding.keyToggleRedwingRecall.getKey().getValue()){
            CAKeyBinding.keyToggleRedwingRecall.handleKey();
        }
        if(event.getKey() == CAKeyBinding.keyTogglePatrolRedwing.getKey().getValue()){
            CAKeyBinding.keyTogglePatrolRedwing.handleKey();
        }
        if(event.getKey() == CAKeyBinding.keyBoomerangThrowShield.getKey().getValue()){
            CAKeyBinding.keyBoomerangThrowShield.handleKey();
        }
        if(event.getKey() == CAKeyBinding.keyRicochetThrowShield.getKey().getValue()){
            CAKeyBinding.keyRicochetThrowShield.handleKey();
        }
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
}
