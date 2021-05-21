package com.infamous.captain_america.server.network;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.network.packet.CHaltPacket;
import com.infamous.captain_america.client.network.packet.CPropulsionPacket;
import com.infamous.captain_america.client.network.packet.CTakeoffPacket;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.common.util.FlightHelper;
import com.infamous.captain_america.server.network.packet.SPropulsionPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

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
                CaptainAmerica.LOGGER.info("Server player {} has taken off using an EXO-7 Falcon!", serverPlayer.getDisplayName().getString());
            } else {
                CaptainAmerica.LOGGER.info("Server player {} cannot take off using an EXO-7 Falcon!", serverPlayer.getDisplayName().getString());
                FlightHelper.haltFalconFlight(serverPlayer);
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
                CaptainAmerica.LOGGER.info("Server player {} has propelled with an EXO-7 Falcon!", serverPlayer.getDisplayName().getString());
                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SPropulsionPacket());
            } else {
                CaptainAmerica.LOGGER.info("Server player {} cannot propel using an EXO-7 Falcon!", serverPlayer.getDisplayName().getString());
            }
        });
    }

    public static void handleHalt(CHaltPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity serverPlayer = ctx.get().getSender();
            if(serverPlayer == null){
                return;
            }
            FlightHelper.haltFalconFlight(serverPlayer);
            CaptainAmerica.LOGGER.info("Server player {} has halted their EXO-7 Falcon flight!", serverPlayer.getDisplayName().getString());
        });
    }
}
