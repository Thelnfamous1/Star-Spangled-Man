package com.infamous.captain_america.client.network.packet;

import com.infamous.captain_america.server.ServerNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CPropulsionPacket {

    public CPropulsionPacket(){

    }

    public static CPropulsionPacket read(PacketBuffer packetBuffer){
        return new CPropulsionPacket();
    }

    public static void write(CPropulsionPacket packet, PacketBuffer packetBuffer){

    }

    public static void handle(CPropulsionPacket packet, Supplier<NetworkEvent.Context> ctx){
        ServerNetworkHandler.handlePropulsion(packet, ctx);
    }
}
