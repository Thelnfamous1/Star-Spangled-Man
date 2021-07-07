package com.infamous.captain_america.common.abilities;

import com.infamous.captain_america.common.util.ITranslatable;
import net.minecraft.entity.player.ServerPlayerEntity;

public abstract class Ability implements ITranslatable {

    public final AbilityType abilityType;
    public final String translationKeySuffix;

    protected Ability(AbilityType abilityType, String translationKeySuffix){
        this.abilityType = abilityType;
        this.translationKeySuffix = translationKeySuffix;
    }

    public AbilityType getAbilityType() {
        return this.abilityType;
    }

    public abstract void activate(ServerPlayerEntity serverPlayer);

    @Override
    public String getTranslationKey() {
        return this.abilityType.getTranslationKey() + "." + this.translationKeySuffix;
    }
}
