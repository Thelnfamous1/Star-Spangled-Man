package com.infamous.captain_america.client.network.packet;

import com.infamous.captain_america.common.util.FalconAbilityKey;
import com.infamous.captain_america.common.util.KeyBindAction;
import com.infamous.captain_america.server.network.ServerNetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class CUseAbilityPacket {
    private final KeyBindAction keyBindAction;
    private final FalconAbilityKey abilityKey;

    public CUseAbilityPacket(KeyBindAction keyBindAction, FalconAbilityKey abilityKey){
        this.keyBindAction = keyBindAction;
        this.abilityKey = abilityKey;
    }

    public static CUseAbilityPacket decodePacket(FriendlyByteBuf packetBuffer){
        KeyBindAction keyBindAction = packetBuffer.readEnum(KeyBindAction.class);
        FalconAbilityKey abilityKey = packetBuffer.readEnum(FalconAbilityKey.class);
        return new CUseAbilityPacket(keyBindAction, abilityKey);
    }

    public static void encodePacket(CUseAbilityPacket packet, FriendlyByteBuf packetBuffer){
        packetBuffer.writeEnum(packet.keyBindAction);
        packetBuffer.writeEnum(packet.abilityKey);
    }

    public static void handlePacket(CUseAbilityPacket packet, Supplier<NetworkEvent.Context> ctx){
        ServerNetworkHandler.handleUseAbility(packet, ctx);
    }

    public KeyBindAction getKeyBindAction() {
        return this.keyBindAction;
    }

    public FalconAbilityKey getAbilityKey() {
        return this.abilityKey;
    }

}
