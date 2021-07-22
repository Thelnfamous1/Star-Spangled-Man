package com.infamous.captain_america.common.util;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.drone_controller.IDroneController;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.entity.drone.IVisualLinker;
import com.infamous.captain_america.common.entity.projectile.BulletEntity;
import com.infamous.captain_america.common.entity.projectile.CAProjectileEntity;
import com.infamous.captain_america.common.entity.projectile.MissileEntity;
import com.infamous.captain_america.common.entity.projectile.TimedGrenadeEntity;
import com.infamous.captain_america.common.item.GogglesItem;
import com.infamous.captain_america.common.item.gauntlet.WeaponGauntletItem;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.common.registry.EffectRegistry;
import com.infamous.captain_america.server.network.packet.SCombatPacket;
import com.infamous.captain_america.server.network.packet.SDronePacket;
import com.infamous.captain_america.server.network.packet.SFlightPacket;
import com.infamous.captain_america.server.network.packet.SHudPacket;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import java.util.Optional;
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
                //CaptainAmerica.LOGGER.debug("Server player {} is {} hovering using an EXO-7 Falcon!", serverPlayer.getDisplayName().getString(), falconAbilityCap.isHovering() ? "" : "no longer");
                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SFlightPacket(SFlightPacket.Action.TOGGLE_HOVER, falconAbilityCap.isHovering()));
                TranslatableComponent hoverToggleMessage = falconAbilityCap.isHovering() ? new TranslatableComponent("action.falcon.hoverOn") : new TranslatableComponent("action.falcon.hoverOff");
                if (wasHovering != falconAbilityCap.isHovering()) {
                    serverPlayer.sendMessage(hoverToggleMessage.withStyle(ChatFormatting.RED), Util.NIL_UUID);
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
            },
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            "deployFlares"),

    ROLL(
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
            "roll"),

    FLIP(
            () -> FalconAbilityKey.FLIGHT,
            (serverPlayer) -> {
                IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(serverPlayer);
                if(falconAbilityCap == null) return;

                if(FalconFlightHelper.canFlipFly(serverPlayer) && !serverPlayer.isAutoSpinAttack()){
                    falconAbilityCap.setFlipping(true);
                    NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SFlightPacket(SFlightPacket.Action.START_FLIP));
                }
            },
            (serverPlayer) -> {
                IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(serverPlayer);
                if(falconAbilityCap == null) return;

                if(FalconFlightHelper.canFlipFly(serverPlayer) && !serverPlayer.isAutoSpinAttack()){
                    falconAbilityCap.setFlipping(true);
                    NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SFlightPacket(SFlightPacket.Action.CONTINUE_FLIP));
                } else{
                    falconAbilityCap.setFlipping(false);
                    NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SFlightPacket(SFlightPacket.Action.STOP_FLIP));
                }
            },
            (serverPlayer) -> {
                IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(serverPlayer);
                if(falconAbilityCap == null) return;
                falconAbilityCap.setFlipping(false);
                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SFlightPacket(SFlightPacket.Action.STOP_FLIP));
                },
            "flip"),

    MISSILE(
            () -> FalconAbilityKey.COMBAT,
            (serverPlayer) -> {
                if(WeaponGauntletItem.isHoldingThis(serverPlayer)){
                    MissileEntity missile = new MissileEntity(serverPlayer, serverPlayer.level);
                    if (serverPlayer.getAbilities().instabuild) {
                        missile.pickup = CAProjectileEntity.PickupStatus.CREATIVE_ONLY;
                    }

                    Vec3 upVector = serverPlayer.getUpVector(1.0F);
                    Quaternion quaternion = new Quaternion(new Vector3f(upVector), 0, true);
                    Vec3 viewVector = serverPlayer.getViewVector(1.0F);
                    Vector3f viewVectorF = new Vector3f(viewVector);
                    viewVectorF.transform(quaternion);
                    missile.shoot((double)viewVectorF.x(), (double)viewVectorF.y(), (double)viewVectorF.z(), 3.2F, 0);

                    serverPlayer.level.addFreshEntity(missile);
                    serverPlayer.level.playSound((Player)null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.FIREWORK_ROCKET_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
                    //CaptainAmerica.LOGGER.info("Server player {} has fired a missile!", serverPlayer.getDisplayName().getString());
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
                    if (serverPlayer.getAbilities().instabuild) {
                        timedGrenade.pickup = CAProjectileEntity.PickupStatus.CREATIVE_ONLY;
                    }

                    Vec3 upVector = serverPlayer.getUpVector(1.0F);
                    Quaternion quaternion = new Quaternion(new Vector3f(upVector), 0, true);
                    Vec3 viewVector = serverPlayer.getViewVector(1.0F);
                    Vector3f viewVectorF = new Vector3f(viewVector);
                    viewVectorF.transform(quaternion);
                    timedGrenade.shoot((double)viewVectorF.x(), (double)viewVectorF.y(), (double)viewVectorF.z(), 3.2F, 0);

                    serverPlayer.level.addFreshEntity(timedGrenade);
                    serverPlayer.level.playSound((Player)null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.FIREWORK_ROCKET_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
                    //CaptainAmerica.LOGGER.info("Server player {} has fired a timed grenade!", serverPlayer.getDisplayName().getString());
                }
            },
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            "grenade"),

    LASER(
            () -> FalconAbilityKey.COMBAT,
            (serverPlayer) -> {
                if(WeaponGauntletItem.isHoldingThis(serverPlayer)){
                    //CaptainAmerica.LOGGER.debug("Server player {} has started firing their laser!", serverPlayer.getDisplayName().getString());
                    NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SCombatPacket(SCombatPacket.Action.START_LASER));
                }
            },
            (serverPlayer) -> {
                if(WeaponGauntletItem.isHoldingThis(serverPlayer)){
                    HitResult rayTraceResult = CALogicHelper.getLaserRayTrace(serverPlayer);
                    if(rayTraceResult instanceof EntityHitResult){
                        Entity target = ((EntityHitResult) rayTraceResult).getEntity();
                        DamageSource laserDamageSource = DamageSource.playerAttack(serverPlayer).setIsFire();
                        boolean didHurt = target.hurt(laserDamageSource, 2.0F / 20);
                        if(didHurt){
                            target.invulnerableTime = 0;
                        }
                    } else{
                        //CaptainAmerica.LOGGER.debug("Server player {} is attempting to break a block!", serverPlayer.getDisplayName().getString());
                        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SCombatPacket(SCombatPacket.Action.CONTINUE_LASER));
                    }
                } else {
                    //CaptainAmerica.LOGGER.debug("Server player {} has stopped firing their laser!", serverPlayer.getDisplayName().getString());
                    NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SCombatPacket(SCombatPacket.Action.STOP_LASER));

                }

            },
            (serverPlayer) -> {
                //CaptainAmerica.LOGGER.debug("Server player {} has stopped firing their laser!", serverPlayer.getDisplayName().getString());
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
                    //CaptainAmerica.LOGGER.info("Server player {} has fired a bullet!", serverPlayer.getDisplayName().getString());
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
                        //CaptainAmerica.LOGGER.debug("Server player {} has deployed a Redwing drone!", serverPlayer.getDisplayName().getString());
                        serverPlayer.sendMessage(new TranslatableComponent("action.redwing.deployed").withStyle(ChatFormatting.RED), Util.NIL_UUID);
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
                        //CaptainAmerica.LOGGER.debug("Server player {} has toggled their Redwing drone's patrol mode!", serverPlayer.getDisplayName().getString());
                        boolean dronePatrolling = droneControllerCap.isDronePatrolling();
                        boolean droneRecalled = droneControllerCap.isDroneRecalled();
                        serverPlayer.sendMessage(new TranslatableComponent(dronePatrolling ? "action.redwing.patrolOn" : "action.redwing.patrolOff").withStyle(ChatFormatting.RED), Util.NIL_UUID);
                        if (wasDroneRecalled && dronePatrolling && !droneRecalled) {
                            serverPlayer.sendMessage(new TranslatableComponent("action.redwing.recallOff").withStyle(ChatFormatting.RED), Util.NIL_UUID);
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
                        //CaptainAmerica.LOGGER.debug("Server player {} has toggled their Redwing drone's recall!", serverPlayer.getDisplayName().getString());
                        boolean droneRecalled = droneControllerCap.isDroneRecalled();
                        boolean dronePatrolling = droneControllerCap.isDronePatrolling();
                        serverPlayer.sendMessage(new TranslatableComponent(droneRecalled ? "action.redwing.recallOn" : "action.redwing.recallOff").withStyle(ChatFormatting.RED), Util.NIL_UUID);
                        if (wasDronePatrolling && droneRecalled && !dronePatrolling) {
                            serverPlayer.sendMessage(new TranslatableComponent("action.redwing.patrolOff").withStyle(ChatFormatting.RED), Util.NIL_UUID);
                        }
                    }
                }
            },
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            "toggleRecall"),
    TOGGLE_CAMERA(
            () -> FalconAbilityKey.DRONE,
            (serverPlayer) -> {

                if(GogglesItem.getGoggles(serverPlayer).isEmpty()){
                    return;
                }

                IDroneController droneControllerCap = CapabilityHelper.getDroneControllerCap(serverPlayer);
                if (droneControllerCap != null) {
                    Optional<? extends Entity> optionalDrone = droneControllerCap.getDeployedDrone(serverPlayer);
                    if(optionalDrone.isPresent()) {
                        Entity deployedDrone = optionalDrone.get();
                        if (deployedDrone instanceof IVisualLinker visualLinker && deployedDrone.isAlive()) {
                            visualLinker.setVisualLink(!visualLinker.hasVisualLink());
                            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SDronePacket(SDronePacket.Action.TOGGLE_CAMERA, deployedDrone.getId(), visualLinker.hasVisualLink()));
                        }
                    }
                }
            },
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            "toggleCamera"),

    INFRARED(
            () -> FalconAbilityKey.HUD,
            (serverPlayer) -> {
                toggleVisionEffect(serverPlayer, EffectRegistry.HUD_INFRARED.get());
            },
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            "infrared"),

    NIGHT_VISION(
            () -> FalconAbilityKey.HUD,
            (serverPlayer) -> {
                toggleVisionEffect(serverPlayer, EffectRegistry.HUD_NIGHT_VISION.get());
            },
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            "nightVision"),

    ZOOM(
            () -> FalconAbilityKey.HUD,
            (serverPlayer) -> {
                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SHudPacket(SHudPacket.Action.TOGGLE_EAGLE_EYES));
            },
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            "zoom"),

    COMBAT_TRACKER(
            () -> FalconAbilityKey.HUD,
            (serverPlayer) -> {
                toggleVisionEffect(serverPlayer, EffectRegistry.HUD_COMBAT_TRACKER.get());
            },
            (serverPlayer) -> {},
            (serverPlayer) -> {},
            "combatTracker");

    private static void toggleVisionEffect(LivingEntity living, MobEffect visionEffect) {
        Optional<ItemStack> optionalGoggles = GogglesItem.getGoggles(living);
        boolean isHudEnabled = optionalGoggles.isPresent() && GogglesItem.isHUDEnabled(optionalGoggles.get());
        if (isHudEnabled) {
            boolean hasVisionEffect = living.hasEffect(visionEffect);
            if(!hasVisionEffect){
                MobEffectInstance visionEffectInstance = new MobEffectInstance(visionEffect, 120000, 0, false, false, false);
                living.addEffect(visionEffectInstance);
            } else {
                living.removeEffect(visionEffect);
            }
        }
    }

    private static void haltIfFlying(ServerPlayer serverPlayer) {
        if (FalconFlightHelper.isFlying(serverPlayer)) {
            FalconFlightHelper.haltFlight(serverPlayer);
            CaptainAmerica.LOGGER.debug("Server player {} has halted their EXO-7 Falcon flight!", serverPlayer.getDisplayName().getString());
        }
    }

    private final Supplier<FalconAbilityKey> parent;
    private final Consumer<ServerPlayer> onInitialPress;
    private final Consumer<ServerPlayer> onHeld;
    private final Consumer<ServerPlayer> onRelease;
    private final String suffix;

    FalconAbilityValue(Supplier<FalconAbilityKey> parent, Consumer<ServerPlayer> onInitialPress, Consumer<ServerPlayer> onHeld, Consumer<ServerPlayer> onRelease, String suffix) {
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
    public Consumer<ServerPlayer> getHandlerForKeyBindAction(KeyBindAction keyBindAction) {
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
