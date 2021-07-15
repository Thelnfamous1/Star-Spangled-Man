package com.infamous.captain_america.client.network;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.ForgeClientEvents;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.item.GogglesItem;
import com.infamous.captain_america.common.util.CALogicHelper;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import com.infamous.captain_america.server.network.packet.SCombatPacket;
import com.infamous.captain_america.server.network.packet.SFlightPacket;
import com.infamous.captain_america.server.network.packet.SHudPacket;
import com.infamous.captain_america.server.network.packet.SShieldPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientNetworkHandler {

    public static void handleFlight(SFlightPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->{
            Minecraft minecraft = Minecraft.getInstance();
            ClientPlayerEntity clientPlayer = minecraft.player;
            if(clientPlayer == null){
                return;
            }
            IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(clientPlayer);

            switch (packet.getAction()){
                case TOGGLE_FLIGHT:
                    boolean toggleTo = packet.getFlag();
                    FalconFlightHelper.toggleEXO7FalconTo(clientPlayer, toggleTo);
                    CaptainAmerica.LOGGER.debug("Client player {} has toggled their EXO-7 Falcon flight to: {}", clientPlayer.getDisplayName().getString(), toggleTo);

                    if(falconAbilityCap != null){
                        if(falconAbilityCap.isHovering() && !toggleTo){
                            falconAbilityCap.setHovering(false);
                            CaptainAmerica.LOGGER.debug("{} can no longer hover using an EXO-7 Falcon", clientPlayer.getDisplayName().getString());
                        }
                    }
                    break;
                case TAKEOFF_FLIGHT:
                    FalconFlightHelper.playFlightBoostSound(clientPlayer);
                    CaptainAmerica.LOGGER.debug("Client player {} has taken off using an EXO-7 Falcon!", clientPlayer.getDisplayName().getString());
                    break;
                case BOOST_FLIGHT:
                    FalconFlightHelper.boostFlight(clientPlayer);
                    //FalconFlightHelper.animatePropulsion(clientPlayer);
                    CaptainAmerica.LOGGER.debug("Client player {} has boosted their EXO-7 Falcon flight!", clientPlayer.getDisplayName().getString());
                    break;
                case TOGGLE_HOVER:
                    if(falconAbilityCap == null) return;

                    falconAbilityCap.setHovering(packet.getFlag());
                    CaptainAmerica.LOGGER.debug("Client player {} is {} hovering using an EXO-7 Falcon!", clientPlayer.getDisplayName().getString(), packet.getFlag() ? "" : "no longer");
                    break;
                case VERTICAL_FLIGHT:
                    if(falconAbilityCap == null) return;

                    falconAbilityCap.setVerticallyFlying(true);
                    FalconFlightHelper.verticallyFly(clientPlayer, packet.getFlag());
                    break;
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void handleShield(SShieldPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->{
            Minecraft minecraft = Minecraft.getInstance();
            ClientPlayerEntity clientPlayer = minecraft.player;
            if(clientPlayer == null){
                return;
            }
            switch (packet.getAction()){
                case SHIELD_HIT_PLAYER:
                    clientPlayer.level.playSound(clientPlayer, clientPlayer.getX(), clientPlayer.getY(), clientPlayer.getZ(), SoundEvents.SHIELD_BLOCK, SoundCategory.PLAYERS, 0.18F, 0.45F);
                    break;
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void handleHUD(SHudPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->{
            Minecraft minecraft = Minecraft.getInstance();
            ClientPlayerEntity clientPlayer = minecraft.player;
            if(clientPlayer == null){
                return;
            }

            switch (packet.getAction()){
                case TOGGLE_HUD:
                    boolean toggleTo = packet.getFlag();
                    GogglesItem.toggleHUDTo(clientPlayer, toggleTo);
                    CaptainAmerica.LOGGER.debug("Client player {} has toggled their HUD to: {}", clientPlayer.getDisplayName().getString(), toggleTo);
                    break;
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void handleCombat(SCombatPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->{
            Minecraft minecraft = Minecraft.getInstance();
            ClientPlayerEntity clientPlayer = minecraft.player;
            if(clientPlayer == null){
                return;
            }

            switch (packet.getAction()){
                case START_LASER:
                    ForgeClientEvents.LOCAL_LASER = true;
                    CaptainAmerica.LOGGER.debug("Client player {} has started firing their laser!", clientPlayer.getDisplayName().getString());
                    break;
                case CONTINUE_LASER:
                    ForgeClientEvents.LOCAL_LASER = true;
                    RayTraceResult rayTraceResult = CALogicHelper.getLaserRayTrace(clientPlayer);
                    if(rayTraceResult instanceof BlockRayTraceResult){
                        BlockRayTraceResult blockRTR = (BlockRayTraceResult)rayTraceResult;
                        BlockPos blockPos = blockRTR.getBlockPos();
                        Direction direction = blockRTR.getDirection();
                        if (minecraft.level != null && !minecraft.level.isEmptyBlock(blockPos) && minecraft.gameMode != null) {
                            CaptainAmerica.LOGGER.debug("Client player {} is attempting to break a block!", clientPlayer.getDisplayName().getString());
                            if (minecraft.gameMode.continueDestroyBlock(blockPos, direction)) {
                                minecraft.particleEngine.addBlockHitEffects(blockPos, blockRTR);
                            }
                        }
                    }
                    break;
                case STOP_LASER:
                    ForgeClientEvents.LOCAL_LASER = false;
                    if (minecraft.gameMode != null) {
                        minecraft.gameMode.stopDestroyBlock();
                    }
                    CaptainAmerica.LOGGER.debug("Client player {} has stopped firing their laser!", clientPlayer.getDisplayName().getString());
                    break;
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
