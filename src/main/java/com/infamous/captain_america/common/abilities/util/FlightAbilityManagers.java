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
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

public class FlightAbilityManagers {

    public static final AbilityManager HALT = AbilityManager.createOrReplace(Ability.HALT,
            new InputManager()
                    .onInitialPress(FlightAbilityManagers::haltIfFlying)
    );
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
                        TranslationTextComponent hoverToggleMessage = falconAbilityCap.isHovering() ? new TranslationTextComponent("action.falcon.hoverOn") : new TranslationTextComponent("action.falcon.hoverOff");
                        if (wasHovering != falconAbilityCap.isHovering()) {
                            serverPlayer.sendMessage(hoverToggleMessage, Util.NIL_UUID);
                        }
                    })
    );

    private FlightAbilityManagers(){

    }

    private static void haltIfFlying(ServerPlayerEntity serverPlayer) {
        if (FalconFlightHelper.isFlying(serverPlayer)) {
            FalconFlightHelper.haltFlight(serverPlayer);
            CaptainAmerica.LOGGER.debug("Server player {} has halted their EXO-7 Falcon flight!", serverPlayer.getDisplayName().getString());
        }
    }
}
