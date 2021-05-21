package com.infamous.captain_america.server;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.network.packet.CPropulsionPacket;
import com.infamous.captain_america.client.network.packet.CTakeoffPacket;
import com.infamous.captain_america.common.util.FlightHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerNetworkHandler {

    public static void handleTakeoff(CTakeoffPacket packet, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity serverPlayer = ctx.get().getSender();
            if(serverPlayer == null){
                return;
            }
            if (FlightHelper.canTakeOff(serverPlayer)) {
                FlightHelper.takeOffFalconFlight(serverPlayer);
                CaptainAmerica.LOGGER.info("Player {} has taken off using an EXO-7 Falcon!", serverPlayer);
            } else{
                CaptainAmerica.LOGGER.info("Player {} cannot take off using an EXO-7 Falcon!", serverPlayer);
                FlightHelper.stopFalconFlight(serverPlayer);
            }
        });
    }

    public static void handlePropulsion(CPropulsionPacket packet, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity serverPlayer = ctx.get().getSender();
            if(serverPlayer == null){
                return;
            }
            if (FlightHelper.canPropel(serverPlayer)) {
                FlightHelper.propelFalconFlight(serverPlayer);
                CaptainAmerica.LOGGER.info("Player {} has propelled with an EXO-7 Falcon!", serverPlayer);
            } else {
                CaptainAmerica.LOGGER.info("Player {} cannot propel using an EXO-7 Falcon!", serverPlayer);
                FlightHelper.stopFalconFlight(serverPlayer);
            }
        });
    }
}
