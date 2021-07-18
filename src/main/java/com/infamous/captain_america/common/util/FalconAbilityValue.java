package com.infamous.captain_america.common.util;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.drone_controller.IDroneController;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.entity.projectile.BulletEntity;
import com.infamous.captain_america.common.entity.projectile.CAProjectileEntity;
import com.infamous.captain_america.common.entity.projectile.MissileEntity;
import com.infamous.captain_america.common.entity.projectile.TimedGrenadeEntity;
import com.infamous.captain_america.common.item.gauntlet.WeaponGauntletItem;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.server.network.packet.SCombatPacket;
import com.infamous.captain_america.server.network.packet.SFlightPacket;
import com.infamous.captain_america.server.network.packet.SHudPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Consumer;
import java.util.function.Supplier;

public enum FalconAbilityValue implements IAbilityValue {
    /*
    HALT(
            () -> FalconAbilityKey.FLIGHT,
            FalconAbilityValue::haltIfFlying,
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            "halt"),
     */
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

    DEPLOY_FLARES(
            () -> FalconAbilityKey.FLIGHT,
            (serverPlayer) -> {
                ItemStack flareStack = CALogicHelper.createFirework(DyeColor.YELLOW, 0);
                FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(serverPlayer.level, flareStack, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), true);
                fireworkRocketEntity.setOwner(serverPlayer);
                Direction[] closestLookDirections = Direction.orderedByNearest(serverPlayer);
                Direction closestLookDirection = closestLookDirections[0];
                Direction oppositeLookDirection = closestLookDirection.getOpposite();
                fireworkRocketEntity.shoot(oppositeLookDirection.getStepX(), oppositeLookDirection.getStepY(), oppositeLookDirection.getStepZ(), 0.0F, 1.0F);
                serverPlayer.level.addFreshEntity(fireworkRocketEntity);
                serverPlayer.sendMessage(new TranslationTextComponent("action.falcon.deployFlares"), Util.NIL_UUID);
            },
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            "deployFlares"),

    BARREL_ROLL(
            () -> FalconAbilityKey.FLIGHT,
            (serverPlayer) -> {
                if(FalconFlightHelper.isFlying(serverPlayer) && !serverPlayer.isAutoSpinAttack()){
                    serverPlayer.startAutoSpinAttack(20);
                }
            },
            (serverPlayer) -> {
                if(FalconFlightHelper.isFlying(serverPlayer) && !serverPlayer.isAutoSpinAttack()){
                    serverPlayer.startAutoSpinAttack(20);
                }
            },
            (serverPlayer) -> {},
            "barrelRoll"),

    MISSILE(
            () -> FalconAbilityKey.COMBAT,
            (serverPlayer) -> {
                if(WeaponGauntletItem.isHoldingThis(serverPlayer)){
                    MissileEntity missile = new MissileEntity(serverPlayer, serverPlayer.level);
                    if (serverPlayer.abilities.instabuild) {
                        missile.pickup = CAProjectileEntity.PickupStatus.CREATIVE_ONLY;
                    }

                    Vector3d upVector = serverPlayer.getUpVector(1.0F);
                    Quaternion quaternion = new Quaternion(new Vector3f(upVector), 0, true);
                    Vector3d viewVector = serverPlayer.getViewVector(1.0F);
                    Vector3f viewVectorF = new Vector3f(viewVector);
                    viewVectorF.transform(quaternion);
                    missile.shoot((double)viewVectorF.x(), (double)viewVectorF.y(), (double)viewVectorF.z(), 3.2F, 0);

                    serverPlayer.level.addFreshEntity(missile);
                    serverPlayer.level.playSound((PlayerEntity)null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.FIREWORK_ROCKET_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    CaptainAmerica.LOGGER.info("Server player {} has fired a missile!", serverPlayer.getDisplayName().getString());
                }
            },
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            "missile"),

    GRENADE(
            () -> FalconAbilityKey.COMBAT,
            (serverPlayer) -> {
                if(WeaponGauntletItem.isHoldingThis(serverPlayer)){
                    TimedGrenadeEntity timedGrenade = new TimedGrenadeEntity(serverPlayer, serverPlayer.level);
                    if (serverPlayer.abilities.instabuild) {
                        timedGrenade.pickup = CAProjectileEntity.PickupStatus.CREATIVE_ONLY;
                    }

                    Vector3d upVector = serverPlayer.getUpVector(1.0F);
                    Quaternion quaternion = new Quaternion(new Vector3f(upVector), 0, true);
                    Vector3d viewVector = serverPlayer.getViewVector(1.0F);
                    Vector3f viewVectorF = new Vector3f(viewVector);
                    viewVectorF.transform(quaternion);
                    timedGrenade.shoot((double)viewVectorF.x(), (double)viewVectorF.y(), (double)viewVectorF.z(), 3.2F, 0);

                    serverPlayer.level.addFreshEntity(timedGrenade);
                    serverPlayer.level.playSound((PlayerEntity)null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.FIREWORK_ROCKET_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    CaptainAmerica.LOGGER.info("Server player {} has fired a timed grenade!", serverPlayer.getDisplayName().getString());
                }
            },
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            "grenade"),

    LASER(
            () -> FalconAbilityKey.COMBAT,
            (serverPlayer) -> {
                if(WeaponGauntletItem.isHoldingThis(serverPlayer)){
                    CaptainAmerica.LOGGER.debug("Server player {} has started firing their laser!", serverPlayer.getDisplayName().getString());
                    NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SCombatPacket(SCombatPacket.Action.START_LASER));
                }
            },
            (serverPlayer) -> {
                if(WeaponGauntletItem.isHoldingThis(serverPlayer)){
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
                } else {
                    CaptainAmerica.LOGGER.debug("Server player {} has stopped firing their laser!", serverPlayer.getDisplayName().getString());
                    NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SCombatPacket(SCombatPacket.Action.STOP_LASER));

                }

            },
            (serverPlayer) -> {
                CaptainAmerica.LOGGER.debug("Server player {} has stopped firing their laser!", serverPlayer.getDisplayName().getString());
                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SCombatPacket(SCombatPacket.Action.STOP_LASER));
            },
            "laser"),
    MACHINE_GUN(
            () -> FalconAbilityKey.COMBAT,
            serverPlayer -> {},
            serverPlayer -> {
                if(serverPlayer.tickCount % 5 == 0){
                    BulletEntity bulletEntity = CALogicHelper.createBullet(serverPlayer);
                    CALogicHelper.shootBullet(serverPlayer, bulletEntity, 0.0F, 1.6F);
                    NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SCombatPacket(SCombatPacket.Action.FIRING_MACHINE_GUN));
                    serverPlayer.level.addFreshEntity(bulletEntity);
                    CaptainAmerica.LOGGER.info("Server player {} has fired a bullet!", serverPlayer.getDisplayName().getString());
                }
            },
            serverPlayer -> {},
            "machineGun"),

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
            "nightVision"),

    EAGLE_EYES(
            () -> FalconAbilityKey.HUD,
            (serverPlayer) -> {
                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SHudPacket(SHudPacket.Action.TOGGLE_EAGLE_EYES));
            },
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            "eagleEyes");

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
