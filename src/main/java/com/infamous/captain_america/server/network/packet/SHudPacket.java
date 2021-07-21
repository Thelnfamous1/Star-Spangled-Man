package com.infamous.captain_america.server.network.packet;

import com.infamous.captain_america.client.network.ClientNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SHudPacket {
    private final SHudPacket.Action action;
    private int id;
    private final float data;
    private final boolean flag;

    public SHudPacket(SHudPacket.Action action){
        this(action, 0, 0, false);
    }

    public SHudPacket(SHudPacket.Action action, int id){
        this(action, id, 0, false);
    }

    public SHudPacket(SHudPacket.Action action, float data){
        this(action, 0, data, false);
    }

    public SHudPacket(SHudPacket.Action action, boolean flag){
        this(action, 0, 0, flag);
    }

    public SHudPacket(SHudPacket.Action action, int id, float data, boolean flag){
        this.id = id;
        this.action = action;
        this.data = data;
        this.flag = flag;
    }

    public static SHudPacket decodePacket(PacketBuffer packetBuffer){
        Action action = packetBuffer.readEnum(Action.class);
        int id = packetBuffer.readInt();
        float data = packetBuffer.readFloat();
        boolean flag = packetBuffer.readBoolean();
        return new SHudPacket(action, id, data, flag);
    }

    public static void encodePacket(SHudPacket packet, PacketBuffer packetBuffer){
        packetBuffer.writeEnum(packet.action);
        packetBuffer.writeInt(packet.id);
        packetBuffer.writeFloat(packet.data);
        packetBuffer.writeBoolean(packet.flag);
    }

    public static void handlePacket(SHudPacket packet, Supplier<NetworkEvent.Context> ctx){
        ClientNetworkHandler.handleHUD(packet, ctx);
    }

    public int getId() {
        return this.id;
    }

    public SHudPacket.Action getAction() {
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
        TOGGLE_EAGLE_EYES,
        TRACK_HURT_BY,
        TRACK_HURT,
    }
}