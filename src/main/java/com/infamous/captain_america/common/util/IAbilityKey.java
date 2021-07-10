package com.infamous.captain_america.common.util;

import java.util.List;
import java.util.function.Supplier;

public interface IAbilityKey extends ITranslatable{

    List<? extends Supplier<? extends IAbilityValue>> getChildrenSuppliers();

    default boolean isValidForValue(IAbilityValue value){
        for(Supplier<? extends IAbilityValue> abilityValue : this.getChildrenSuppliers()){
            if(abilityValue.get() == value){
                return true;
            }
        }
        return false;
    }

    default String getTranslationKey(IAbilityValue value){
        return this.getTranslationKey() + "." + value.getTranslationKeySuffix();
    }
}
