package com.infamous.captain_america.common.network;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.network.packet.CHaltPacket;
import com.infamous.captain_america.client.network.packet.CPropulsionPacket;
import com.infamous.captain_america.client.network.packet.CTakeoffPacket;
import com.infamous.captain_america.server.network.packet.SPropulsionPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;

public final class NetworkHandler {
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(
            new ResourceLocation(CaptainAmerica.MODID, "network"))
            .clientAcceptedVersions("1"::equals)
            .serverAcceptedVersions("1"::equals)
            .networkProtocolVersion(() -> "1")
            .simpleChannel();

    protected static int packetID = 0;

    public NetworkHandler() {
    }

    public static void init() {
        CaptainAmerica.LOGGER.debug("Registering network");

        INSTANCE.registerMessage(
                getPacketID(),
                CTakeoffPacket.class,
                CTakeoffPacket::encodePacket,
                CTakeoffPacket::decodePacket,
                CTakeoffPacket::handlePacket,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        INSTANCE.registerMessage(
                getPacketID(),
                CPropulsionPacket.class,
                CPropulsionPacket::encodePacket,
                CPropulsionPacket::decodePacket,
                CPropulsionPacket::handlePacket,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        INSTANCE.registerMessage(
                getPacketID(),
                SPropulsionPacket.class,
                SPropulsionPacket::encodePacket,
                SPropulsionPacket::decodePacket,
                SPropulsionPacket::handlePacket,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        INSTANCE.registerMessage(
                getPacketID(),
                CHaltPacket.class,
                CHaltPacket::encodePacket,
                CHaltPacket::decodePacket,
                CHaltPacket::handlePacket,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));


        CaptainAmerica.LOGGER.debug("Finished registering network!");
    }

    public static int getPacketID() {
        return packetID++;
    }

}
