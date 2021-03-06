package com.infamous.captain_america.common.util;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public enum FalconAbilityKey implements IAbilityKey{
    FLIGHT("ability.falcon.flight",
            () -> FalconAbilityValue.TOGGLE_HOVER,
            () -> FalconAbilityValue.DEPLOY_FLARES,
            () -> FalconAbilityValue.ROLL,
            () -> FalconAbilityValue.FLIP
    ),
    COMBAT("ability.falcon.combat",
            () -> FalconAbilityValue.MISSILE,
            () -> FalconAbilityValue.GRENADE,
            () -> FalconAbilityValue.LASER,
            () -> FalconAbilityValue.MACHINE_GUN
    ),
    DRONE("ability.falcon.drone",
            () -> FalconAbilityValue.DEPLOY,
            () -> FalconAbilityValue.TOGGLE_PATROL,
            () -> FalconAbilityValue.TOGGLE_RECALL,
            () -> FalconAbilityValue.TOGGLE_CAMERA
    ),
    HUD("ability.falcon.hud",
            () -> FalconAbilityValue.INFRARED,
            () -> FalconAbilityValue.NIGHT_VISION,
            () -> FalconAbilityValue.ZOOM,
            () -> FalconAbilityValue.COMBAT_TRACKER
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
    public List<Supplier<FalconAbilityValue>> getChildrenSuppliers() {
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
