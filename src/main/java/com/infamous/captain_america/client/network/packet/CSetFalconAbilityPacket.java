package com.infamous.captain_america.client.network.packet;

import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.server.network.ServerNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CSetFalconAbilityPacket {
    private final IFalconAbility.Key key;
    private final IFalconAbility.Value value;

    public CSetFalconAbilityPacket(IFalconAbility.Key key, IFalconAbility.Value value){
        this.key = key;
        this.value = value;
    }

    public static CSetFalconAbilityPacket decodePacket(PacketBuffer packetBuffer){
        return new CSetFalconAbilityPacket(packetBuffer.readEnum(IFalconAbility.Key.class), packetBuffer.readEnum(IFalconAbility.Value.class));
    }

    public static void encodePacket(CSetFalconAbilityPacket packet, PacketBuffer packetBuffer){
        packetBuffer.writeEnum(packet.key);
        packetBuffer.writeEnum(packet.value);
    }

    public static void handlePacket(CSetFalconAbilityPacket packet, Supplier<NetworkEvent.Context> ctx){
        ServerNetworkHandler.handleSetAbility(packet, ctx);
    }

    public IFalconAbility.Key getKey() {
        return this.key;
    }

    public IFalconAbility.Value getValue() {
        return this.value;
    }
}