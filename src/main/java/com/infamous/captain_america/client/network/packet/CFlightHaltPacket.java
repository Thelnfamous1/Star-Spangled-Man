package com.infamous.captain_america.client.network.packet;

import com.infamous.captain_america.server.network.ServerNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CFlightHaltPacket {

    public CFlightHaltPacket(){

    }

    public static CFlightHaltPacket decodePacket(PacketBuffer packetBuffer){
        return new CFlightHaltPacket();
    }

    public static void encodePacket(CFlightHaltPacket packet, PacketBuffer packetBuffer){

    }

    public static void handlePacket(CFlightHaltPacket packet, Supplier<NetworkEvent.Context> ctx){
        ServerNetworkHandler.handleHalt(packet, ctx);
    }
}
