package com.infamous.captain_america.common.util;

import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IAbilityValue extends ITranslatable{

    Supplier<? extends IAbilityKey> getParentSupplier();

    Consumer<ServerPlayer> getHandlerForKeyBindAction(KeyBindAction keyBindAction);

    default boolean isValidForKey(IAbilityKey key){
        return this.getParentSupplier().get() == key;
    }

    String getTranslationKeySuffix();

    @Override
    default String getTranslationKey(){
        return this.getParentSupplier().get().getTranslationKey() + "." + this.getTranslationKeySuffix();
    }
}
