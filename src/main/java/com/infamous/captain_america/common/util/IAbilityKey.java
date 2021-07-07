package com.infamous.captain_america.common.util;

import java.util.List;

public interface IAbilityKey extends ITranslatable{

    List<? extends IAbilityValue> getChildren();

    default boolean isValidForValue(IAbilityValue value){
        return this.getChildren().contains(value);
    }

    default String getTranslationKey(IAbilityValue value){
        return this.getTranslationKey() + "." + value.getTranslationKeySuffix();
    }
}
