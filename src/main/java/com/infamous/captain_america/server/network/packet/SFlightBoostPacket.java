package com.infamous.captain_america.server.network.packet;

import com.infamous.captain_america.client.network.ClientNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SFlightBoostPacket {

    public SFlightBoostPacket(){

    }

    public static SFlightBoostPacket decodePacket(PacketBuffer packetBuffer){
        return new SFlightBoostPacket();
    }

    public static void encodePacket(SFlightBoostPacket packet, PacketBuffer packetBuffer){

    }

    public static void handlePacket(SFlightBoostPacket packet, Supplier<NetworkEvent.Context> ctx){
        ClientNetworkHandler.handleBoost(packet, ctx);
    }
}