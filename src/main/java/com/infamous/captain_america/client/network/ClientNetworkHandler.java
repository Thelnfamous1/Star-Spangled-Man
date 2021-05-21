package com.infamous.captain_america.client.network;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.network.packet.CPropulsionPacket;
import com.infamous.captain_america.client.network.packet.CTakeoffPacket;
import com.infamous.captain_america.common.util.FlightHelper;
import com.infamous.captain_america.server.network.packet.SPropulsionPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientNetworkHandler {

    public static void handlePropulsion(SPropulsionPacket packet, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            ClientPlayerEntity clientPlayer = minecraft.player;
            if(clientPlayer == null){
                return;
            }
            FlightHelper.propelFalconFlight(clientPlayer);
            CaptainAmerica.LOGGER.info("Client player {} has propelled with an EXO-7 Falcon!", clientPlayer.getDisplayName().getString());
        });
    }
}
