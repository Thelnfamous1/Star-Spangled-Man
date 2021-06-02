package com.infamous.captain_america.client.network.packet;

import com.infamous.captain_america.common.entity.VibraniumShieldEntity2;
import com.infamous.captain_america.server.network.ServerNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CThrowShieldPacket {
    private VibraniumShieldEntity2.ThrowType throwType;
    private int data;

    public CThrowShieldPacket(VibraniumShieldEntity2.ThrowType throwType, int data){
        this.throwType = throwType;
        this.data = data;
    }

    public CThrowShieldPacket(VibraniumShieldEntity2.ThrowType throwType){
        this(throwType, 0);
    }

    public static CThrowShieldPacket decodePacket(PacketBuffer packetBuffer){
        VibraniumShieldEntity2.ThrowType throwType = packetBuffer.readEnum(VibraniumShieldEntity2.ThrowType.class);
        int data = packetBuffer.readVarInt();
        return new CThrowShieldPacket(throwType, data);
    }

    public static void encodePacket(CThrowShieldPacket packet, PacketBuffer packetBuffer){
        packetBuffer.writeEnum(packet.throwType);
        packetBuffer.writeVarInt(packet.data);
    }

    public static void handlePacket(CThrowShieldPacket packet, Supplier<NetworkEvent.Context> ctx){
        ServerNetworkHandler.handleThrowShield(packet, ctx);
    }

    public VibraniumShieldEntity2.ThrowType getThrowType() {
        return this.throwType;
    }

    public int getData() {
        return this.data;
    }
}
