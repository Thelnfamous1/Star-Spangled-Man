package com.infamous.captain_america.server.network.packet;

import com.infamous.captain_america.client.network.ClientNetworkHandler;
import com.infamous.captain_america.client.network.packet.CFlightPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SFlightPacket {
    private final SFlightPacket.Action action;
    private final float data;
    private final boolean flag;

    public SFlightPacket(SFlightPacket.Action action){
        this(action, 0, false);
    }

    public SFlightPacket(SFlightPacket.Action action, float data){
        this(action, data, false);
    }

    public SFlightPacket(SFlightPacket.Action action, boolean flag){
        this(action, 0, flag);
    }

    public SFlightPacket(SFlightPacket.Action action, float data, boolean flag){
        this.action = action;
        this.data = data;
        this.flag = flag;
    }

    public static SFlightPacket decodePacket(PacketBuffer packetBuffer){
        Action action = packetBuffer.readEnum(Action.class);
        float data = packetBuffer.readFloat();
        boolean flag = packetBuffer.readBoolean();
        return new SFlightPacket(action, data, flag);
    }

    public static void encodePacket(SFlightPacket packet, PacketBuffer packetBuffer){
        packetBuffer.writeEnum(packet.action);
        packetBuffer.writeFloat(packet.data);
        packetBuffer.writeBoolean(packet.flag);
    }

    public static void handlePacket(SFlightPacket packet, Supplier<NetworkEvent.Context> ctx){
        ClientNetworkHandler.handleFlight(packet, ctx);
    }

    public SFlightPacket.Action getAction() {
        return this.action;
    }

    public float getData() {
        return this.data;
    }

    public boolean getFlag() {
        return this.flag;
    }

    public enum Action {
        TOGGLE_FLIGHT,
        TAKEOFF_FLIGHT,
        BOOST_FLIGHT,
        TOGGLE_HOVER,
        VERTICAL_FLIGHT
    }
}