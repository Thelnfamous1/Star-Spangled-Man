package com.infamous.captain_america.common.abilities.util;

import com.infamous.captain_america.common.abilities.Ability;
import com.infamous.captain_america.common.abilities.AbilityManager;
import com.infamous.captain_america.common.abilities.InputManager;

public class HUDAbilityManagers {

    public static final AbilityManager INFRARED = AbilityManager.createOrReplace(Ability.INFRARED,
            new InputManager()
    );
    public static final AbilityManager NIGHT_VISION = AbilityManager.createOrReplace(Ability.NIGHT_VISION,
            new InputManager()
    );

    private HUDAbilityManagers(){

    }
}
