package com.infamous.captain_america.common.abilities;

import com.infamous.captain_america.common.util.ITranslatable;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class Ability implements ITranslatable {
    // Only a-z and _ are allowed, meaning names must be lower case.
    // Use _ to separate words.
    private static final Pattern INVALID_NAME = Pattern.compile("[^a-z_]");
    private static final Map<String, Ability> VALUES = new ConcurrentHashMap<>();

    public static final Ability HALT = createOrReplace(AbilityType.FLIGHT, "halt");
    public static final Ability TOGGLE_HOVER = createOrReplace(AbilityType.FLIGHT, "toggle_hover");

    public static final Ability MISSILE = createOrReplace(AbilityType.COMBAT, "missile");
    public static final Ability GRENADE = createOrReplace(AbilityType.COMBAT, "grenade");
    public static final Ability LASER = createOrReplace(AbilityType.COMBAT, "laser");

    public static final Ability DEPLOY = createOrReplace(AbilityType.DRONE, "deploy");
    public static final Ability TOGGLE_PATROL = createOrReplace(AbilityType.DRONE, "toggle_patrol");
    public static final Ability TOGGLE_RECALL = createOrReplace(AbilityType.DRONE, "toggle_recall");

    public static final Ability INFRARED = createOrReplace(AbilityType.HUD, "infrared");
    public static final Ability NIGHT_VISION = createOrReplace(AbilityType.HUD, "night_vision");

    private final AbilityType abilityType;
    private final String name;

    public static Set<Ability> getUniqueAbilities(){
        return new HashSet<>(VALUES.values());
    }

    public static Ability createOrReplace(AbilityType abilityType, String name)
    {
        if (INVALID_NAME.matcher(name).find())
        throw new IllegalArgumentException("Ability.get() called with invalid name: " + name);

        VALUES.put(name, new Ability(abilityType, name));
        return VALUES.get(name);
    }

    public static Optional<Ability> get(String name)
    {
        return Optional.ofNullable(VALUES.get(name));
    }

    private Ability(AbilityType abilityType, String name){
        this.abilityType = abilityType;
        this.name = name;
    }

    public AbilityType getAbilityType() {
        return this.abilityType;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String getTranslationKey() {
        return this.abilityType.getTranslationKey() + "." + this.name;
    }
}
