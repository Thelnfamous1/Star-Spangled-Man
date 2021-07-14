package com.infamous.captain_america.common.abilities;

import com.infamous.captain_america.common.util.ITranslatable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class AbilityType implements ITranslatable {
    // Only a-z, _ and . are allowed, meaning translation keys must be lower case.
    // Use . to separate categories in the translation key and _ to separate words.
    private static final Pattern INVALID_NAME = Pattern.compile("[^a-z_.]");
    private static final Map<String, AbilityType> VALUES = new ConcurrentHashMap<>();

    public static final AbilityType FLIGHT = getOrCreate("ability.falcon.flight");
    public static final AbilityType COMBAT = getOrCreate("ability.falcon.combat");
    public static final AbilityType DRONE = getOrCreate("ability.falcon.drone");
    public static final AbilityType HUD = getOrCreate("ability.falcon.hud");

    private final String translationKey;

    public static Set<AbilityType> getUniqueTypes(){
        return new HashSet<>(VALUES.values());
    }

    public static AbilityType getOrCreate(String translationKey)
    {
        return VALUES.computeIfAbsent(translationKey, k ->
        {
            if (INVALID_NAME.matcher(translationKey).find())
                throw new IllegalArgumentException("AbilityType.get() called with invalid translation key: " + translationKey);
            return new AbilityType(translationKey);
        });
    }

    private AbilityType(String translationKey){
        this.translationKey = translationKey;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }
}
