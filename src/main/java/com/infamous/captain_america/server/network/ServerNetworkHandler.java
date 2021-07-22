package com.infamous.captain_america.server.network;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.network.packet.*;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.item.GogglesItem;
import com.infamous.captain_america.common.item.VibraniumShieldItem;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.common.util.CALogicHelper;
import com.infamous.captain_america.common.util.FalconAbilityKey;
import com.infamous.captain_america.common.util.FalconAbilityValue;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import com.infamous.captain_america.server.network.packet.SFlightPacket;
import com.infamous.captain_america.server.network.packet.SHudPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import java.util.Optional;
import java.util.function.Supplier;

public class ServerNetworkHandler {

    public static void handleFlight(CFlightPacket packet, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            ServerPlayer serverPlayer = ctx.get().getSender();
            if(serverPlayer == null){
                return;
            }
            IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(serverPlayer);
            if(falconAbilityCap == null) return;

            switch (packet.getAction()){
                case HALT_FLIGHT:
                    if(FalconFlightHelper.isFlying(serverPlayer)){
                        FalconFlightHelper.haltFlight(serverPlayer);
                    }
                    break;
                case TOGGLE_FLIGHT:
                    if (FalconFlightHelper.hasEXO7Falcon(serverPlayer)) {
                        boolean toggledTo = FalconFlightHelper.toggleEXO7Falcon(serverPlayer);
                        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SFlightPacket(SFlightPacket.Action.TOGGLE_FLIGHT, toggledTo));
                        //CaptainAmerica.LOGGER.debug("Server player {} has toggled their EXO-7 Falcon flight to: {}", serverPlayer.getDisplayName().getString(), toggledTo);
                        TranslatableComponent flightToggleMessage = toggledTo ? new TranslatableComponent("action.falcon.flightOn") : new TranslatableComponent("action.falcon.flightOff");
                        serverPlayer.sendMessage(flightToggleMessage.withStyle(ChatFormatting.RED), Util.NIL_UUID);

                        boolean wasHovering = falconAbilityCap.isHovering();
                        if(falconAbilityCap.isHovering() && !toggledTo){
                            falconAbilityCap.setHovering(false);
                            //CaptainAmerica.LOGGER.debug("{} can no longer hover using an EXO-7 Falcon", serverPlayer.getDisplayName().getString());
                            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SFlightPacket(SFlightPacket.Action.TOGGLE_HOVER, false));
                        }
                        TranslatableComponent hoverToggleMessage = falconAbilityCap.isHovering() ? new TranslatableComponent("action.falcon.hoverOn") : new TranslatableComponent("action.falcon.hoverOff");
                        if(wasHovering != falconAbilityCap.isHovering()){
                            serverPlayer.sendMessage(hoverToggleMessage.withStyle(ChatFormatting.RED), Util.NIL_UUID);
                        }
                    } else {
                        //CaptainAmerica.LOGGER.debug("Server player {} cannot toggle their EXO-7 Falcon flight!", serverPlayer.getDisplayName().getString());
                    }
                    break;
                case TAKEOFF_FLIGHT:
                    if (FalconFlightHelper.canBoostFlight(serverPlayer)) {
                        FalconFlightHelper.playFlightBoostSound(serverPlayer);
                        //CaptainAmerica.LOGGER.debug("Server player {} has taken off using an EXO-7 Falcon!", serverPlayer.getDisplayName().getString());
                        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SFlightPacket(SFlightPacket.Action.TAKEOFF_FLIGHT));
                    } else {
                        //CaptainAmerica.LOGGER.debug("Server player {} cannot take off using an EXO-7 Falcon!", serverPlayer.getDisplayName().getString());
                    }
                    break;
                case BOOST_FLIGHT:
                    if (FalconFlightHelper.canBoostFlight(serverPlayer)) {
                        FalconFlightHelper.boostFlight(serverPlayer);
                        FalconFlightHelper.animatePropulsion(serverPlayer);
                        //CaptainAmerica.LOGGER.debug("Server player {} has boosted their EXO-7 Falcon flight!", serverPlayer.getDisplayName().getString());
                        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SFlightPacket(SFlightPacket.Action.BOOST_FLIGHT));
                    } else {
                        //CaptainAmerica.LOGGER.debug("Server player {} cannot boost their EXO-7 Falcon flight!", serverPlayer.getDisplayName().getString());
                    }
                    break;
                case VERTICAL_FLIGHT:
                    if(falconAbilityCap.isHovering() && FalconFlightHelper.canHover(serverPlayer)){
                        falconAbilityCap.setVerticallyFlying(true);
                        FalconFlightHelper.verticallyFly(serverPlayer, packet.getFlag());
                        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SFlightPacket(SFlightPacket.Action.VERTICAL_FLIGHT, packet.getFlag()));
                    }
                    break;
                case LATERAL_FLIGHT:
                    float lateralMove = packet.getData();
                    if(lateralMove != 0.0F){
                        CALogicHelper.moveLaterally(serverPlayer, FalconFlightHelper.isRollFlying(serverPlayer), lateralMove);
                    }

            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void handleShield(CShieldPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->{
            ServerPlayer serverPlayer = ctx.get().getSender();
            if(serverPlayer == null){
                return;
            }
            switch (packet.getThrowType()){
                case BOOMERANG_THROW:
                case RICOCHET_THROW:
                    if (VibraniumShieldItem.hasVibraniumShield(serverPlayer)) {
                        InteractionHand shieldHoldingHand = VibraniumShieldItem.getShieldHoldingHand(serverPlayer);
                        ItemStack heldShield = serverPlayer.getItemInHand(shieldHoldingHand);
                        Optional<VibraniumShieldItem> optionalShield = VibraniumShieldItem.getShieldItem(heldShield);
                        optionalShield.ifPresent(vibraniumShieldItem -> vibraniumShieldItem.throwShield(heldShield, serverPlayer.level, serverPlayer, packet.getThrowType(), packet.getData()));
                    }
                    break;
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void handleSetAbility(CSetFalconAbilityPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(()->{
            ServerPlayer serverPlayer = ctx.get().getSender();
            if(serverPlayer == null) return;

            IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(serverPlayer);
            if(falconAbilityCap != null){
                if(!packet.getKey().isValidForValue(packet.getValue())){
                    CaptainAmerica.LOGGER.error(
                            "Failed to set the {} ability for server player {} because the value {} is not valid for the ability key",
                            packet.getKey().name(),
                            serverPlayer.getDisplayName().getString(),
                            packet.getValue().name());
                } else{
                    boolean put = falconAbilityCap.put(packet.getKey(), packet.getValue());
                    if(put){
                        serverPlayer.sendMessage(
                                new TranslatableComponent("action.falcon.setAbility",
                                        new TranslatableComponent(packet.getKey().getTranslationKey()),
                                        new TranslatableComponent(packet.getKey().getTranslationKey(packet.getValue())))
                                        .withStyle(ChatFormatting.RED),
                                Util.NIL_UUID);
                        //CaptainAmerica.LOGGER.info("Server player {} has set their {} ability to {}!", serverPlayer.getDisplayName().getString(), packet.getKey().name(), packet.getValue().name());
                    }

                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void handleUseAbility(CUseAbilityPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(()->{
            ServerPlayer serverPlayer = ctx.get().getSender();
            if(serverPlayer == null) return;

            IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(serverPlayer);
            if(falconAbilityCap != null){
                FalconAbilityKey abilityKey = packet.getAbilityKey();
                FalconAbilityValue abilityValue = falconAbilityCap.get(abilityKey);
                abilityValue.getHandlerForKeyBindAction(packet.getKeyBindAction()).accept(serverPlayer);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void handleHUD(CHUDPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer serverPlayer = ctx.get().getSender();
            if(serverPlayer == null){
                return;
            }
            IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(serverPlayer);
            if(falconAbilityCap == null) return;

            switch (packet.getAction()){
                case TOGGLE_HUD:
                    if (GogglesItem.getGoggles(serverPlayer).isPresent()) {
                        boolean toggledTo = GogglesItem.toggleHUD(serverPlayer);
                        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SHudPacket(SHudPacket.Action.TOGGLE_HUD, toggledTo));
                        //CaptainAmerica.LOGGER.debug("Server player {} has toggled their HUD to: {}", serverPlayer.getDisplayName().getString(), toggledTo);
                        TranslatableComponent hudToggleMessage = toggledTo ? new TranslatableComponent("action.falcon.hudOn") : new TranslatableComponent("action.falcon.hudOff");
                        serverPlayer.sendMessage(hudToggleMessage.withStyle(ChatFormatting.RED), Util.NIL_UUID);
                    } else {
                        //CaptainAmerica.LOGGER.debug("Server player {} cannot toggle their HUD!", serverPlayer.getDisplayName().getString());
                    }
                    break;

            }
        });
        ctx.get().setPacketHandled(true);
    }
}
