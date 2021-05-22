package com.infamous.captain_america.client.network.packet;

import com.infamous.captain_america.server.network.ServerNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CFlightBoostPacket {

    public CFlightBoostPacket(){

    }

    public static CFlightBoostPacket decodePacket(PacketBuffer packetBuffer){
        return new CFlightBoostPacket();
    }

    public static void encodePacket(CFlightBoostPacket packet, PacketBuffer packetBuffer){

    }

    public static void handlePacket(CFlightBoostPacket packet, Supplier<NetworkEvent.Context> ctx){
        ServerNetworkHandler.handleBoost(packet, ctx);
    }
}
