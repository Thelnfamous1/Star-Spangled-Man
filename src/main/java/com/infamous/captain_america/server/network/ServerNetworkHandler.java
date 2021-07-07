package com.infamous.captain_america.server.network;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.network.packet.*;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.item.VibraniumShieldItem;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.common.util.FalconAbilityKey;
import com.infamous.captain_america.common.util.FalconAbilityValue;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import com.infamous.captain_america.server.network.packet.SFlightPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Optional;
import java.util.function.Supplier;

public class ServerNetworkHandler {

    public static void handleFlight(CFlightPacket packet, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity serverPlayer = ctx.get().getSender();
            if(serverPlayer == null){
                return;
            }
            IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(serverPlayer);
            if(falconAbilityCap == null) return;

            switch (packet.getAction()){
                case TOGGLE_FLIGHT:
                    if (FalconFlightHelper.hasEXO7Falcon(serverPlayer)) {
                        boolean toggledTo = FalconFlightHelper.toggleEXO7Falcon(serverPlayer);
                        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SFlightPacket(SFlightPacket.Action.TOGGLE_FLIGHT, toggledTo));
                        CaptainAmerica.LOGGER.debug("Server player {} has toggled their EXO-7 Falcon flight to: {}", serverPlayer.getDisplayName().getString(), toggledTo);
                        TranslationTextComponent flightToggleMessage = toggledTo ? new TranslationTextComponent("action.falcon.flightOn") : new TranslationTextComponent("action.falcon.flightOff");
                        serverPlayer.sendMessage(flightToggleMessage, Util.NIL_UUID);

                        boolean wasHovering = falconAbilityCap.isHovering();
                        if(falconAbilityCap.isHovering() && !toggledTo){
                            falconAbilityCap.setHovering(false);
                            CaptainAmerica.LOGGER.debug("{} can no longer hover using an EXO-7 Falcon", serverPlayer.getDisplayName().getString());
                        }
                        TranslationTextComponent hoverToggleMessage = falconAbilityCap.isHovering() ? new TranslationTextComponent("action.falcon.hoverOn") : new TranslationTextComponent("action.falcon.hoverOff");
                        if(wasHovering != falconAbilityCap.isHovering()){
                            serverPlayer.sendMessage(hoverToggleMessage, Util.NIL_UUID);
                        }
                    } else {
                        CaptainAmerica.LOGGER.debug("Server player {} cannot toggle their EXO-7 Falcon flight!", serverPlayer.getDisplayName().getString());
                    }
                    break;
                case TAKEOFF_FLIGHT:
                    if (FalconFlightHelper.canBoostFlight(serverPlayer)) {
                        FalconFlightHelper.playFlightBoostSound(serverPlayer);
                        CaptainAmerica.LOGGER.debug("Server player {} has taken off using an EXO-7 Falcon!", serverPlayer.getDisplayName().getString());
                        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SFlightPacket(SFlightPacket.Action.TAKEOFF_FLIGHT));
                    } else {
                        CaptainAmerica.LOGGER.debug("Server player {} cannot take off using an EXO-7 Falcon!", serverPlayer.getDisplayName().getString());
                    }
                    break;
                case BOOST_FLIGHT:
                    if (FalconFlightHelper.canBoostFlight(serverPlayer)) {
                        FalconFlightHelper.boostFlight(serverPlayer);
                        CaptainAmerica.LOGGER.debug("Server player {} has boosted their EXO-7 Falcon flight!", serverPlayer.getDisplayName().getString());
                        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SFlightPacket(SFlightPacket.Action.BOOST_FLIGHT));
                    } else {
                        CaptainAmerica.LOGGER.debug("Server player {} cannot boost their EXO-7 Falcon flight!", serverPlayer.getDisplayName().getString());
                    }
                    break;
                case VERTICAL_FLIGHT:
                    if(falconAbilityCap.isHovering() && FalconFlightHelper.canHover(serverPlayer)){
                        falconAbilityCap.setVerticallyFlying(true);
                        FalconFlightHelper.verticallyFly(serverPlayer, packet.getFlag());
                        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SFlightPacket(SFlightPacket.Action.VERTICAL_FLIGHT, packet.getFlag()));
                    }
                    break;

            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void handleShield(CShieldPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->{
            ServerPlayerEntity serverPlayer = ctx.get().getSender();
            if(serverPlayer == null){
                return;
            }
            switch (packet.getThrowType()){
                case BOOMERANG_THROW:{
                    if (VibraniumShieldItem.hasVibraniumShield(serverPlayer)) {
                        Hand shieldHoldingHand = VibraniumShieldItem.getShieldHoldingHand(serverPlayer);
                        ItemStack heldShield = serverPlayer.getItemInHand(shieldHoldingHand);
                        Optional<VibraniumShieldItem> optionalShield = VibraniumShieldItem.getShieldItem(heldShield);
                        if(optionalShield.isPresent() && optionalShield.get().throwShield(heldShield, serverPlayer.level, serverPlayer, packet.getThrowType(), packet.getData())){
                            CaptainAmerica.LOGGER.info("Server player {} has boomerang-thrown their Vibranium Shield!", serverPlayer.getDisplayName().getString());
                        } else{
                            CaptainAmerica.LOGGER.info("Server player {} has failed to boomerang-throw their Vibranium Shield!", serverPlayer.getDisplayName().getString());
                        }
                    }
                    break;
                }

                case RICOCHET_THROW:{
                    if (VibraniumShieldItem.hasVibraniumShield(serverPlayer)) {
                        Hand shieldHoldingHand = VibraniumShieldItem.getShieldHoldingHand(serverPlayer);
                        ItemStack heldShield = serverPlayer.getItemInHand(shieldHoldingHand);
                        Optional<VibraniumShieldItem> optionalShield = VibraniumShieldItem.getShieldItem(heldShield);
                        if(optionalShield.isPresent() && optionalShield.get().throwShield(heldShield, serverPlayer.level, serverPlayer, packet.getThrowType(), packet.getData())){
                            CaptainAmerica.LOGGER.info("Server player {} has ricochet-thrown their Vibranium Shield!", serverPlayer.getDisplayName().getString());
                        } else{
                            CaptainAmerica.LOGGER.info("Server player {} has failed to ricochet-throw their Vibranium Shield!", serverPlayer.getDisplayName().getString());
                        }
                    }
                    break;
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void handleSetAbility(CSetFalconAbilityPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(()->{
            ServerPlayerEntity serverPlayer = ctx.get().getSender();
            if(serverPlayer == null) return;

            IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(serverPlayer);
            if(falconAbilityCap != null){
                if(!packet.getKey().isValidForValue(packet.getValue())){
                    CaptainAmerica.LOGGER.error(
                            "Failed to set the {} ability for server player {} because the value {} is not valid for ability key",
                            packet.getKey().name(),
                            serverPlayer.getDisplayName().getString(),
                            packet.getValue().name());
                } else{
                    falconAbilityCap.put(packet.getKey(), packet.getValue());
                    serverPlayer.sendMessage(
                            new TranslationTextComponent("action.falcon.setAbility",
                                    new TranslationTextComponent(packet.getKey().getTranslationKey()),
                                    new TranslationTextComponent(packet.getKey().getTranslationKey(packet.getValue()))),
                            Util.NIL_UUID);
                    CaptainAmerica.LOGGER.info("Server player {} has set their {} ability to {}!", serverPlayer.getDisplayName().getString(), packet.getKey().name(), packet.getValue().name());

                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void handleUseAbility(CUseAbilityPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(()->{
            ServerPlayerEntity serverPlayer = ctx.get().getSender();
            if(serverPlayer == null) return;

            IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(serverPlayer);
            if(falconAbilityCap != null){
                FalconAbilityKey abilityKey = packet.getAbilityKey();
                FalconAbilityValue abilityValue = falconAbilityCap.get(abilityKey);
                if(abilityValue.getKeyBindAction() == packet.getKeyBindAction()){
                    abilityValue.getPlayerConsumer().accept(serverPlayer);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
