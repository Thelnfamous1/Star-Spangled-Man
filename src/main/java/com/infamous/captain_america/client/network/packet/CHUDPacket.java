package com.infamous.captain_america.client.network.packet;

import com.infamous.captain_america.server.network.ServerNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CHUDPacket {
    private final CHUDPacket.Action action;
    private final float data;
    private final boolean flag;

    public CHUDPacket(CHUDPacket.Action action){
        this(action, 0, false);
    }

    public CHUDPacket(CHUDPacket.Action action, boolean flag){
        this(action, 0, flag);
    }

    public CHUDPacket(CHUDPacket.Action action, float data){
        this(action, data, false);
    }

    public CHUDPacket(CHUDPacket.Action action, float data, boolean flag){
        this.action = action;
        this.data = data;
        this.flag = flag;
    }

    public static CHUDPacket decodePacket(PacketBuffer packetBuffer){
        Action action = packetBuffer.readEnum(Action.class);
        float data = packetBuffer.readFloat();
        boolean flag = packetBuffer.readBoolean();
        return new CHUDPacket(action, data, flag);
    }

    public static void encodePacket(CHUDPacket packet, PacketBuffer packetBuffer){
        packetBuffer.writeEnum(packet.action);
        packetBuffer.writeFloat(packet.data);
        packetBuffer.writeBoolean(packet.flag);
    }

    public static void handlePacket(CHUDPacket packet, Supplier<NetworkEvent.Context> ctx){
        ServerNetworkHandler.handleHUD(packet, ctx);
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
        TOGGLE_HUD,
    }
}
