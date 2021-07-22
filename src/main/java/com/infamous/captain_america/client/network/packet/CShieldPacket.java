package com.infamous.captain_america.client.network.packet;

import com.infamous.captain_america.common.entity.projectile.VibraniumShieldEntity;
import com.infamous.captain_america.server.network.ServerNetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class CShieldPacket {
    private VibraniumShieldEntity.ThrowType throwType;
    private int data;

    public CShieldPacket(VibraniumShieldEntity.ThrowType throwType, int data){
        this.throwType = throwType;
        this.data = data;
    }

    public static CShieldPacket decodePacket(FriendlyByteBuf packetBuffer){
        VibraniumShieldEntity.ThrowType throwType = packetBuffer.readEnum(VibraniumShieldEntity.ThrowType.class);
        int data = packetBuffer.readVarInt();
        return new CShieldPacket(throwType, data);
    }

    public static void encodePacket(CShieldPacket packet, FriendlyByteBuf packetBuffer){
        packetBuffer.writeEnum(packet.throwType);
        packetBuffer.writeVarInt(packet.data);
    }

    public static void handlePacket(CShieldPacket packet, Supplier<NetworkEvent.Context> ctx){
        ServerNetworkHandler.handleShield(packet, ctx);
    }

    public VibraniumShieldEntity.ThrowType getThrowType() {
        return this.throwType;
    }

    public int getData() {
        return this.data;
    }
}
