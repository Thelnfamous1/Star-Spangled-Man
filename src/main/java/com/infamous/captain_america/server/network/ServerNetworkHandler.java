package com.infamous.captain_america.server.network;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.network.packet.CFlightHaltPacket;
import com.infamous.captain_america.client.network.packet.CFlightBoostPacket;
import com.infamous.captain_america.client.network.packet.CFlightTakeoffPacket;
import com.infamous.captain_america.client.network.packet.CFlightVerticalPacket;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import com.infamous.captain_america.server.network.packet.SFlightBoostPacket;
import com.infamous.captain_america.server.network.packet.SFlightVerticalPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class ServerNetworkHandler {

    public static void handleTakeoff(CFlightTakeoffPacket packet, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity serverPlayer = ctx.get().getSender();
            if(serverPlayer == null){
                return;
            }
            if (FalconFlightHelper.canTakeOff(serverPlayer)) {
                FalconFlightHelper.takeOff(serverPlayer);
                CaptainAmerica.LOGGER.info("Server player {} has taken off using an EXO-7 Falcon!", serverPlayer.getDisplayName().getString());
            } else {
                CaptainAmerica.LOGGER.info("Server player {} cannot take off using an EXO-7 Falcon!", serverPlayer.getDisplayName().getString());
                FalconFlightHelper.haltFlight(serverPlayer);
            }
        });
    }

    public static void handleBoost(CFlightBoostPacket packet, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity serverPlayer = ctx.get().getSender();
            if(serverPlayer == null){
                return;
            }
            if (FalconFlightHelper.canBoostFlight(serverPlayer)) {
                FalconFlightHelper.boostFlight(serverPlayer);
                CaptainAmerica.LOGGER.info("Server player {} has boosted their EXO-7 Falcon flight!", serverPlayer.getDisplayName().getString());
                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SFlightBoostPacket());
            } else {
                CaptainAmerica.LOGGER.info("Server player {} cannot boost their EXO-7 Falcon flight!", serverPlayer.getDisplayName().getString());
            }
        });
    }

    public static void handleHalt(CFlightHaltPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity serverPlayer = ctx.get().getSender();
            if(serverPlayer == null){
                return;
            }
            FalconFlightHelper.haltFlight(serverPlayer);
            CaptainAmerica.LOGGER.info("Server player {} has halted their EXO-7 Falcon flight!", serverPlayer.getDisplayName().getString());
        });
    }

    public static void handleFlyUp(CFlightVerticalPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->{
            ServerPlayerEntity serverPlayer = ctx.get().getSender();
            if(serverPlayer == null){
                return;
            }
            if (FalconFlightHelper.canFlyUp(serverPlayer)) {
                FalconFlightHelper.flyUp(serverPlayer);
                CaptainAmerica.LOGGER.info("Server player {} is flying up using an EXO-7 Falcon!", serverPlayer.getDisplayName().getString());
                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SFlightVerticalPacket());
            }
        });
    }
}
