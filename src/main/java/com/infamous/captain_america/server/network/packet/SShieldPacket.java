package com.infamous.captain_america.server.network.packet;

import com.infamous.captain_america.client.network.ClientNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SShieldPacket {
    private SShieldPacket.Action action;

    public SShieldPacket(SShieldPacket.Action action){
        this.action = action;
    }

    public static SShieldPacket decodePacket(PacketBuffer packetBuffer){
        return new SShieldPacket(packetBuffer.readEnum(SShieldPacket.Action.class));
    }

    public static void encodePacket(SShieldPacket packet, PacketBuffer packetBuffer){
        packetBuffer.writeEnum(packet.action);
    }

    public static void handlePacket(SShieldPacket packet, Supplier<NetworkEvent.Context> ctx){
        ClientNetworkHandler.handleShield(packet, ctx);
    }

    public SShieldPacket.Action getAction() {
        return this.action;
    }

    public enum Action {
        SHIELD_HIT_PLAYER
    }
}