package com.infamous.captain_america.common.util;

import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IAbilityValue extends ITranslatable{

    Supplier<? extends IAbilityKey> getParent();

    KeyBindAction getKeyBindAction();

    Consumer<ServerPlayerEntity> getPlayerConsumer();

    default boolean isValidForKey(IAbilityKey key){
        return this.getParent() == key;
    }

    String getTranslationKeySuffix();

    @Override
    default String getTranslationKey(){
        return this.getParent().get().getTranslationKey() + "." + this.getTranslationKeySuffix();
    }
}
