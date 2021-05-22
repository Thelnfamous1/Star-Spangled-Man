package com.infamous.captain_america.client.network.packet;

import com.infamous.captain_america.server.network.ServerNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CFlightVerticalPacket {

    public CFlightVerticalPacket(){

    }

    public static CFlightVerticalPacket decodePacket(PacketBuffer packetBuffer){
        return new CFlightVerticalPacket();
    }

    public static void encodePacket(CFlightVerticalPacket packet, PacketBuffer packetBuffer){

    }

    public static void handlePacket(CFlightVerticalPacket packet, Supplier<NetworkEvent.Context> ctx){
        ServerNetworkHandler.handleFlyUp(packet, ctx);
    }
}
