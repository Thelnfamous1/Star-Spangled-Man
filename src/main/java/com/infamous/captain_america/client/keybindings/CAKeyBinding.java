package com.infamous.captain_america.client.keybindings;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.network.packet.*;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import com.infamous.captain_america.common.util.VibraniumShieldHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

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

    public static final int SPACEBAR_KEYCODE = GLFW.GLFW_KEY_SPACE;
    public static final int R_KEYCODE = GLFW.GLFW_KEY_R;
    public static final int O_KEYCODE = GLFW.GLFW_KEY_O;
    public static final int I_KEYCODE = GLFW.GLFW_KEY_I;
    public static final int K_KEYCODE = GLFW.GLFW_KEY_K;
    public static final int V_KEYCODE = GLFW.GLFW_KEY_V;

    public static final String FALCON_TECH_KEY_CATEGORY = "key.categories.falconTech";
    public static final String CAP_TECH_KEY_CATEGORY = "key.categories.capTech";

    public static final CAKeyBinding keyHover =
            new CAKeyBinding(
                    "key.hover",
                    KeyConflictContext.IN_GAME,
                    InputMappings.Type.KEYSYM,
                    R_KEYCODE,
                    FALCON_TECH_KEY_CATEGORY,
                    () -> {
                    },
                    () -> {
                        ClientPlayerEntity clientPlayer = getClient();
                        if (clientPlayer == null) return;
                        if(FalconFlightHelper.canHover(clientPlayer) && canClientHover(clientPlayer)){
                            CaptainAmerica.LOGGER.debug("Client player {} wants to hover using an EXO-7 Falcon!", clientPlayer.getDisplayName().getString());
                            NetworkHandler.INSTANCE.sendToServer(new CFlightPacket(CFlightPacket.Action.HOVER));
                        }
                    },
                    () -> {
                    });

    public static final CAKeyBinding keyHaltFlight =
            new CAKeyBinding(
                    "key.haltFlight",
                    KeyConflictContext.IN_GAME,
                    InputMappings.Type.KEYSYM,
                    R_KEYCODE,
                    FALCON_TECH_KEY_CATEGORY,
                    () -> {
                        ClientPlayerEntity clientPlayer = getClient();
                        if (clientPlayer == null) return;
                        if(FalconFlightHelper.isFlying(clientPlayer)){
                            CaptainAmerica.LOGGER.debug("Client player {} has halted their EXO-7 Falcon flight!", clientPlayer.getDisplayName().getString());
                            FalconFlightHelper.haltFlight(clientPlayer);
                            NetworkHandler.INSTANCE.sendToServer(new CFlightPacket(CFlightPacket.Action.HALT_FLIGHT));
                        }
                    },
                    () -> {
                    },
                    () -> {
                    });

    public static final CAKeyBinding keyBoostFlight =
            new CAKeyBinding(
                    "key.boostFlight",
                    KeyConflictContext.IN_GAME,
                    InputMappings.Type.KEYSYM,
                    SPACEBAR_KEYCODE,
                    FALCON_TECH_KEY_CATEGORY,
                    () -> {
                        ClientPlayerEntity clientPlayer = getClient();
                        if (clientPlayer == null) return;
                        if(FalconFlightHelper.canTakeOff(clientPlayer) || FalconFlightHelper.canBoostFlight(clientPlayer)){
                            CaptainAmerica.LOGGER.debug("Client player {} wants to take off using an EXO-7 Falcon!", clientPlayer.getDisplayName().getString());
                            NetworkHandler.INSTANCE.sendToServer(new CFlightPacket(CFlightPacket.Action.TAKEOFF_FLIGHT));
                        }
                    },
                    () -> {
                        ClientPlayerEntity clientPlayer = getClient();
                        if (clientPlayer == null) return;
                        if(FalconFlightHelper.canBoostFlight(clientPlayer)){
                            CaptainAmerica.LOGGER.debug("Client player {} wants to boost their EXO-7 Falcon flight!", clientPlayer.getDisplayName().getString());
                            NetworkHandler.INSTANCE.sendToServer(new CFlightPacket(CFlightPacket.Action.BOOST_FLIGHT));
                        }
                    },
                    () -> {
                    });

    public static final CAKeyBinding keyDeployRedwing =
            new CAKeyBinding(
                    "key.deployRedwing",
                    KeyConflictContext.IN_GAME,
                    InputMappings.Type.KEYSYM,
                    O_KEYCODE,
                    FALCON_TECH_KEY_CATEGORY,
                    () -> {
                        ClientPlayerEntity clientPlayer = getClient();
                        if (clientPlayer == null) return;
                        if(FalconFlightHelper.hasEXO7Falcon(clientPlayer)){
                            CaptainAmerica.LOGGER.debug("Client player {} wants to deploy their Redwing drone!", clientPlayer.getDisplayName().getString());
                            NetworkHandler.INSTANCE.sendToServer(new CRedwingPacket(CRedwingPacket.Action.DEPLOY));
                        }
                    },
                    () -> {
                    },
                    () -> {
                    });

    public static final CAKeyBinding keyToggleRedwingRecall =
            new CAKeyBinding(
                    "key.toggleRedwingRecall",
                    KeyConflictContext.IN_GAME,
                    InputMappings.Type.KEYSYM,
                    K_KEYCODE,
                    FALCON_TECH_KEY_CATEGORY,
                    () -> {
                        ClientPlayerEntity clientPlayer = getClient();
                        if (clientPlayer == null) return;
                        if(FalconFlightHelper.hasEXO7Falcon(clientPlayer)){
                            CaptainAmerica.LOGGER.debug("Client player {} wants to toggle their Redwing drone's recall!", clientPlayer.getDisplayName().getString());
                            NetworkHandler.INSTANCE.sendToServer(new CRedwingPacket(CRedwingPacket.Action.RECALL));
                        }
                    },
                    () -> {
                    },
                    () -> {
                    });

    public static final CAKeyBinding keyTogglePatrolRedwing =
            new CAKeyBinding(
                    "key.toggleRedwingPatrol",
                    KeyConflictContext.IN_GAME,
                    InputMappings.Type.KEYSYM,
                    I_KEYCODE,
                    FALCON_TECH_KEY_CATEGORY,
                    () -> {
                        ClientPlayerEntity clientPlayer = getClient();
                        if (clientPlayer == null) return;
                        if(FalconFlightHelper.hasEXO7Falcon(clientPlayer)){
                            CaptainAmerica.LOGGER.debug("Client player {} wants to toggle their Redwing drone's patrol mode!", clientPlayer.getDisplayName().getString());
                            NetworkHandler.INSTANCE.sendToServer(new CRedwingPacket(CRedwingPacket.Action.PATROL));
                        }
                    },
                    () -> {
                    },
                    () -> {
                    });

    public static final CAKeyBinding keyThrowVibraniumShield =
            new CAKeyBinding(
                    "key.throwVibraniumShield",
                    KeyConflictContext.IN_GAME,
                    InputMappings.Type.KEYSYM,
                    V_KEYCODE,
                    CAP_TECH_KEY_CATEGORY,
                    () -> {
                        ClientPlayerEntity clientPlayer = getClient();
                        if (clientPlayer == null) return;
                        if(VibraniumShieldHelper.hasVibraniumShield(clientPlayer)){
                            CaptainAmerica.LOGGER.info("Client player {} wants to throw their Vibranium Shield!", clientPlayer.getDisplayName().getString());
                            NetworkHandler.INSTANCE.sendToServer(new CShieldPacket(CShieldPacket.Action.THROW_SHIELD));
                        }
                    },
                    () -> {
                    },
                    () -> {
                    });

    private static ClientPlayerEntity getClient() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.player;
    }

    private static boolean canClientHover(ClientPlayerEntity clientPlayer){
        return !clientPlayer.abilities.flying
                && !clientPlayer.isPassenger()
                && !clientPlayer.onClimbable();
    }
}
