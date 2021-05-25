package com.infamous.captain_america.client;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.keybindings.CAKeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
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
}
