package com.infamous.captain_america.client.network.packet;

import com.infamous.captain_america.server.network.ServerNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CShieldPacket {
    private CShieldPacket.Action action;

    public CShieldPacket(CShieldPacket.Action action){
        this.action = action;
    }

    public static CShieldPacket decodePacket(PacketBuffer packetBuffer){
        return new CShieldPacket(packetBuffer.readEnum(CShieldPacket.Action.class));
    }

    public static void encodePacket(CShieldPacket packet, PacketBuffer packetBuffer){
        packetBuffer.writeEnum(packet.action);
    }

    public static void handlePacket(CShieldPacket packet, Supplier<NetworkEvent.Context> ctx){
        ServerNetworkHandler.handleShield(packet, ctx);
    }

    public Action getAction() {
        return this.action;
    }

    public enum Action {
        THROW_SHIELD
    }
}
