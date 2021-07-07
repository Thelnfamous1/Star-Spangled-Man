package com.infamous.captain_america.common.util;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public enum FalconAbilityKey implements IAbilityKey{
    FLIGHT("ability.falcon.flight",
            () -> FalconAbilityValue.HALT,
            () -> FalconAbilityValue.TOGGLE_HOVER
    ),
    COMBAT("ability.falcon.combat",
            () -> FalconAbilityValue.MISSILE,
            () -> FalconAbilityValue.GRENADE
    ),
    DRONE("ability.falcon.drone",
            () -> FalconAbilityValue.DEPLOY,
            () -> FalconAbilityValue.TOGGLE_PATROL,
            () -> FalconAbilityValue.TOGGLE_RECALL
    ),
    HUD("ability.falcon.hud",
            () -> FalconAbilityValue.INFRARED,
            () -> FalconAbilityValue.NIGHT_VISION
    );

    private final String translationKey;
    private final List<Supplier<FalconAbilityValue>> children;

    @SafeVarargs
    FalconAbilityKey(String translationKey, Supplier<FalconAbilityValue>... values) {
        this.translationKey = translationKey;
        this.children = ImmutableList.copyOf(Arrays.asList(values));
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    @Override
    public List<Supplier<FalconAbilityValue>> getChildren() {
        return this.children;
    }

    @Override
    public String toString() {
        return "FalconAbilityKey{" +
                "translationKey='" + this.translationKey + '\'' +
                ", children=" + this.children +
                '}';
    }
}
