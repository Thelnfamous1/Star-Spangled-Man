package com.infamous.captain_america.common.abilities;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class AbilityManager {
    private static final Map<Ability, AbilityManager> VALUES = new ConcurrentHashMap<>();

    private final Ability ability;
    private final InputManager inputManager;

    private AbilityManager(Ability ability, InputManager inputManager){
        this.ability = ability;
        this.inputManager = inputManager;
    }

    public static AbilityManager createOrReplace(Ability ability, InputManager inputManager)
    {
        VALUES.put(ability, new AbilityManager(ability, inputManager));
        return VALUES.get(ability);
    }

    public static Optional<AbilityManager> get(Ability ability)
    {
        return Optional.ofNullable(VALUES.get(ability));
    }

    public Ability getAbility() {
        return this.ability;
    }

    public InputManager getInputManager() {
        return inputManager;
    }
}
