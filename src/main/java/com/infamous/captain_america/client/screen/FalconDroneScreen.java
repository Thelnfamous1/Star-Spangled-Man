package com.infamous.captain_america.client.screen;

import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;

public class FalconDroneScreen extends FalconAbilityScreen{

    public FalconDroneScreen() {
        super(IFalconAbility.Key.DRONE);
    }

    @Override
    protected void addButtons(int relX, int relY) {
        addFalconButton(relX, relY,10, IFalconAbility.Value.DEPLOY);
        addFalconButton(relX, relY,30, IFalconAbility.Value.TOGGLE_PATROL);
        addFalconButton(relX, relY,50, IFalconAbility.Value.TOGGLE_RECALL);
    }
}
