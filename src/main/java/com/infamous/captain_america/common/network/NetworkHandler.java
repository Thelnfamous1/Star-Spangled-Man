package com.infamous.captain_america.common.network;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.network.packet.CFlightPacket;
import com.infamous.captain_america.client.network.packet.CDronePacket;
import com.infamous.captain_america.client.network.packet.CSetFalconAbilityPacket;
import com.infamous.captain_america.client.network.packet.CShieldPacket;
import com.infamous.captain_america.server.network.packet.SFlightPacket;
import com.infamous.captain_america.server.network.packet.SSetFalconAbilityPacket;
import com.infamous.captain_america.server.network.packet.SShieldPacket;
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

    protected static int PACKET_COUNTER = 0;

    public NetworkHandler() {
    }

    public static void init() {
        CaptainAmerica.LOGGER.debug("Registering network");

        INSTANCE.registerMessage(
                incrementAndGetPacketCounter(),
                CFlightPacket.class,
                CFlightPacket::encodePacket,
                CFlightPacket::decodePacket,
                CFlightPacket::handlePacket,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        INSTANCE.registerMessage(
                incrementAndGetPacketCounter(),
                SFlightPacket.class,
                SFlightPacket::encodePacket,
                SFlightPacket::decodePacket,
                SFlightPacket::handlePacket,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        INSTANCE.registerMessage(
                incrementAndGetPacketCounter(),
                CDronePacket.class,
                CDronePacket::encodePacket,
                CDronePacket::decodePacket,
                CDronePacket::handlePacket,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        INSTANCE.registerMessage(
                incrementAndGetPacketCounter(),
                CShieldPacket.class,
                CShieldPacket::encodePacket,
                CShieldPacket::decodePacket,
                CShieldPacket::handlePacket,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        INSTANCE.registerMessage(
                incrementAndGetPacketCounter(),
                SShieldPacket.class,
                SShieldPacket::encodePacket,
                SShieldPacket::decodePacket,
                SShieldPacket::handlePacket,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        INSTANCE.registerMessage(
                incrementAndGetPacketCounter(),
                CSetFalconAbilityPacket.class,
                CSetFalconAbilityPacket::encodePacket,
                CSetFalconAbilityPacket::decodePacket,
                CSetFalconAbilityPacket::handlePacket,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        INSTANCE.registerMessage(
                incrementAndGetPacketCounter(),
                SSetFalconAbilityPacket.class,
                SSetFalconAbilityPacket::encodePacket,
                SSetFalconAbilityPacket::decodePacket,
                SSetFalconAbilityPacket::handlePacket,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));


        CaptainAmerica.LOGGER.debug("Finished registering network!");
    }

    public static int incrementAndGetPacketCounter() {
        return PACKET_COUNTER++;
    }

}
