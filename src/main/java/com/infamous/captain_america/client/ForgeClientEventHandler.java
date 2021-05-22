package com.infamous.captain_america.client;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.network.packet.CFlightHaltPacket;
import com.infamous.captain_america.client.network.packet.CFlightBoostPacket;
import com.infamous.captain_america.client.network.packet.CFlightTakeoffPacket;
import com.infamous.captain_america.client.network.packet.CFlightVerticalPacket;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CaptainAmerica.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEventHandler {

    private static boolean jumpFired = false;
    private static boolean sprintFired = false;
    private static boolean shiftFired = false;


    @SubscribeEvent
    public static void handleFlightInputs(InputEvent.KeyInputEvent event){
        Minecraft minecraft = Minecraft.getInstance();
        ClientPlayerEntity clientPlayer = minecraft.player;
        if(clientPlayer == null){
            return;
        }
        boolean jumpKeyDown = minecraft.options.keyJump.isDown();
        boolean sprintKeyDown = minecraft.options.keySprint.isDown();
        boolean shiftKeyDown = minecraft.options.keyShift.isDown();

        boolean flyUpInput = !jumpFired && jumpKeyDown;
        boolean takeOffInput = !sprintFired && sprintKeyDown;
        boolean haltInput = !shiftFired && shiftKeyDown;

        if(takeOffInput){
            sprintFired = true;
            if(!haltInput && FalconFlightHelper.canTakeOff(clientPlayer) && canClientTakeOff(clientPlayer)){
                FalconFlightHelper.takeOff(clientPlayer);
                CaptainAmerica.LOGGER.info("Client player {} wants to take off using an EXO-7 Falcon!", clientPlayer.getDisplayName().getString());
                NetworkHandler.INSTANCE.sendToServer(new CFlightTakeoffPacket());
           }
        } else {
            sprintFired = shiftKeyDown;
             if(!haltInput && sprintKeyDown && FalconFlightHelper.canBoostFlight(clientPlayer)){
                CaptainAmerica.LOGGER.info("Client player {} wants to boost their EXO-7 Falcon flight!", clientPlayer.getDisplayName().getString());
                //FlightHelper.propelFalconFlight(clientPlayer);
                NetworkHandler.INSTANCE.sendToServer(new CFlightBoostPacket());
            }
        }

        if(haltInput){
            shiftFired = true;
            if(FalconFlightHelper.isFlying(clientPlayer)){
                CaptainAmerica.LOGGER.info("Client player {} has halted their EXO-7 Falcon flight!", clientPlayer.getDisplayName().getString());
                FalconFlightHelper.haltFlight(clientPlayer);
                NetworkHandler.INSTANCE.sendToServer(new CFlightHaltPacket());
            }
        } else {
            shiftFired = shiftKeyDown;
        }

        if(flyUpInput){
            jumpFired = true;
        } else {
            jumpFired = jumpKeyDown;
            if(jumpKeyDown && FalconFlightHelper.canFlyUp(clientPlayer) && canClientFlyUp(clientPlayer)){
                CaptainAmerica.LOGGER.info("Client player {} wants to fly up using an EXO-7 Falcon!", clientPlayer.getDisplayName().getString());
                NetworkHandler.INSTANCE.sendToServer(new CFlightVerticalPacket());
            }
        }
    }

    private static boolean canClientTakeOff(ClientPlayerEntity clientPlayer){
        /*
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

         */

        return //!canTriggerFlyAbility&&
                !clientPlayer.abilities.flying
                && !clientPlayer.isPassenger()
                && !clientPlayer.onClimbable();
    }

    private static boolean canClientFlyUp(ClientPlayerEntity clientPlayer){
        return !clientPlayer.abilities.flying
                && !clientPlayer.isPassenger()
                && !clientPlayer.onClimbable();
    }
}
