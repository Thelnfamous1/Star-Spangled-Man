package com.infamous.captain_america.common.network;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.network.packet.CFlightHaltPacket;
import com.infamous.captain_america.client.network.packet.CFlightBoostPacket;
import com.infamous.captain_america.client.network.packet.CFlightTakeoffPacket;
import com.infamous.captain_america.client.network.packet.CFlightVerticalPacket;
import com.infamous.captain_america.server.network.packet.SFlightBoostPacket;
import com.infamous.captain_america.server.network.packet.SFlightVerticalPacket;
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
                CFlightTakeoffPacket.class,
                CFlightTakeoffPacket::encodePacket,
                CFlightTakeoffPacket::decodePacket,
                CFlightTakeoffPacket::handlePacket,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        INSTANCE.registerMessage(
                getPacketID(),
                CFlightBoostPacket.class,
                CFlightBoostPacket::encodePacket,
                CFlightBoostPacket::decodePacket,
                CFlightBoostPacket::handlePacket,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        INSTANCE.registerMessage(
                getPacketID(),
                SFlightBoostPacket.class,
                SFlightBoostPacket::encodePacket,
                SFlightBoostPacket::decodePacket,
                SFlightBoostPacket::handlePacket,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        INSTANCE.registerMessage(
                getPacketID(),
                CFlightHaltPacket.class,
                CFlightHaltPacket::encodePacket,
                CFlightHaltPacket::decodePacket,
                CFlightHaltPacket::handlePacket,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        INSTANCE.registerMessage(
                getPacketID(),
                CFlightVerticalPacket.class,
                CFlightVerticalPacket::encodePacket,
                CFlightVerticalPacket::decodePacket,
                CFlightVerticalPacket::handlePacket,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        INSTANCE.registerMessage(
                getPacketID(),
                SFlightVerticalPacket.class,
                SFlightVerticalPacket::encodePacket,
                SFlightVerticalPacket::decodePacket,
                SFlightVerticalPacket::handlePacket,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));


        CaptainAmerica.LOGGER.debug("Finished registering network!");
    }

    public static int getPacketID() {
        return packetID++;
    }

}
