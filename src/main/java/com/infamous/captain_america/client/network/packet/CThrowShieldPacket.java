package com.infamous.captain_america.client.network.packet;

import com.infamous.captain_america.common.entity.VibraniumShieldEntity;
import com.infamous.captain_america.server.network.ServerNetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CThrowShieldPacket {
    private VibraniumShieldEntity.ThrowType throwType;

    public CThrowShieldPacket(VibraniumShieldEntity.ThrowType throwType){
        this.throwType = throwType;
    }

    public static CThrowShieldPacket decodePacket(PacketBuffer packetBuffer){
        return new CThrowShieldPacket(packetBuffer.readEnum(VibraniumShieldEntity.ThrowType.class));
    }

    public static void encodePacket(CThrowShieldPacket packet, PacketBuffer packetBuffer){
        packetBuffer.writeEnum(packet.throwType);
    }

    public static void handlePacket(CThrowShieldPacket packet, Supplier<NetworkEvent.Context> ctx){
        ServerNetworkHandler.handleThrowShield(packet, ctx);
    }

    public VibraniumShieldEntity.ThrowType getThrowType() {
        return this.throwType;
    }
}
