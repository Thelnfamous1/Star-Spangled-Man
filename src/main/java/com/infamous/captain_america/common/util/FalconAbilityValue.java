package com.infamous.captain_america.common.util;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.drone_controller.IDroneController;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.item.EXO7FalconItem;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.server.network.packet.SCombatPacket;
import com.infamous.captain_america.server.network.packet.SFlightPacket;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Consumer;
import java.util.function.Supplier;

public enum FalconAbilityValue implements IAbilityValue {
    HALT(
            () -> FalconAbilityKey.FLIGHT,
            FalconAbilityValue::haltIfFlying,
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            "halt"),
    TOGGLE_HOVER(
            () -> FalconAbilityKey.FLIGHT,
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
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            "toggleHover"),

    MISSILE(
            () -> FalconAbilityKey.COMBAT,
            (serverPlayer) -> {

            },
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            "missile"),

    GRENADE(
            () -> FalconAbilityKey.COMBAT,
            (serverPlayer) -> {

            },
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            "grenade"),

    LASER(
            () -> FalconAbilityKey.COMBAT,
            (serverPlayer) -> {
                if(!EXO7FalconItem.getEXO7FalconStack(serverPlayer).isEmpty()
                        && serverPlayer.getMainHandItem().isEmpty()){
                    IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(serverPlayer);
                    if (falconAbilityCap == null) return;

                    falconAbilityCap.setShootingLaser(true);
                    CaptainAmerica.LOGGER.debug("Server player {} has started firing their laser!", serverPlayer.getDisplayName().getString());
                    NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SCombatPacket(SCombatPacket.Action.START_LASER));
                }
            },
            (serverPlayer) -> {
                IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(serverPlayer);
                if (falconAbilityCap == null) return;

                boolean hasCorrectEquipment = !EXO7FalconItem.getEXO7FalconStack(serverPlayer).isEmpty()
                        && serverPlayer.getMainHandItem().isEmpty();
                if(falconAbilityCap.isShootingLaser()
                        && hasCorrectEquipment){
                    RayTraceResult rayTraceResult = CALogicHelper.getLaserRayTrace(serverPlayer);
                    if(rayTraceResult instanceof EntityRayTraceResult){
                        Entity target = ((EntityRayTraceResult) rayTraceResult).getEntity();
                        DamageSource laserDamageSource = DamageSource.playerAttack(serverPlayer).setIsFire();
                        boolean didHurt = target.hurt(laserDamageSource, 2.0F / 20);
                        if(didHurt){
                            target.invulnerableTime = 0;
                        }
                    } else{
                        CaptainAmerica.LOGGER.debug("Server player {} is attempting to break a block!", serverPlayer.getDisplayName().getString());
                        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SCombatPacket(SCombatPacket.Action.CONTINUE_LASER));
                    }
                } else if(falconAbilityCap.isShootingLaser() && !hasCorrectEquipment){
                    falconAbilityCap.setShootingLaser(false);
                    CaptainAmerica.LOGGER.debug("Server player {} has stopped firing their laser!", serverPlayer.getDisplayName().getString());
                    NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SCombatPacket(SCombatPacket.Action.STOP_LASER));

                }

            },
            (serverPlayer) -> {
                IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(serverPlayer);
                if (falconAbilityCap == null) return;

                falconAbilityCap.setShootingLaser(false);
                CaptainAmerica.LOGGER.debug("Server player {} has stopped firing their laser!", serverPlayer.getDisplayName().getString());
                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SCombatPacket(SCombatPacket.Action.STOP_LASER));
            },
            "laser"),

    DEPLOY(
            () -> FalconAbilityKey.DRONE,
            (serverPlayer) -> {
                IDroneController droneControllerCap = CapabilityHelper.getDroneControllerCap(serverPlayer);
                if (droneControllerCap != null) {
                    if (droneControllerCap.deployDrone(serverPlayer)) {
                        CaptainAmerica.LOGGER.debug("Server player {} has deployed a Redwing drone!", serverPlayer.getDisplayName().getString());
                        serverPlayer.sendMessage(new TranslationTextComponent("action.redwing.deployed"), Util.NIL_UUID);
                    }
                }
            },
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            "deploy"),
    TOGGLE_PATROL(
            () -> FalconAbilityKey.DRONE,
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
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            "togglePatrol"),
    TOGGLE_RECALL(
            () -> FalconAbilityKey.DRONE,
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
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            "toggleRecall"),

    INFRARED(
            () -> FalconAbilityKey.HUD,
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            "infrared"),

    NIGHT_VISION(
            () -> FalconAbilityKey.HUD,
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            "nightVision");

    private static void haltIfFlying(ServerPlayerEntity serverPlayer) {
        if (FalconFlightHelper.isFlying(serverPlayer)) {
            FalconFlightHelper.haltFlight(serverPlayer);
            CaptainAmerica.LOGGER.debug("Server player {} has halted their EXO-7 Falcon flight!", serverPlayer.getDisplayName().getString());
        }
    }

    private final Supplier<FalconAbilityKey> parent;
    private final Consumer<ServerPlayerEntity> onInitialPress;
    private final Consumer<ServerPlayerEntity> onHeld;
    private final Consumer<ServerPlayerEntity> onRelease;
    private final String suffix;

    FalconAbilityValue(Supplier<FalconAbilityKey> parent, Consumer<ServerPlayerEntity> onInitialPress, Consumer<ServerPlayerEntity> onHeld, Consumer<ServerPlayerEntity> onRelease, String suffix) {
        this.parent = parent;
        this.onInitialPress = onInitialPress;
        this.onHeld = onHeld;
        this.onRelease = onRelease;
        this.suffix = suffix;
    }

    @Override
    public Supplier<FalconAbilityKey> getParentSupplier() {
        return this.parent;
    }

    @Override
    public Consumer<ServerPlayerEntity> getHandlerForKeyBindAction(KeyBindAction keyBindAction) {
        switch (keyBindAction){
            case INITIAL_PRESS:
                return this.onInitialPress;
            case HELD:
                return this.onHeld;
            case RELEASE:
                return this.onRelease;
            default:
                return serverPlayerEntity -> {};
        }
    }

    @Override
    public String getTranslationKeySuffix() {
        return this.suffix;
    }

    @Override
    public String toString() {
        return "FalconAbilityValue{" +
                "parent=" + this.parent +
                ", onInitialPress=" + this.onInitialPress +
                ", onHeld=" + this.onHeld +
                ", onRelease=" + this.onRelease +
                ", suffix='" + this.suffix + '\'' +
                '}';
    }
}
