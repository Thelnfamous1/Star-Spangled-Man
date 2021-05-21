package com.infamous.captain_america.client.network.packet;

import com.infamous.captain_america.server.ServerNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CTakeoffPacket {

    public CTakeoffPacket(){

    }

    public static CTakeoffPacket read(PacketBuffer packetBuffer){
        return new CTakeoffPacket();
    }

    public static void write(CTakeoffPacket packet, PacketBuffer packetBuffer){

    }

    public static void handle(CTakeoffPacket packet, Supplier<NetworkEvent.Context> ctx){
        ServerNetworkHandler.handleTakeoff(packet, ctx);
    }
}
