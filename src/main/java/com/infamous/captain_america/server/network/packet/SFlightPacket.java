package com.infamous.captain_america.server.network.packet;

import com.infamous.captain_america.client.network.ClientNetworkHandler;
import com.infamous.captain_america.client.network.packet.CFlightPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SFlightPacket {
    private SFlightPacket.Action action;

    public SFlightPacket(SFlightPacket.Action action){
        this.action = action;
    }

    public static SFlightPacket decodePacket(PacketBuffer packetBuffer){
        return new SFlightPacket(packetBuffer.readEnum(SFlightPacket.Action.class));
    }

    public static void encodePacket(SFlightPacket packet, PacketBuffer packetBuffer){
        packetBuffer.writeEnum(packet.action);
    }

    public static void handlePacket(SFlightPacket packet, Supplier<NetworkEvent.Context> ctx){
        ClientNetworkHandler.handleFlight(packet, ctx);
    }

    public SFlightPacket.Action getAction() {
        return this.action;
    }

    public enum Action {
        TAKEOFF_FLIGHT,
        BOOST_FLIGHT,
        HOVER
    }
}