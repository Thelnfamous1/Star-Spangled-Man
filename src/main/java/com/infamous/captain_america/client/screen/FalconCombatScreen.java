package com.infamous.captain_america.client.screen;

import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;

public class FalconCombatScreen extends FalconAbilityScreen{

    public FalconCombatScreen() {
        super(IFalconAbility.Key.COMBAT);
    }

    @Override
    protected void addButtons(int relX, int relY) {
        addFalconButton(relX, relY,10, IFalconAbility.Value.MISSILE);
    }
}
