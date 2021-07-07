package com.infamous.captain_america.common.abilities;

import com.infamous.captain_america.common.util.ITranslatable;

import java.util.Arrays;
import java.util.List;

public class AbilityType implements ITranslatable {

    private final String translationKey;
    private final List<Ability> validAbilities;

    public AbilityType(String translationKey, Ability... validAbilities){
        this.translationKey = translationKey;
        this.validAbilities = Arrays.asList(validAbilities);
    }

    public List<Ability> getValidAbilities() {
        return this.validAbilities;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }
}
