package com.infamous.captain_america.common.util;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.drone_controller.IDroneController;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.item.GogglesItem;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.server.network.packet.SFlightPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Consumer;
import java.util.function.Supplier;

public enum FalconAbilityValue implements IAbilityValue {
    HALT(
            () -> FalconAbilityKey.FLIGHT,
            KeyBindAction.INITIAL_PRESS,
            FalconAbilityValue::haltIfFlying,
            "halt"),
    TOGGLE_HOVER(
            () -> FalconAbilityKey.FLIGHT,
            KeyBindAction.INITIAL_PRESS,
            (serverPlayer) -> {
                IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(serverPlayer);
                if (falconAbilityCap == null) return;

                haltIfFlying(serverPlayer);

                boolean wasHovering = falconAbilityCap.isHovering();
                falconAbilityCap.setHovering(!falconAbilityCap.isHovering() && FalconFlightHelper.canHover(serverPlayer));
                CaptainAmerica.LOGGER.debug("Server player {} is {} hovering using an EXO-7 Falcon!", serverPlayer.getDisplayName().getString(), falconAbilityCap.isHovering() ? "" : "no longer");
                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SFlightPacket(SFlightPacket.Action.TOGGLE_HOVER, falconAbilityCap.isHovering()));
                TranslationTextComponent hoverToggleMessage = falconAbilityCap.isHovering() ? new TranslationTextComponent("action.falcon.hoverOn") : new TranslationTextComponent("action.falcon.hoverOff");
                if (wasHovering != falconAbilityCap.isHovering()) {
                    serverPlayer.sendMessage(hoverToggleMessage, Util.NIL_UUID);
                }
            },
            "toggleHover"),

    MISSILE(
            () -> FalconAbilityKey.COMBAT,
            KeyBindAction.INITIAL_PRESS,
            (serverPlayer) -> {

            },
            "missile"),

    GRENADE(
            () -> FalconAbilityKey.COMBAT,
            KeyBindAction.INITIAL_PRESS,
            (serverPlayer) -> {

            },
            "grenade"),

    DEPLOY(
            () -> FalconAbilityKey.DRONE,
            KeyBindAction.INITIAL_PRESS,
            (serverPlayer) -> {
                IDroneController droneControllerCap = CapabilityHelper.getDroneControllerCap(serverPlayer);
                if (droneControllerCap != null) {
                    if (droneControllerCap.deployDrone(serverPlayer)) {
                        CaptainAmerica.LOGGER.debug("Server player {} has deployed a Redwing drone!", serverPlayer.getDisplayName().getString());
                        serverPlayer.sendMessage(new TranslationTextComponent("action.redwing.deployed"), Util.NIL_UUID);
                    }
                }
            },
            "deploy"),
    TOGGLE_PATROL(
            () -> FalconAbilityKey.DRONE,
            KeyBindAction.INITIAL_PRESS,
            (serverPlayer) -> {
                IDroneController droneControllerCap = CapabilityHelper.getDroneControllerCap(serverPlayer);
                if (droneControllerCap != null) {
                    boolean wasDroneRecalled = droneControllerCap.isDroneRecalled();
                    if (droneControllerCap.toggleDronePatrol(serverPlayer)) {
                        CaptainAmerica.LOGGER.debug("Server player {} has toggled their Redwing drone's patrol mode!", serverPlayer.getDisplayName().getString());
                        boolean dronePatrolling = droneControllerCap.isDronePatrolling();
                        boolean droneRecalled = droneControllerCap.isDroneRecalled();
                        serverPlayer.sendMessage(new TranslationTextComponent(dronePatrolling ? "action.redwing.patrolOn" : "action.redwing.patrolOff"), Util.NIL_UUID);
                        if (wasDroneRecalled && dronePatrolling && !droneRecalled) {
                            serverPlayer.sendMessage(new TranslationTextComponent("action.redwing.recallOff"), Util.NIL_UUID);
                        }
                    }
                }
            },
            "togglePatrol"),
    TOGGLE_RECALL(
            () -> FalconAbilityKey.DRONE,
            KeyBindAction.INITIAL_PRESS,
            (serverPlayer) -> {
                IDroneController droneControllerCap = CapabilityHelper.getDroneControllerCap(serverPlayer);
                if (droneControllerCap != null) {
                    boolean wasDronePatrolling = droneControllerCap.isDronePatrolling();
                    if (droneControllerCap.toggleRecallDrone(serverPlayer)) {
                        CaptainAmerica.LOGGER.debug("Server player {} has toggled their Redwing drone's recall!", serverPlayer.getDisplayName().getString());
                        boolean droneRecalled = droneControllerCap.isDroneRecalled();
                        boolean dronePatrolling = droneControllerCap.isDronePatrolling();
                        serverPlayer.sendMessage(new TranslationTextComponent(droneRecalled ? "action.redwing.recallOn" : "action.redwing.recallOff"), Util.NIL_UUID);
                        if (wasDronePatrolling && droneRecalled && !dronePatrolling) {
                            serverPlayer.sendMessage(new TranslationTextComponent("action.redwing.patrolOff"), Util.NIL_UUID);
                        }
                    }
                }
            },
            "toggleRecall"),

    INFRARED(
            () -> FalconAbilityKey.HUD,
            KeyBindAction.INITIAL_PRESS,
            (serverPlayer) -> {

            },
            "infrared"),

    NIGHT_VISION(
            () -> FalconAbilityKey.HUD,
            KeyBindAction.INITIAL_PRESS,
            (serverPlayer) -> {

            },
            "nightVision");

    private static void haltIfFlying(ServerPlayerEntity serverPlayer) {
        if (FalconFlightHelper.isFlying(serverPlayer)) {
            FalconFlightHelper.haltFlight(serverPlayer);
            CaptainAmerica.LOGGER.debug("Server player {} has halted their EXO-7 Falcon flight!", serverPlayer.getDisplayName().getString());
        }
    }

    private final Supplier<FalconAbilityKey> parent;
    private final KeyBindAction keyBindAction;
    private final Consumer<ServerPlayerEntity> playerConsumer;
    private final String suffix;

    FalconAbilityValue(Supplier<FalconAbilityKey> parent, KeyBindAction keyBindAction, Consumer<ServerPlayerEntity> playerConsumer, String suffix) {
        this.parent = parent;
        this.keyBindAction = keyBindAction;
        this.playerConsumer = playerConsumer;
        this.suffix = suffix;
    }

    @Override
    public Supplier<FalconAbilityKey> getParent() {
        return this.parent;
    }

    @Override
    public KeyBindAction getKeyBindAction() {
        return this.keyBindAction;
    }

    @Override
    public Consumer<ServerPlayerEntity> getPlayerConsumer() {
        return this.playerConsumer;
    }

    @Override
    public String getTranslationKeySuffix() {
        return this.suffix;
    }

    @Override
    public String toString() {
        return "FalconAbilityValue{" +
                "parent=" + this.parent.get() +
                ", keyBindAction=" + this.keyBindAction +
                ", playerConsumer=" + this.playerConsumer +
                ", suffix='" + this.suffix + '\'' +
                '}';
    }
}
