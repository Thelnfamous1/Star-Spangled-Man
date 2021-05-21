package com.infamous.captain_america.client;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.network.packet.CPropulsionPacket;
import com.infamous.captain_america.client.network.packet.CTakeoffPacket;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.common.util.FlightHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CaptainAmerica.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEventHandler {

    private static boolean jumpFired = false;

    @SubscribeEvent
    public static void handleFlightInputs(InputEvent.KeyInputEvent event){
        Minecraft minecraft = Minecraft.getInstance();
        ClientPlayerEntity clientPlayer = minecraft.player;
        if(clientPlayer == null){
            return;
        }
        boolean jumpDown = minecraft.options.keyJump.isDown();

        if(!jumpFired && jumpDown){
            jumpFired = true;

            if(FlightHelper.canTakeOff(clientPlayer) && canClientTakeOff(clientPlayer)){
                FlightHelper.takeOffFalconFlight(clientPlayer);
                CaptainAmerica.LOGGER.info("Player {} wants to take off using an EXO-7 Falcon!", clientPlayer);
                NetworkHandler.INSTANCE.sendToServer(new CTakeoffPacket());
           }
        } else {
            jumpFired = jumpDown;
             if(jumpDown && FlightHelper.canPropel(clientPlayer)){
                CaptainAmerica.LOGGER.info("Player {} wants to propel using an EXO-7 Falcon!", clientPlayer);
                FlightHelper.propelFalconFlight(clientPlayer);
                NetworkHandler.INSTANCE.sendToServer(new CPropulsionPacket());
            }
        }
    }

    private static boolean canClientTakeOff(ClientPlayerEntity clientPlayer){

        boolean canTriggerFlyAbility = false;
        if (clientPlayer.abilities.mayfly) {
            if (Minecraft.getInstance().gameMode.isAlwaysFlying()) {
                if (clientPlayer.abilities.flying) {
                    canTriggerFlyAbility = true;
                }
            } else {
                if (!clientPlayer.isSwimming()) {
                    canTriggerFlyAbility = true;
                }
            }
        }

        return !canTriggerFlyAbility
                && !clientPlayer.abilities.flying
                && !clientPlayer.isPassenger()
                && !clientPlayer.onClimbable();
    }
}
