package com.infamous.captain_america.client.network;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import com.infamous.captain_america.server.network.packet.SFlightBoostPacket;
import com.infamous.captain_america.server.network.packet.SFlightVerticalPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientNetworkHandler {

    public static void handleBoost(SFlightBoostPacket packet, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            ClientPlayerEntity clientPlayer = minecraft.player;
            if(clientPlayer == null){
                return;
            }
            FalconFlightHelper.boostFlight(clientPlayer);
            CaptainAmerica.LOGGER.info("Client player {} has boosted their EXO-7 Falcon flight!", clientPlayer.getDisplayName().getString());
        });
    }

    public static void handleFlyUp(SFlightVerticalPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->{
            Minecraft minecraft = Minecraft.getInstance();
            ClientPlayerEntity clientPlayer = minecraft.player;
            if(clientPlayer == null){
                return;
            }
            FalconFlightHelper.flyUp(clientPlayer);
            CaptainAmerica.LOGGER.info("Client player {} is flying up EXO-7 Falcon!", clientPlayer.getDisplayName().getString());
        });
    }
}
