package com.infamous.captain_america.client.keybindings;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.network.packet.*;
import com.infamous.captain_america.client.screen.FalconAbilitySelectionScreen;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.capability.shield_thrower.IShieldThrower;
import com.infamous.captain_america.common.entity.projectile.VibraniumShieldEntity;
import com.infamous.captain_america.common.item.GogglesItem;
import com.infamous.captain_america.common.item.VibraniumShieldItem;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.common.util.FalconAbilityKey;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import com.infamous.captain_america.common.util.KeyBindAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class CAKeyBinding extends KeyBinding{

    private boolean held;
    private boolean initialPress;
    private boolean initialRelease;

    private final Consumer<ClientPlayerEntity> onInitialPress;
    private final Consumer<ClientPlayerEntity> onHeld;
    private final Consumer<ClientPlayerEntity> onInitialRelease;

    public CAKeyBinding(String description,
                        IKeyConflictContext keyConflictContext,
                        final InputMappings.Type inputType,
                        final int keyCode,
                        String category,
                        Consumer<ClientPlayerEntity> onInitialPress,
                        Consumer<ClientPlayerEntity> onHeld,
                        Consumer<ClientPlayerEntity> onInitialRelease){
        super(description, keyConflictContext, inputType, keyCode, category);
        this.onInitialPress = onInitialPress;
        this.onHeld = onHeld;
        this.onInitialRelease = onInitialRelease;
    }

    public void handleKey(ClientPlayerEntity clientPlayer){
        if(this.initialPress){
            this.onInitialPress.accept(clientPlayer);
        } else if(this.held){
            this.onHeld.accept(clientPlayer);
        } else if(this.initialRelease){
            this.onInitialRelease.accept(clientPlayer);
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

    public static final int FLIGHT_ABILITY_KEYCODE = GLFW.GLFW_KEY_R;
    public static final int COMBAT_ABILITY_KEYCODE = GLFW.GLFW_KEY_Y;
    public static final int DRONE_ABILITY_KEYCODE = GLFW.GLFW_KEY_O;
    public static final int HUD_ABILITY_KEYCODE = GLFW.GLFW_KEY_H;

    public static final int RICOCHET_THROW_SHIELD_KEYCODE = GLFW.GLFW_KEY_C;
    public static final int BOOMERANG_THROW_SHIELD_KEYCODE = GLFW.GLFW_KEY_V;

    public static final int OPEN_FALCON_SCREEN_KEYCODE = GLFW.GLFW_KEY_G;

    public static final int VERTICAL_FLIGHT_KEYCODE = GLFW.GLFW_KEY_UP;
    public static final int INVERTED_VERTICAL_FLIGHT_KEYCODE = GLFW.GLFW_KEY_DOWN;

    public static final int TOGGLE_FLIGHT_KEYCODE = GLFW.GLFW_KEY_LEFT_ALT;
    public static final int TOGGLE_HUD_KEYCODE = GLFW.GLFW_KEY_RIGHT_ALT;

    public static final String FALCON_TECH_KEY_CATEGORY = "key.categories.falconTech";
    public static final String CAP_TECH_KEY_CATEGORY = "key.categories.capTech";

    public static final Consumer<ClientPlayerEntity> SHIELD_THROW_ON_INITIAL_PRESS = (clientPlayer) -> {
        IShieldThrower shieldThrowerCap = CapabilityHelper.getShieldThrowerCap(clientPlayer);
        if (VibraniumShieldItem.hasVibraniumShield(clientPlayer) && shieldThrowerCap != null) {
            shieldThrowerCap.setShieldChargingTicks(0);
            shieldThrowerCap.setShieldChargingScale(0.0F);
        }
    };
    public static final Consumer<ClientPlayerEntity> SHIELD_THROW_ON_HELD = (clientPlayer) -> {
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
                    FLIGHT_ABILITY_KEYCODE,
                    FALCON_TECH_KEY_CATEGORY,
                    (clientPlayer) -> NetworkHandler.INSTANCE.sendToServer(new CUseAbilityPacket(KeyBindAction.INITIAL_PRESS, FalconAbilityKey.FLIGHT)),
                    (clientPlayer) -> NetworkHandler.INSTANCE.sendToServer(new CUseAbilityPacket(KeyBindAction.HELD, FalconAbilityKey.FLIGHT)),
                    (clientPlayer) -> NetworkHandler.INSTANCE.sendToServer(new CUseAbilityPacket(KeyBindAction.RELEASE, FalconAbilityKey.FLIGHT)));

    public static final CAKeyBinding keyCombatAbility =
            new CAKeyBinding(
                    "key.combatAbility",
                    KeyConflictContext.IN_GAME,
                    InputMappings.Type.KEYSYM,
                    COMBAT_ABILITY_KEYCODE,
                    FALCON_TECH_KEY_CATEGORY,
                    (clientPlayer) -> NetworkHandler.INSTANCE.sendToServer(new CUseAbilityPacket(KeyBindAction.INITIAL_PRESS, FalconAbilityKey.COMBAT)),
                    (clientPlayer) -> NetworkHandler.INSTANCE.sendToServer(new CUseAbilityPacket(KeyBindAction.HELD, FalconAbilityKey.COMBAT)),
                    (clientPlayer) -> NetworkHandler.INSTANCE.sendToServer(new CUseAbilityPacket(KeyBindAction.RELEASE, FalconAbilityKey.COMBAT)));


    public static final CAKeyBinding keyDroneAbility =
            new CAKeyBinding(
                    "key.droneAbility",
                    KeyConflictContext.IN_GAME,
                    InputMappings.Type.KEYSYM,
                    DRONE_ABILITY_KEYCODE,
                    FALCON_TECH_KEY_CATEGORY,
                    (clientPlayer) -> NetworkHandler.INSTANCE.sendToServer(new CUseAbilityPacket(KeyBindAction.INITIAL_PRESS, FalconAbilityKey.DRONE)),
                    (clientPlayer) -> NetworkHandler.INSTANCE.sendToServer(new CUseAbilityPacket(KeyBindAction.HELD, FalconAbilityKey.DRONE)),
                    (clientPlayer) -> NetworkHandler.INSTANCE.sendToServer(new CUseAbilityPacket(KeyBindAction.RELEASE, FalconAbilityKey.DRONE)));


    public static final CAKeyBinding keyHUDAbility =
            new CAKeyBinding(
                    "key.hudAbility",
                    KeyConflictContext.IN_GAME,
                    InputMappings.Type.KEYSYM,
                    HUD_ABILITY_KEYCODE,
                    FALCON_TECH_KEY_CATEGORY,
                    (clientPlayer) -> NetworkHandler.INSTANCE.sendToServer(new CUseAbilityPacket(KeyBindAction.INITIAL_PRESS, FalconAbilityKey.HUD)),
                    (clientPlayer) -> NetworkHandler.INSTANCE.sendToServer(new CUseAbilityPacket(KeyBindAction.HELD, FalconAbilityKey.HUD)),
                    (clientPlayer) -> NetworkHandler.INSTANCE.sendToServer(new CUseAbilityPacket(KeyBindAction.RELEASE, FalconAbilityKey.HUD)));

    public static final CAKeyBinding keyBoomerangThrowShield =
            new CAKeyBinding(
                    "key.boomerangThrowShield",
                    KeyConflictContext.IN_GAME,
                    InputMappings.Type.KEYSYM,
                    BOOMERANG_THROW_SHIELD_KEYCODE,
                    CAP_TECH_KEY_CATEGORY,
                    SHIELD_THROW_ON_INITIAL_PRESS,
                    SHIELD_THROW_ON_HELD,
                    (clientPlayer) -> {
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
                    RICOCHET_THROW_SHIELD_KEYCODE,
                    CAP_TECH_KEY_CATEGORY,
                    SHIELD_THROW_ON_INITIAL_PRESS,
                    SHIELD_THROW_ON_HELD,
                    (clientPlayer) -> {
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
                    VERTICAL_FLIGHT_KEYCODE,
                    FALCON_TECH_KEY_CATEGORY,
                    (clientPlayer) -> {},
                    (clientPlayer) -> {
                        IFalconAbility falconAbility = CapabilityHelper.getFalconAbilityCap(clientPlayer);
                        if(falconAbility == null) return;

                        if(falconAbility.isHovering()){
                            NetworkHandler.INSTANCE.sendToServer(new CFlightPacket(CFlightPacket.Action.VERTICAL_FLIGHT, false));
                        }
                    },
                    (clientPlayer) -> {}
                    );

    public static final CAKeyBinding keyVerticalFlightInverted =
            new CAKeyBinding(
                    "key.verticalFlightInverted",
                    KeyConflictContext.IN_GAME,
                    InputMappings.Type.KEYSYM,
                    INVERTED_VERTICAL_FLIGHT_KEYCODE,
                    FALCON_TECH_KEY_CATEGORY,
                    (clientPlayer) -> {},
                    (clientPlayer) -> {
                        IFalconAbility falconAbility = CapabilityHelper.getFalconAbilityCap(clientPlayer);
                        if(falconAbility == null) return;

                        if(falconAbility.isHovering()){
                            NetworkHandler.INSTANCE.sendToServer(new CFlightPacket(CFlightPacket.Action.VERTICAL_FLIGHT, true));
                        }
                    },
                    (clientPlayer) -> {}
            );

    public static final CAKeyBinding keyOpenFalconScreen =
            new CAKeyBinding(
                    "key.openFalconScreen",
                    KeyConflictContext.IN_GAME,
                    InputMappings.Type.KEYSYM,
                    OPEN_FALCON_SCREEN_KEYCODE,
                    FALCON_TECH_KEY_CATEGORY,
                    (clientPlayer) -> {
                        Minecraft minecraft = Minecraft.getInstance();
                        if(minecraft.screen == null
                                && minecraft.level != null
                                && !clientPlayer.isDeadOrDying()
                                && FalconFlightHelper.hasEXO7Falcon(clientPlayer)){
                            CaptainAmerica.LOGGER.info("Opening falcon screen for client player {}!", clientPlayer.getDisplayName().getString());
                            minecraft.setScreen(new FalconAbilitySelectionScreen());
                        }
                    },
                    (clientPlayer) -> {},
                    (clientPlayer) -> {}
            );

    public static final CAKeyBinding keyToggleFlight =
            new CAKeyBinding(
                   "key.toggleFlight",
                   KeyConflictContext.IN_GAME,
                   InputMappings.Type.KEYSYM,
                    TOGGLE_FLIGHT_KEYCODE,
                   FALCON_TECH_KEY_CATEGORY,
                    (clientPlayer) -> {
                        if(FalconFlightHelper.hasEXO7Falcon(clientPlayer)){
                            CaptainAmerica.LOGGER.debug("Client player {} wants to toggle their EXO-7 Falcon flight!", clientPlayer.getDisplayName().getString());
                            NetworkHandler.INSTANCE.sendToServer(new CFlightPacket(CFlightPacket.Action.TOGGLE_FLIGHT));
                        }
                    },
                    (clientPlayer) -> {},
                    (clientPlayer) -> {}
            );

    public static final CAKeyBinding keyToggleHUD =
            new CAKeyBinding(
                    "key.toggleHUD",
                    KeyConflictContext.IN_GAME,
                    InputMappings.Type.KEYSYM,
                    TOGGLE_HUD_KEYCODE,
                    FALCON_TECH_KEY_CATEGORY,
                    (clientPlayer) -> {
                        if(!GogglesItem.getGoggles(clientPlayer).isEmpty()){
                            CaptainAmerica.LOGGER.debug("Client player {} wants to toggle their HUD!", clientPlayer.getDisplayName().getString());
                            NetworkHandler.INSTANCE.sendToServer(new CHUDPacket(CHUDPacket.Action.TOGGLE_HUD));
                        }
                    },
                    (clientPlayer) -> {},
                    (clientPlayer) -> {}
            );

    public static void handleAllKeys(int key, ClientPlayerEntity clientPlayer) {
        if(key == keyOpenFalconScreen.getKey().getValue()){
            keyOpenFalconScreen.handleKey(clientPlayer);
        }
        if(key == keyFlightAbility.getKey().getValue()){
            keyFlightAbility.handleKey(clientPlayer);
        }
        if(key == keyCombatAbility.getKey().getValue()){
            keyCombatAbility.handleKey(clientPlayer);
        }
        if(key == keyDroneAbility.getKey().getValue()){
            keyDroneAbility.handleKey(clientPlayer);
        }
        if(key == keyHUDAbility.getKey().getValue()){
            keyHUDAbility.handleKey(clientPlayer);
        }
        if(key == keyVerticalFlight.getKey().getValue()){
            keyVerticalFlight.handleKey(clientPlayer);
        }
        if(key == keyVerticalFlightInverted.getKey().getValue()){
            keyVerticalFlightInverted.handleKey(clientPlayer);
        }
        if(key == keyBoomerangThrowShield.getKey().getValue()){
            keyBoomerangThrowShield.handleKey(clientPlayer);
        }
        if(key == keyRicochetThrowShield.getKey().getValue()){
            keyRicochetThrowShield.handleKey(clientPlayer);
        }
        if(key == keyToggleFlight.getKey().getValue()){
            keyToggleFlight.handleKey(clientPlayer);
        }
        if(key == keyToggleHUD.getKey().getValue()){
            keyToggleHUD.handleKey(clientPlayer);
        }
    }
}
