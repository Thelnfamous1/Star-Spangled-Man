package com.infamous.captain_america.server.network.packet;

import com.infamous.captain_america.client.network.ClientNetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class SDronePacket {
    private final SDronePacket.Action action;
    private final int id;
    private final boolean flag;

    public SDronePacket(SDronePacket.Action action){
        this(action, 0, false);
    }

    public SDronePacket(SDronePacket.Action action, int id){
        this(action, id, false);
    }

    public SDronePacket(SDronePacket.Action action, boolean flag){
        this(action, 0, flag);
    }

    public SDronePacket(SDronePacket.Action action, int id, boolean flag){
        this.action = action;
        this.id = id;
        this.flag = flag;
    }

    public static SDronePacket decodePacket(FriendlyByteBuf packetBuffer){
        Action action = packetBuffer.readEnum(Action.class);
        int id = packetBuffer.readInt();
        boolean flag = packetBuffer.readBoolean();
        return new SDronePacket(action, id, flag);
    }

    public static void encodePacket(SDronePacket packet, FriendlyByteBuf packetBuffer){
        packetBuffer.writeEnum(packet.action);
        packetBuffer.writeInt(packet.id);
        packetBuffer.writeBoolean(packet.flag);
    }

    public static void handlePacket(SDronePacket packet, Supplier<NetworkEvent.Context> ctx){
        ClientNetworkHandler.handleDrone(packet, ctx);
    }

    public SDronePacket.Action getAction() {
        return this.action;
    }

    public int getId() {
        return this.id;
    }

    public boolean getFlag() {
        return this.flag;
    }

    public enum Action {
        TOGGLE_CAMERA
    }
}