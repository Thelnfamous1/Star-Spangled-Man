package com.infamous.captain_america.client.network.packet;

import com.infamous.captain_america.common.entity.projectile.VibraniumShieldEntity2;
import com.infamous.captain_america.server.network.ServerNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CShieldPacket {
    private VibraniumShieldEntity2.ThrowType throwType;
    private int data;

    public CShieldPacket(VibraniumShieldEntity2.ThrowType throwType, int data){
        this.throwType = throwType;
        this.data = data;
    }

    public static CShieldPacket decodePacket(PacketBuffer packetBuffer){
        VibraniumShieldEntity2.ThrowType throwType = packetBuffer.readEnum(VibraniumShieldEntity2.ThrowType.class);
        int data = packetBuffer.readVarInt();
        return new CShieldPacket(throwType, data);
    }

    public static void encodePacket(CShieldPacket packet, PacketBuffer packetBuffer){
        packetBuffer.writeEnum(packet.throwType);
        packetBuffer.writeVarInt(packet.data);
    }

    public static void handlePacket(CShieldPacket packet, Supplier<NetworkEvent.Context> ctx){
        ServerNetworkHandler.handleShield(packet, ctx);
    }

    public VibraniumShieldEntity2.ThrowType getThrowType() {
        return this.throwType;
    }

    public int getData() {
        return this.data;
    }
}
