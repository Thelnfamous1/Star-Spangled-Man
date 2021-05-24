package com.infamous.captain_america.client.network;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import com.infamous.captain_america.server.network.packet.SFlightPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientNetworkHandler {

    public static void handleFlight(SFlightPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->{
            Minecraft minecraft = Minecraft.getInstance();
            ClientPlayerEntity clientPlayer = minecraft.player;
            if(clientPlayer == null){
                return;
            }
            switch (packet.getAction()){
                case TAKEOFF_FLIGHT:
                    FalconFlightHelper.playFlightBoostSound(clientPlayer);
                    CaptainAmerica.LOGGER.debug("Client player {} has taken off using an EXO-7 Falcon!", clientPlayer.getDisplayName().getString());
                    break;
                case BOOST_FLIGHT:
                    FalconFlightHelper.boostFlight(clientPlayer);
                    CaptainAmerica.LOGGER.debug("Client player {} has boosted their EXO-7 Falcon flight!", clientPlayer.getDisplayName().getString());
                    break;
                case HOVER:
                    FalconFlightHelper.hover(clientPlayer);
                    CaptainAmerica.LOGGER.debug("Client player {} is hovering using an EXO-7 Falcon!", clientPlayer.getDisplayName().getString());
                    break;
            }
        });
    }
}
