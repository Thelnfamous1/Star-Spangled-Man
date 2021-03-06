package com.infamous.captain_america.common.util;

import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IAbilityValue extends ITranslatable{

    Supplier<? extends IAbilityKey> getParentSupplier();

    Consumer<ServerPlayerEntity> getHandlerForKeyBindAction(KeyBindAction keyBindAction);

    default boolean isValidForKey(IAbilityKey key){
        return this.getParentSupplier().get() == key;
    }

    String getTranslationKeySuffix();

    @Override
    default String getTranslationKey(){
        return this.getParentSupplier().get().getTranslationKey() + "." + this.getTranslationKeySuffix();
    }
}
