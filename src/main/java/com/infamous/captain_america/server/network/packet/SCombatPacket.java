package com.infamous.captain_america.server.network.packet;

import com.infamous.captain_america.client.network.ClientNetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class SCombatPacket {
    private final SCombatPacket.Action action;
    private final float data;
    private final boolean flag;

    public SCombatPacket(SCombatPacket.Action action){
        this(action, 0, false);
    }

    public SCombatPacket(SCombatPacket.Action action, float data){
        this(action, data, false);
    }

    public SCombatPacket(SCombatPacket.Action action, boolean flag){
        this(action, 0, flag);
    }

    public SCombatPacket(SCombatPacket.Action action, float data, boolean flag){
        this.action = action;
        this.data = data;
        this.flag = flag;
    }

    public static SCombatPacket decodePacket(FriendlyByteBuf packetBuffer){
        Action action = packetBuffer.readEnum(Action.class);
        float data = packetBuffer.readFloat();
        boolean flag = packetBuffer.readBoolean();
        return new SCombatPacket(action, data, flag);
    }

    public static void encodePacket(SCombatPacket packet, FriendlyByteBuf packetBuffer){
        packetBuffer.writeEnum(packet.action);
        packetBuffer.writeFloat(packet.data);
        packetBuffer.writeBoolean(packet.flag);
    }

    public static void handlePacket(SCombatPacket packet, Supplier<NetworkEvent.Context> ctx){
        ClientNetworkHandler.handleCombat(packet, ctx);
    }

    public SCombatPacket.Action getAction() {
        return this.action;
    }

    public float getData() {
        return this.data;
    }

    public boolean getFlag() {
        return this.flag;
    }

    public enum Action {
        START_LASER,
        CONTINUE_LASER,
        STOP_LASER,
        FIRING_MACHINE_GUN
    }
}