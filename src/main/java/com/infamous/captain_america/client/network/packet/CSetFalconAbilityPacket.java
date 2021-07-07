package com.infamous.captain_america.client.network.packet;

import com.infamous.captain_america.common.util.FalconAbilityKey;
import com.infamous.captain_america.common.util.FalconAbilityValue;
import com.infamous.captain_america.server.network.ServerNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CSetFalconAbilityPacket {
    private final FalconAbilityKey key;
    private final FalconAbilityValue value;

    public CSetFalconAbilityPacket(FalconAbilityKey key, FalconAbilityValue value){
        this.key = key;
        this.value = value;
    }

    public static CSetFalconAbilityPacket decodePacket(PacketBuffer packetBuffer){
        return new CSetFalconAbilityPacket(packetBuffer.readEnum(FalconAbilityKey.class), packetBuffer.readEnum(FalconAbilityValue.class));
    }

    public static void encodePacket(CSetFalconAbilityPacket packet, PacketBuffer packetBuffer){
        packetBuffer.writeEnum(packet.key);
        packetBuffer.writeEnum(packet.value);
    }

    public static void handlePacket(CSetFalconAbilityPacket packet, Supplier<NetworkEvent.Context> ctx){
        ServerNetworkHandler.handleSetAbility(packet, ctx);
    }

    public FalconAbilityKey getKey() {
        return this.key;
    }

    public FalconAbilityValue getValue() {
        return this.value;
    }
}