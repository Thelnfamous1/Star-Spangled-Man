package com.infamous.captain_america.server.network.packet;

import com.infamous.captain_america.client.network.ClientNetworkHandler;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.server.network.ServerNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SSetFalconAbilityPacket {
    private final IFalconAbility.Key key;
    private final IFalconAbility.Value value;

    public SSetFalconAbilityPacket(IFalconAbility.Key key, IFalconAbility.Value value){
        this.key = key;
        this.value = value;
    }

    public static SSetFalconAbilityPacket decodePacket(PacketBuffer packetBuffer){
        return new SSetFalconAbilityPacket(packetBuffer.readEnum(IFalconAbility.Key.class), packetBuffer.readEnum(IFalconAbility.Value.class));
    }

    public static void encodePacket(SSetFalconAbilityPacket packet, PacketBuffer packetBuffer){
        packetBuffer.writeEnum(packet.key);
        packetBuffer.writeEnum(packet.value);
    }

    public static void handlePacket(SSetFalconAbilityPacket packet, Supplier<NetworkEvent.Context> ctx){
        ClientNetworkHandler.handleSetAbility(packet, ctx);
    }

    public IFalconAbility.Key getKey() {
        return this.key;
    }

    public IFalconAbility.Value getValue() {
        return this.value;
    }
}