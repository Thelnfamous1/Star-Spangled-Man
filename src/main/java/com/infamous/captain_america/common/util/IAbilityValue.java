package com.infamous.captain_america.common.util;

import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.function.Consumer;

public interface IAbilityValue extends ITranslatable{

    IAbilityKey getParent();

    KeyBindAction getKeyBindAction();

    Consumer<ServerPlayerEntity> getPlayerConsumer();

    default boolean isValidForKey(IAbilityKey key){
        return this.getParent() == key;
    }

    String getTranslationKeySuffix();

    @Override
    default String getTranslationKey(){
        return this.getParent().getTranslationKey() + "." + this.getTranslationKeySuffix();
    }
}
