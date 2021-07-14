package com.infamous.captain_america.common.abilities;

import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.function.Consumer;

public class InputManager {
    private Consumer<ServerPlayerEntity> onInitialPress = serverPlayerEntity -> {};
    private Consumer<ServerPlayerEntity> onHeld = serverPlayerEntity -> {};
    private Consumer<ServerPlayerEntity> onRelease = serverPlayerEntity -> {};

    public InputManager(){
    }

    public InputManager onInitialPress(Consumer<ServerPlayerEntity> onInitialPress){
        this.onInitialPress = onInitialPress;
        return this;
    }

    public InputManager onHeld(Consumer<ServerPlayerEntity> onHeld){
        this.onHeld = onHeld;
        return this;
    }

    public InputManager onRelease(Consumer<ServerPlayerEntity> onRelease){
        this.onRelease = onRelease;
        return this;
    }

    public void onInitialPress(ServerPlayerEntity serverPlayer) {
        this.onInitialPress.accept(serverPlayer);
    }

    public void onHeld(ServerPlayerEntity serverPlayer) {
        this.onHeld.accept(serverPlayer);
    }

    public void onRelease(ServerPlayerEntity serverPlayer) {
        this.onRelease.accept(serverPlayer);
    }
}
