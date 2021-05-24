package com.infamous.captain_america.client.network.packet;

import com.infamous.captain_america.server.network.ServerNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CRedwingPacket {
    private CRedwingPacket.Action action;

    public CRedwingPacket(CRedwingPacket.Action action){
        this.action = action;
    }

    public static CRedwingPacket decodePacket(PacketBuffer packetBuffer){
        return new CRedwingPacket(packetBuffer.readEnum(CRedwingPacket.Action.class));
    }

    public static void encodePacket(CRedwingPacket packet, PacketBuffer packetBuffer){
        packetBuffer.writeEnum(packet.action);
    }

    public static void handlePacket(CRedwingPacket packet, Supplier<NetworkEvent.Context> ctx){
        ServerNetworkHandler.handleRedwing(packet, ctx);
    }

    public CRedwingPacket.Action getAction() {
        return this.action;
    }

    public enum Action {
        DEPLOY,
        RECALL,
        PATROL
    }
}
