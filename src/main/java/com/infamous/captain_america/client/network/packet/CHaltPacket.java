package com.infamous.captain_america.client.network.packet;

import com.infamous.captain_america.server.network.ServerNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CHaltPacket {

    public CHaltPacket(){

    }

    public static CHaltPacket decodePacket(PacketBuffer packetBuffer){
        return new CHaltPacket();
    }

    public static void encodePacket(CHaltPacket packet, PacketBuffer packetBuffer){

    }

    public static void handlePacket(CHaltPacket packet, Supplier<NetworkEvent.Context> ctx){
        ServerNetworkHandler.handleHalt(packet, ctx);
    }
}
