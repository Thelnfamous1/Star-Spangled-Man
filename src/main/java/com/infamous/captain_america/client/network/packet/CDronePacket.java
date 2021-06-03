package com.infamous.captain_america.client.network.packet;

import com.infamous.captain_america.server.network.ServerNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CDronePacket {
    private CDronePacket.Action action;

    public CDronePacket(CDronePacket.Action action){
        this.action = action;
    }

    public static CDronePacket decodePacket(PacketBuffer packetBuffer){
        return new CDronePacket(packetBuffer.readEnum(CDronePacket.Action.class));
    }

    public static void encodePacket(CDronePacket packet, PacketBuffer packetBuffer){
        packetBuffer.writeEnum(packet.action);
    }

    public static void handlePacket(CDronePacket packet, Supplier<NetworkEvent.Context> ctx){
        ServerNetworkHandler.handleDrone(packet, ctx);
    }

    public CDronePacket.Action getAction() {
        return this.action;
    }

    public enum Action {
        DEPLOY,
        RECALL,
        PATROL
    }
}
