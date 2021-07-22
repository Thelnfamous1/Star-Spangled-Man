package com.infamous.captain_america.common.abilities.util;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.abilities.Ability;
import com.infamous.captain_america.common.abilities.AbilityManager;
import com.infamous.captain_america.common.abilities.InputManager;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import com.infamous.captain_america.server.network.packet.SFlightPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.Util;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.fml.network.PacketDistributor;

public class FlightAbilityManagers {

    /*
    public static final AbilityManager HALT = AbilityManager.createOrReplace(Ability.HALT,
            new InputManager()
                    .onInitialPress(FlightAbilityManagers::haltIfFlying)
    );
     */

    public static final AbilityManager TOGGLE_HOVER = AbilityManager.createOrReplace(Ability.TOGGLE_HOVER,
            new InputManager()
                    .onInitialPress(serverPlayer -> {
                        IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(serverPlayer);
                        if (falconAbilityCap == null) return;

                        haltIfFlying(serverPlayer);

                        boolean wasHovering = falconAbilityCap.isHovering();
                        falconAbilityCap.setHovering(!falconAbilityCap.isHovering() && FalconFlightHelper.canHover(serverPlayer));
                        CaptainAmerica.LOGGER.debug("Server player {} is {} hovering using an EXO-7 Falcon!", serverPlayer.getDisplayName().getString(), falconAbilityCap.isHovering() ? "" : "no longer");
                        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SFlightPacket(SFlightPacket.Action.TOGGLE_HOVER, falconAbilityCap.isHovering()));
                        TranslatableComponent hoverToggleMessage = falconAbilityCap.isHovering() ? new TranslatableComponent("action.falcon.hoverOn") : new TranslatableComponent("action.falcon.hoverOff");
                        if (wasHovering != falconAbilityCap.isHovering()) {
                            serverPlayer.sendMessage(hoverToggleMessage, Util.NIL_UUID);
                        }
                    })
    );

    private FlightAbilityManagers(){

    }

    private static void haltIfFlying(ServerPlayer serverPlayer) {
        if (FalconFlightHelper.isFlying(serverPlayer)) {
            FalconFlightHelper.haltFlight(serverPlayer);
            CaptainAmerica.LOGGER.debug("Server player {} has halted their EXO-7 Falcon flight!", serverPlayer.getDisplayName().getString());
        }
    }
}
