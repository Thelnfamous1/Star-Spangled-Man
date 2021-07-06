package com.infamous.captain_america.client.keybindings;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.network.packet.*;
import com.infamous.captain_america.client.screen.FalconAbilitySelectionScreen;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.capability.shield_thrower.IShieldThrower;
import com.infamous.captain_america.common.entity.projectile.VibraniumShieldEntity;
import com.infamous.captain_america.common.item.VibraniumShieldItem;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

public class CAKeyBinding extends KeyBinding{

    private boolean held;
    private boolean initialPress;
    private boolean initialRelease;

    private final Runnable onInitialPress;
    private final Runnable onHeld;
    private final Runnable onInitialRelease;

    public CAKeyBinding(String description,
                        IKeyConflictContext keyConflictContext,
                        final InputMappings.Type inputType,
                        final int keyCode,
                        String category,
                        Runnable onInitialPress,
                        Runnable onHeld,
                        Runnable onInitialRelease){
        super(description, keyConflictContext, inputType, keyCode, category);
        this.onInitialPress = onInitialPress;
        this.onHeld = onHeld;
        this.onInitialRelease = onInitialRelease;
    }

    public void handleKey(){
        if(this.initialPress){
            this.onInitialPress.run();
        } else if(this.held){
            this.onHeld.run();
        } else if(this.initialRelease){
            this.onInitialRelease.run();
        }
    }

    @Override
    public void setDown(boolean down) {
        boolean wasDown = this.isDown();
        super.setDown(down);
        this.initialPress = !wasDown && this.isDown();
        this.held = wasDown && this.isDown();
        this.initialRelease = wasDown && !this.isDown();
    }

    public static final int R_KEYCODE = GLFW.GLFW_KEY_R;
    public static final int O_KEYCODE = GLFW.GLFW_KEY_O;
    public static final int C_KEYCODE = GLFW.GLFW_KEY_C;
    public static final int V_KEYCODE = GLFW.GLFW_KEY_V;
    public static final int G_KEYCODE = GLFW.GLFW_KEY_G;
    public static final int UP_KEYCODE = GLFW.GLFW_KEY_UP;
    public static final int DOWN_KEYCODE = GLFW.GLFW_KEY_DOWN;

    public static final String FALCON_TECH_KEY_CATEGORY = "key.categories.falconTech";
    public static final String CAP_TECH_KEY_CATEGORY = "key.categories.capTech";

    public static final Runnable SHIELD_THROW_ON_INITIAL_PRESS = () -> {
        ClientPlayerEntity clientPlayer = getClient();
        if (clientPlayer == null) return;
        IShieldThrower shieldThrowerCap = CapabilityHelper.getShieldThrowerCap(clientPlayer);
        if (VibraniumShieldItem.hasVibraniumShield(clientPlayer) && shieldThrowerCap != null) {
            shieldThrowerCap.setShieldChargingTicks(0);
            shieldThrowerCap.setShieldChargingScale(0.0F);
        }
    };
    public static final Runnable SHIELD_THROW_ON_HELD = () -> {
        ClientPlayerEntity clientPlayer = getClient();
        if (clientPlayer == null) return;
        IShieldThrower shieldThrowerCap = CapabilityHelper.getShieldThrowerCap(clientPlayer);
        if (VibraniumShieldItem.hasVibraniumShield(clientPlayer) && shieldThrowerCap != null) {
            shieldThrowerCap.addShieldChargingTicks(1);
            if (shieldThrowerCap.getShieldChargingTicks() < 10) {
                shieldThrowerCap.setShieldChargingScale((float) shieldThrowerCap.getShieldChargingTicks() * 0.1F);
            } else {
                shieldThrowerCap.setShieldChargingScale(0.8F + 2.0F / (float) (shieldThrowerCap.getShieldChargingTicks() - 9) * 0.1F);
            }
        }
    };


    public static final CAKeyBinding keyFlightAbility =
            new CAKeyBinding(
                    "key.flightAbility",
                    KeyConflictContext.IN_GAME,
                    InputMappings.Type.KEYSYM,
                    R_KEYCODE,
                    FALCON_TECH_KEY_CATEGORY,
                    () -> {
                        ClientPlayerEntity clientPlayer = getClient();
                        if (clientPlayer == null) return;

                        IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(clientPlayer);
                        if(falconAbilityCap == null) return;

                        Map<IFalconAbility.Key, IFalconAbility.Value> abilitySelectionMap = falconAbilityCap.getAbilitySelectionMap();

                        if(abilitySelectionMap.get(IFalconAbility.Key.FLIGHT) == IFalconAbility.Value.TOGGLE_FLIGHT){
                            if(FalconFlightHelper.hasEXO7Falcon(clientPlayer)){
                                CaptainAmerica.LOGGER.debug("Client player {} wants to toggle their EXO-7 Falcon flight!", clientPlayer.getDisplayName().getString());
                                NetworkHandler.INSTANCE.sendToServer(new CFlightPacket(CFlightPacket.Action.TOGGLE_FLIGHT));
                            }
                        } else if(abilitySelectionMap.get(IFalconAbility.Key.FLIGHT) == IFalconAbility.Value.BOOST){
                            if(FalconFlightHelper.canBoostFlight(clientPlayer)){
                                CaptainAmerica.LOGGER.debug("Client player {} wants to take off using an EXO-7 Falcon!", clientPlayer.getDisplayName().getString());
                                NetworkHandler.INSTANCE.sendToServer(new CFlightPacket(CFlightPacket.Action.TAKEOFF_FLIGHT));
                            }
                        } else {
                            boolean doHover = abilitySelectionMap.get(IFalconAbility.Key.FLIGHT) == IFalconAbility.Value.TOGGLE_HOVER;
                            if(abilitySelectionMap.get(IFalconAbility.Key.FLIGHT) == IFalconAbility.Value.HALT
                                    || doHover){
                                if(FalconFlightHelper.isFlying(clientPlayer)){
                                    CaptainAmerica.LOGGER.debug("Client player {} has halted their EXO-7 Falcon flight!", clientPlayer.getDisplayName().getString());
                                    FalconFlightHelper.haltFlight(clientPlayer);
                                    NetworkHandler.INSTANCE.sendToServer(new CFlightPacket(CFlightPacket.Action.HALT_FLIGHT));
                                }
                                if(doHover){
                                    NetworkHandler.INSTANCE.sendToServer(new CFlightPacket(CFlightPacket.Action.TOGGLE_HOVER));
                                    CaptainAmerica.LOGGER.debug("Client player {} wants to hover using an EXO-7 Falcon!", clientPlayer.getDisplayName().getString());
                                }
                            }
                        }
                    },
                    () -> {
                        ClientPlayerEntity clientPlayer = getClient();
                        if (clientPlayer == null) return;

                        IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(clientPlayer);
                        if(falconAbilityCap == null) return;

                        Map<IFalconAbility.Key, IFalconAbility.Value> abilitySelectionMap = falconAbilityCap.getAbilitySelectionMap();

                        if(abilitySelectionMap.get(IFalconAbility.Key.FLIGHT) == IFalconAbility.Value.TOGGLE_HOVER){
                            if(canClientFalconFly(clientPlayer)){
                                CaptainAmerica.LOGGER.debug("Client player {} wants to hover using an EXO-7 Falcon!", clientPlayer.getDisplayName().getString());
                                NetworkHandler.INSTANCE.sendToServer(new CFlightPacket(CFlightPacket.Action.TOGGLE_HOVER));
                            }
                        } else if(abilitySelectionMap.get(IFalconAbility.Key.FLIGHT) == IFalconAbility.Value.BOOST){
                            if(FalconFlightHelper.canBoostFlight(clientPlayer)){
                                CaptainAmerica.LOGGER.debug("Client player {} wants to boost their EXO-7 Falcon flight!", clientPlayer.getDisplayName().getString());
                                NetworkHandler.INSTANCE.sendToServer(new CFlightPacket(CFlightPacket.Action.BOOST_FLIGHT));
                            }
                        }
                    },
                    () -> {
                    });


    public static final CAKeyBinding keyDroneAbility =
            new CAKeyBinding(
                    "key.droneAbility",
                    KeyConflictContext.IN_GAME,
                    InputMappings.Type.KEYSYM,
                    O_KEYCODE,
                    FALCON_TECH_KEY_CATEGORY,
                    () -> {
                        ClientPlayerEntity clientPlayer = getClient();
                        if (clientPlayer == null) return;

                        IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(clientPlayer);
                        if(falconAbilityCap == null) return;

                        Map<IFalconAbility.Key, IFalconAbility.Value> abilitySelectionMap = falconAbilityCap.getAbilitySelectionMap();
                        if(abilitySelectionMap.get(IFalconAbility.Key.DRONE) == IFalconAbility.Value.DEPLOY){
                            if(FalconFlightHelper.hasEXO7Falcon(clientPlayer)){
                                CaptainAmerica.LOGGER.debug("Client player {} wants to deploy their Redwing drone!", clientPlayer.getDisplayName().getString());
                                NetworkHandler.INSTANCE.sendToServer(new CDronePacket(CDronePacket.Action.DEPLOY));
                            }
                        } else if(abilitySelectionMap.get(IFalconAbility.Key.DRONE) == IFalconAbility.Value.TOGGLE_PATROL){
                            if(FalconFlightHelper.hasEXO7Falcon(clientPlayer)){
                                CaptainAmerica.LOGGER.debug("Client player {} wants to toggle their Redwing drone's patrol mode!", clientPlayer.getDisplayName().getString());
                                NetworkHandler.INSTANCE.sendToServer(new CDronePacket(CDronePacket.Action.PATROL));
                            }
                        } else if(abilitySelectionMap.get(IFalconAbility.Key.DRONE) == IFalconAbility.Value.TOGGLE_RECALL){
                            if(FalconFlightHelper.hasEXO7Falcon(clientPlayer)){
                                CaptainAmerica.LOGGER.debug("Client player {} wants to toggle their Redwing drone's recall!", clientPlayer.getDisplayName().getString());
                                NetworkHandler.INSTANCE.sendToServer(new CDronePacket(CDronePacket.Action.RECALL));
                            }
                        }
                    },
                    () -> {
                    },
                    () -> {
                    });

    public static final CAKeyBinding keyBoomerangThrowShield =
            new CAKeyBinding(
                    "key.boomerangThrowShield",
                    KeyConflictContext.IN_GAME,
                    InputMappings.Type.KEYSYM,
                    V_KEYCODE,
                    CAP_TECH_KEY_CATEGORY,
                    SHIELD_THROW_ON_INITIAL_PRESS,
                    SHIELD_THROW_ON_HELD,
                    () -> {
                        ClientPlayerEntity clientPlayer = getClient();
                        if (clientPlayer == null) return;
                        IShieldThrower shieldThrowerCap = CapabilityHelper.getShieldThrowerCap(clientPlayer);
                        if(VibraniumShieldItem.hasVibraniumShield(clientPlayer) && shieldThrowerCap != null){
                            CaptainAmerica.LOGGER.info("Client player {} wants to boomerang-throw their Vibranium Shield!", clientPlayer.getDisplayName().getString());
                            shieldThrowerCap.setShieldChargingTicks(-10);
                            int shieldCharge = MathHelper.floor(shieldThrowerCap.getShieldChargingScale() * 100.0F);
                            NetworkHandler.INSTANCE.sendToServer(new CShieldPacket(VibraniumShieldEntity.ThrowType.BOOMERANG_THROW, shieldCharge));
                        }
                    });

    public static final CAKeyBinding keyRicochetThrowShield =
            new CAKeyBinding(
                    "key.ricochetThrowShield",
                    KeyConflictContext.IN_GAME,
                    InputMappings.Type.KEYSYM,
                    C_KEYCODE,
                    CAP_TECH_KEY_CATEGORY,
                    SHIELD_THROW_ON_INITIAL_PRESS,
                    SHIELD_THROW_ON_HELD,
                    () -> {
                        ClientPlayerEntity clientPlayer = getClient();
                        if (clientPlayer == null) return;
                        IShieldThrower shieldThrowerCap = CapabilityHelper.getShieldThrowerCap(clientPlayer);
                        if(VibraniumShieldItem.hasVibraniumShield(clientPlayer) && shieldThrowerCap != null){
                            CaptainAmerica.LOGGER.info("Client player {} wants to ricochet-throw their Vibranium Shield!", clientPlayer.getDisplayName().getString());
                            shieldThrowerCap.setShieldChargingTicks(-10);
                            int shieldCharge = MathHelper.floor(shieldThrowerCap.getShieldChargingScale() * 100.0F);
                            NetworkHandler.INSTANCE.sendToServer(new CShieldPacket(VibraniumShieldEntity.ThrowType.RICOCHET_THROW, shieldCharge));
                        }
                    });

    public static final CAKeyBinding keyVerticalFlight =
            new CAKeyBinding(
                    "key.verticalFlight",
                    KeyConflictContext.IN_GAME,
                    InputMappings.Type.KEYSYM,
                    UP_KEYCODE,
                    FALCON_TECH_KEY_CATEGORY,
                    () -> {},
                    () -> {
                        ClientPlayerEntity clientPlayer = getClient();
                        if (clientPlayer == null) return;

                        IFalconAbility falconAbility = CapabilityHelper.getFalconAbilityCap(clientPlayer);
                        if(falconAbility == null) return;

                        if(falconAbility.isHovering()){
                            NetworkHandler.INSTANCE.sendToServer(new CFlightPacket(CFlightPacket.Action.VERTICAL_FLIGHT, false));
                        }
                    },
                    () -> {}
                    );

    public static final CAKeyBinding keyVerticalFlightInverted =
            new CAKeyBinding(
                    "key.verticalFlightInverted",
                    KeyConflictContext.IN_GAME,
                    InputMappings.Type.KEYSYM,
                    DOWN_KEYCODE,
                    FALCON_TECH_KEY_CATEGORY,
                    () -> {},
                    () -> {
                        ClientPlayerEntity clientPlayer = getClient();
                        if (clientPlayer == null) return;

                        IFalconAbility falconAbility = CapabilityHelper.getFalconAbilityCap(clientPlayer);
                        if(falconAbility == null) return;

                        if(falconAbility.isHovering()){
                            NetworkHandler.INSTANCE.sendToServer(new CFlightPacket(CFlightPacket.Action.VERTICAL_FLIGHT, true));
                        }
                    },
                    () -> {}
            );

    public static final CAKeyBinding keyOpenFalconScreen =
            new CAKeyBinding(
                    "key.openFalconScreen",
                    KeyConflictContext.IN_GAME,
                    InputMappings.Type.KEYSYM,
                    G_KEYCODE,
                    FALCON_TECH_KEY_CATEGORY,
                    () -> {
                        Minecraft minecraft = Minecraft.getInstance();
                        if(minecraft.screen == null
                                && minecraft.level != null
                                && minecraft.player != null
                                && !minecraft.player.isDeadOrDying()
                                && FalconFlightHelper.hasEXO7Falcon(minecraft.player)){
                            CaptainAmerica.LOGGER.info("Opening falcon screen for client player {}!", minecraft.player.getDisplayName().getString());
                            minecraft.setScreen(new FalconAbilitySelectionScreen());
                        }
                    },
                    () -> {},
                    () -> {}
            );

    private static ClientPlayerEntity getClient() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.player;
    }

    private static boolean canClientFalconFly(ClientPlayerEntity clientPlayer){
        return !clientPlayer.abilities.flying
                && !clientPlayer.isPassenger()
                && !clientPlayer.onClimbable();
    }

    public static void handleAllKeys(int key) {
        if(key == keyOpenFalconScreen.getKey().getValue()){
            keyOpenFalconScreen.handleKey();
        }
        if(key == keyFlightAbility.getKey().getValue()){
            keyFlightAbility.handleKey();
        }
        if(key == keyDroneAbility.getKey().getValue()){
            keyDroneAbility.handleKey();
        }
        if(key == keyVerticalFlight.getKey().getValue()){
            keyVerticalFlight.handleKey();
        }
        if(key == keyVerticalFlightInverted.getKey().getValue()){
            keyVerticalFlightInverted.handleKey();
        }
        if(key == keyBoomerangThrowShield.getKey().getValue()){
            keyBoomerangThrowShield.handleKey();
        }
        if(key == keyRicochetThrowShield.getKey().getValue()){
            keyRicochetThrowShield.handleKey();
        }
    }
}
