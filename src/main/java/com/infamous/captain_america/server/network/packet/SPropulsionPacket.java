package com.infamous.captain_america.server.network.packet;

import com.infamous.captain_america.client.network.ClientNetworkHandler;
import com.infamous.captain_america.server.network.ServerNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SPropulsionPacket {

    public SPropulsionPacket(){

    }

    public static SPropulsionPacket decodePacket(PacketBuffer packetBuffer){
        return new SPropulsionPacket();
    }

    public static void encodePacket(SPropulsionPacket packet, PacketBuffer packetBuffer){

    }

    public static void handlePacket(SPropulsionPacket packet, Supplier<NetworkEvent.Context> ctx){
        ClientNetworkHandler.handlePropulsion(packet, ctx);
    }
}