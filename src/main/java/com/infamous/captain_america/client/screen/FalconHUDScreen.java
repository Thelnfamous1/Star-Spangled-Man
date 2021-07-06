package com.infamous.captain_america.client.screen;

import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;

public class FalconHUDScreen extends FalconAbilityScreen{

    public FalconHUDScreen() {
        super(IFalconAbility.Key.HUD);
    }

    @Override
    protected void addButtons(int relX, int relY) {
        addFalconButton(relX, relY,10, IFalconAbility.Value.INFRARED);
    }
}
