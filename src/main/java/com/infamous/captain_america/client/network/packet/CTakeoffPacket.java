package com.infamous.captain_america.client.network.packet;

import com.infamous.captain_america.server.network.ServerNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CTakeoffPacket {

    public CTakeoffPacket(){

    }

    public static CTakeoffPacket decodePacket(PacketBuffer packetBuffer){
        return new CTakeoffPacket();
    }

    public static void encodePacket(CTakeoffPacket packet, PacketBuffer packetBuffer){

    }

    public static void handlePacket(CTakeoffPacket packet, Supplier<NetworkEvent.Context> ctx){
        ServerNetworkHandler.handleTakeoff(packet, ctx);
    }
}
