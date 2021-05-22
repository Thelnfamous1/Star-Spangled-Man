package com.infamous.captain_america.client.network.packet;

import com.infamous.captain_america.server.network.ServerNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CFlightTakeoffPacket {

    public CFlightTakeoffPacket(){

    }

    public static CFlightTakeoffPacket decodePacket(PacketBuffer packetBuffer){
        return new CFlightTakeoffPacket();
    }

    public static void encodePacket(CFlightTakeoffPacket packet, PacketBuffer packetBuffer){

    }

    public static void handlePacket(CFlightTakeoffPacket packet, Supplier<NetworkEvent.Context> ctx){
        ServerNetworkHandler.handleTakeoff(packet, ctx);
    }
}
