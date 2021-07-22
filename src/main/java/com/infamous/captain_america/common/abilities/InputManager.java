package com.infamous.captain_america.common.abilities;

import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public class InputManager {
    private Consumer<ServerPlayer> onInitialPress = serverPlayerEntity -> {};
    private Consumer<ServerPlayer> onHeld = serverPlayerEntity -> {};
    private Consumer<ServerPlayer> onRelease = serverPlayerEntity -> {};

    public InputManager(){
    }

    public InputManager onInitialPress(Consumer<ServerPlayer> onInitialPress){
        this.onInitialPress = onInitialPress;
        return this;
    }

    public InputManager onHeld(Consumer<ServerPlayer> onHeld){
        this.onHeld = onHeld;
        return this;
    }

    public InputManager onRelease(Consumer<ServerPlayer> onRelease){
        this.onRelease = onRelease;
        return this;
    }

    public void onInitialPress(ServerPlayer serverPlayer) {
        this.onInitialPress.accept(serverPlayer);
    }

    public void onHeld(ServerPlayer serverPlayer) {
        this.onHeld.accept(serverPlayer);
    }

    public void onRelease(ServerPlayer serverPlayer) {
        this.onRelease.accept(serverPlayer);
    }
}
