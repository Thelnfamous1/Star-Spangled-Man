package com.infamous.captain_america.common.abilities.util;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.abilities.Ability;
import com.infamous.captain_america.common.abilities.AbilityManager;
import com.infamous.captain_america.common.abilities.InputManager;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.drone_controller.IDroneController;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;

public class DroneAbilityManagers {

    public static final AbilityManager DEPLOY = AbilityManager.createOrReplace(Ability.DEPLOY,
            new InputManager()
                    .onInitialPress(serverPlayer -> {
                        IDroneController droneControllerCap = CapabilityHelper.getDroneControllerCap(serverPlayer);
                        if (droneControllerCap != null) {
                            if (droneControllerCap.deployDrone(serverPlayer)) {
                                CaptainAmerica.LOGGER.debug("Server player {} has deployed a Redwing drone!", serverPlayer.getDisplayName().getString());
                                serverPlayer.sendMessage(new TranslationTextComponent("action.redwing.deployed"), Util.NIL_UUID);
                            }
                        }
                    })
    );
    public static final AbilityManager TOGGLE_PATROL = AbilityManager.createOrReplace(Ability.TOGGLE_PATROL,
            new InputManager()
                    .onInitialPress(serverPlayer -> {
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
                    })
    );
    public static final AbilityManager TOGGLE_RECALL = AbilityManager.createOrReplace(Ability.TOGGLE_RECALL,
            new InputManager()
                    .onInitialPress(serverPlayer -> {
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
                    })
    );

    private DroneAbilityManagers(){

    }
}
