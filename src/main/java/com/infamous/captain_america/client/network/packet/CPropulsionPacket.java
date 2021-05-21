package com.infamous.captain_america.client.network.packet;

import com.infamous.captain_america.server.network.ServerNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CPropulsionPacket {

    public CPropulsionPacket(){

    }

    public static CPropulsionPacket decodePacket(PacketBuffer packetBuffer){
        return new CPropulsionPacket();
    }

    public static void encodePacket(CPropulsionPacket packet, PacketBuffer packetBuffer){

    }

    public static void handlePacket(CPropulsionPacket packet, Supplier<NetworkEvent.Context> ctx){
        ServerNetworkHandler.handlePropulsion(packet, ctx);
    }
}
