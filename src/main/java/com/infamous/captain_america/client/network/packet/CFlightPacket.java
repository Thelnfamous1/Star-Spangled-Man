package com.infamous.captain_america.client.network.packet;

import com.infamous.captain_america.server.network.ServerNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CFlightPacket {
    private CFlightPacket.Action action;

    public CFlightPacket(CFlightPacket.Action action){
        this.action = action;
    }

    public static CFlightPacket decodePacket(PacketBuffer packetBuffer){
        return new CFlightPacket(packetBuffer.readEnum(CFlightPacket.Action.class));
    }

    public static void encodePacket(CFlightPacket packet, PacketBuffer packetBuffer){
        packetBuffer.writeEnum(packet.action);
    }

    public static void handlePacket(CFlightPacket packet, Supplier<NetworkEvent.Context> ctx){
        ServerNetworkHandler.handleFlight(packet, ctx);
    }

    public Action getAction() {
        return this.action;
    }

    public enum Action {
        TAKEOFF_FLIGHT,
        BOOST_FLIGHT,
        HALT_FLIGHT,
        HOVER
    }
}
