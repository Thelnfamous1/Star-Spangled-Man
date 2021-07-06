package com.infamous.captain_america.client.network.packet;

import com.infamous.captain_america.server.network.ServerNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CFlightPacket {
    private final CFlightPacket.Action action;
    private final float data;
    private final boolean flag;

    public CFlightPacket(CFlightPacket.Action action){
        this(action, 0, false);
    }

    public CFlightPacket(CFlightPacket.Action action, boolean flag){
        this(action, 0, flag);
    }

    public CFlightPacket(CFlightPacket.Action action, float data){
        this(action, data, false);
    }

    public CFlightPacket(CFlightPacket.Action action, float data, boolean flag){
        this.action = action;
        this.data = data;
        this.flag = flag;
    }

    public static CFlightPacket decodePacket(PacketBuffer packetBuffer){
        Action action = packetBuffer.readEnum(Action.class);
        float data = packetBuffer.readFloat();
        boolean flag = packetBuffer.readBoolean();
        return new CFlightPacket(action, data, flag);
    }

    public static void encodePacket(CFlightPacket packet, PacketBuffer packetBuffer){
        packetBuffer.writeEnum(packet.action);
        packetBuffer.writeFloat(packet.data);
        packetBuffer.writeBoolean(packet.flag);
    }

    public static void handlePacket(CFlightPacket packet, Supplier<NetworkEvent.Context> ctx){
        ServerNetworkHandler.handleFlight(packet, ctx);
    }

    public Action getAction() {
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
        HALT_FLIGHT,
        TOGGLE_HOVER,
        VERTICAL_FLIGHT
    }
}
