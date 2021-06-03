package com.infamous.captain_america.server.network.packet;

import com.infamous.captain_america.client.network.ClientNetworkHandler;
import com.infamous.captain_america.client.network.packet.CFlightPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SFlightPacket {
    private SFlightPacket.Action action;
    private int data;

    public SFlightPacket(SFlightPacket.Action action){
        this(action, 0);
    }

    public SFlightPacket(SFlightPacket.Action action, int data){
        this.action = action;
        this.data = data;
    }

    public static SFlightPacket decodePacket(PacketBuffer packetBuffer){
        Action action = packetBuffer.readEnum(Action.class);
        int data = packetBuffer.readVarInt();
        return new SFlightPacket(action, data);
    }

    public static void encodePacket(SFlightPacket packet, PacketBuffer packetBuffer){
        packetBuffer.writeEnum(packet.action);
        packetBuffer.writeVarInt(packet.data);
    }

    public static void handlePacket(SFlightPacket packet, Supplier<NetworkEvent.Context> ctx){
        ClientNetworkHandler.handleFlight(packet, ctx);
    }

    public SFlightPacket.Action getAction() {
        return this.action;
    }

    public int getData() {
        return this.data;
    }

    public enum Action {
        TOGGLE_FLIGHT,
        TAKEOFF_FLIGHT,
        BOOST_FLIGHT,
        HOVER;
    }
}