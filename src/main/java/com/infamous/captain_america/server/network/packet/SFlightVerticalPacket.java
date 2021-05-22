package com.infamous.captain_america.server.network.packet;

import com.infamous.captain_america.client.network.ClientNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SFlightVerticalPacket {

    public SFlightVerticalPacket(){

    }

    public static SFlightVerticalPacket decodePacket(PacketBuffer packetBuffer){
        return new SFlightVerticalPacket();
    }

    public static void encodePacket(SFlightVerticalPacket packet, PacketBuffer packetBuffer){

    }

    public static void handlePacket(SFlightVerticalPacket packet, Supplier<NetworkEvent.Context> ctx){
        ClientNetworkHandler.handleFlyUp(packet, ctx);
    }
}