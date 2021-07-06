package com.infamous.captain_america.server.network;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.network.packet.*;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.drone_controller.IDroneController;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.item.VibraniumShieldItem;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import com.infamous.captain_america.server.network.packet.SFlightPacket;
import com.infamous.captain_america.server.network.packet.SSetFalconAbilityPacket;
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

            switch (packet.getAction()){
                case TOGGLE_FLIGHT:
                    if (FalconFlightHelper.hasEXO7Falcon(serverPlayer)) {
                        boolean toggledTo = FalconFlightHelper.toggleEXO7Falcon(serverPlayer);
                        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SFlightPacket(SFlightPacket.Action.TOGGLE_FLIGHT, toggledTo));
                        CaptainAmerica.LOGGER.debug("Server player {} has toggled their EXO-7 Falcon flight to: {}", serverPlayer.getDisplayName().getString(), toggledTo);
                        TranslationTextComponent flightToggleMessage = toggledTo ? new TranslationTextComponent("action.falcon.flightOn") : new TranslationTextComponent("action.falcon.flightOff");
                        serverPlayer.sendMessage(flightToggleMessage, Util.NIL_UUID);

                        if(falconAbilityCap != null){
                            boolean wasHovering = falconAbilityCap.isHovering();
                            if(falconAbilityCap.isHovering() && !toggledTo){
                                falconAbilityCap.setHovering(false);
                            }
                            TranslationTextComponent hoverToggleMessage = falconAbilityCap.isHovering() ? new TranslationTextComponent("action.falcon.hoverOn") : new TranslationTextComponent("action.falcon.hoverOff");
                            if(wasHovering != falconAbilityCap.isHovering()){
                                serverPlayer.sendMessage(hoverToggleMessage, Util.NIL_UUID);
                            }
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
                case HALT_FLIGHT:
                    if(FalconFlightHelper.isFlying(serverPlayer)){
                        FalconFlightHelper.haltFlight(serverPlayer);
                        CaptainAmerica.LOGGER.debug("Server player {} has halted their EXO-7 Falcon flight!", serverPlayer.getDisplayName().getString());
                    }
                    break;
                case TOGGLE_HOVER:
                    if(falconAbilityCap == null) return;
                    boolean wasHovering = falconAbilityCap.isHovering();
                    falconAbilityCap.setHovering(!falconAbilityCap.isHovering() && FalconFlightHelper.canHover(serverPlayer));
                    CaptainAmerica.LOGGER.debug("Server player {} is {} hovering using an EXO-7 Falcon!", serverPlayer.getDisplayName().getString(), falconAbilityCap.isHovering() ? "" : "no longer");
                    NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SFlightPacket(SFlightPacket.Action.TOGGLE_HOVER, falconAbilityCap.isHovering()));
                    TranslationTextComponent hoverToggleMessage = falconAbilityCap.isHovering() ? new TranslationTextComponent("action.falcon.hoverOn") : new TranslationTextComponent("action.falcon.hoverOff");
                    if(wasHovering != falconAbilityCap.isHovering()){
                        serverPlayer.sendMessage(hoverToggleMessage, Util.NIL_UUID);
                    }
                    break;
                case VERTICAL_FLIGHT:
                    if(falconAbilityCap == null) return;
                    if(falconAbilityCap.isHovering()){
                        falconAbilityCap.setVerticallyFlying(true);
                        FalconFlightHelper.verticallyFly(serverPlayer, packet.getFlag());
                        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SFlightPacket(SFlightPacket.Action.VERTICAL_FLIGHT, packet.getFlag()));
                    }
                    break;

            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void handleDrone(CDronePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->{
            ServerPlayerEntity serverPlayer = ctx.get().getSender();
            if(serverPlayer == null){
                return;
            }
            switch (packet.getAction()){
                case DEPLOY: {
                    IDroneController droneControllerCap = CapabilityHelper.getDroneControllerCap(serverPlayer);
                    if(droneControllerCap != null){
                        if(droneControllerCap.deployDrone(serverPlayer)){
                            CaptainAmerica.LOGGER.debug("Server player {} has deployed a Redwing drone!", serverPlayer.getDisplayName().getString());
                            serverPlayer.sendMessage(new TranslationTextComponent("action.redwing.deployed"), Util.NIL_UUID);
                        }
                    }
                }
                break;

                case RECALL: {
                    IDroneController droneControllerCap = CapabilityHelper.getDroneControllerCap(serverPlayer);
                    if(droneControllerCap != null){
                        boolean wasDronePatrolling = droneControllerCap.isDronePatrolling();
                         if(droneControllerCap.toggleRecallDrone(serverPlayer)){
                            CaptainAmerica.LOGGER.debug("Server player {} has toggled their Redwing drone's recall!", serverPlayer.getDisplayName().getString());
                             boolean droneRecalled = droneControllerCap.isDroneRecalled();
                             boolean dronePatrolling = droneControllerCap.isDronePatrolling();
                             serverPlayer.sendMessage(new TranslationTextComponent(droneRecalled ? "action.redwing.recallOn" : "action.redwing.recallOff"), Util.NIL_UUID);
                             if(wasDronePatrolling && droneRecalled && !dronePatrolling){
                                 serverPlayer.sendMessage(new TranslationTextComponent("action.redwing.patrolOff"), Util.NIL_UUID);
                             }
                        }
                    }
                }
                break;
                case PATROL:{
                    IDroneController droneControllerCap = CapabilityHelper.getDroneControllerCap(serverPlayer);
                    if(droneControllerCap != null){
                        boolean wasDroneRecalled = droneControllerCap.isDroneRecalled();
                        if(droneControllerCap.toggleDronePatrol(serverPlayer)){
                            CaptainAmerica.LOGGER.debug("Server player {} has toggled their Redwing drone's patrol mode!", serverPlayer.getDisplayName().getString());
                            boolean dronePatrolling = droneControllerCap.isDronePatrolling();
                            boolean droneRecalled = droneControllerCap.isDroneRecalled();
                            serverPlayer.sendMessage(new TranslationTextComponent(dronePatrolling ? "action.redwing.patrolOn" : "action.redwing.patrolOff"), Util.NIL_UUID);
                            if(wasDroneRecalled && dronePatrolling && !droneRecalled){
                                serverPlayer.sendMessage(new TranslationTextComponent("action.redwing.recallOff"), Util.NIL_UUID);
                            }
                        }
                    }
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
                if(!packet.getValue().isValidForKey(packet.getKey())){
                    CaptainAmerica.LOGGER.error(
                            "Failed to set the {} ability for server player {} because {} is a child of the {} ability",
                            packet.getKey().name(),
                            serverPlayer.getDisplayName().getString(),
                            packet.getValue().name(),
                            packet.getValue().getParent());
                } else{
                    falconAbilityCap.getAbilitySelectionMap().put(packet.getKey(), packet.getValue());
                    NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SSetFalconAbilityPacket(packet.getKey(), packet.getValue()));
                    serverPlayer.sendMessage(new TranslationTextComponent("action.falcon.setAbility", packet.getKey().name(), packet.getValue().name()), Util.NIL_UUID);
                    CaptainAmerica.LOGGER.info("Server player {} has set their {} ability to {}!", serverPlayer.getDisplayName().getString(), packet.getKey().name(), packet.getValue().name());

                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
