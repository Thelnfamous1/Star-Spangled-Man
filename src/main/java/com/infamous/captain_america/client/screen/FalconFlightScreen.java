package com.infamous.captain_america.client.screen;

import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;

public class FalconFlightScreen extends FalconAbilityScreen{

    public FalconFlightScreen() {
        super(IFalconAbility.Key.FLIGHT);
    }

    @Override
    protected void addButtons(int relX, int relY) {
        addFalconButton(relX, relY,10, IFalconAbility.Value.TOGGLE_FLIGHT);
        addFalconButton(relX, relY,30,IFalconAbility.Value.BOOST);
        addFalconButton(relX, relY,50,IFalconAbility.Value.HALT);
        addFalconButton(relX, relY,70,IFalconAbility.Value.TOGGLE_HOVER);
    }
}
