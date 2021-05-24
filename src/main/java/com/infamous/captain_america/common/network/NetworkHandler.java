package com.infamous.captain_america.common.network;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.network.packet.CFlightPacket;
import com.infamous.captain_america.client.network.packet.CRedwingPacket;
import com.infamous.captain_america.client.network.packet.CShieldPacket;
import com.infamous.captain_america.server.network.packet.SFlightPacket;
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
                CFlightPacket.class,
                CFlightPacket::encodePacket,
                CFlightPacket::decodePacket,
                CFlightPacket::handlePacket,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        INSTANCE.registerMessage(
                getPacketID(),
                SFlightPacket.class,
                SFlightPacket::encodePacket,
                SFlightPacket::decodePacket,
                SFlightPacket::handlePacket,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        INSTANCE.registerMessage(
                getPacketID(),
                CRedwingPacket.class,
                CRedwingPacket::encodePacket,
                CRedwingPacket::decodePacket,
                CRedwingPacket::handlePacket,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        INSTANCE.registerMessage(
                getPacketID(),
                CShieldPacket.class,
                CShieldPacket::encodePacket,
                CShieldPacket::decodePacket,
                CShieldPacket::handlePacket,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));


        CaptainAmerica.LOGGER.debug("Finished registering network!");
    }

    public static int getPacketID() {
        return packetID++;
    }

}
