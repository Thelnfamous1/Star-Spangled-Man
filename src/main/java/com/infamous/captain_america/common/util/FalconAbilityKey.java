package com.infamous.captain_america.common.util;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.List;

public enum FalconAbilityKey implements IAbilityKey {
    FLIGHT("ability.falcon.flight", FalconAbilityValue.HALT, FalconAbilityValue.TOGGLE_HOVER),
    COMBAT("ability.falcon.combat", FalconAbilityValue.MISSILE),
    DRONE("ability.falcon.drone", FalconAbilityValue.DEPLOY, FalconAbilityValue.TOGGLE_PATROL, FalconAbilityValue.TOGGLE_RECALL),
    HUD("ability.falcon.hud", FalconAbilityValue.INFRARED);

    private final String translationKey;
    private final List<FalconAbilityValue> children;

    FalconAbilityKey(String translationKey, FalconAbilityValue... values) {
        this.translationKey = translationKey;
        this.children = ImmutableList.copyOf(Arrays.asList(values));
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    @Override
    public List<FalconAbilityValue> getChildren() {
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
