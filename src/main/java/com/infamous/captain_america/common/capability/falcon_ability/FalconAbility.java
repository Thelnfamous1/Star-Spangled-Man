package com.infamous.captain_america.common.capability.falcon_ability;

import com.google.common.collect.Maps;

import java.util.Map;

public class FalconAbility implements IFalconAbility {

    private final Map<Key, Value> abilityMap = Maps.newHashMap();
    private boolean hovering;
    private boolean verticallyFlying;

    public FalconAbility(){
        this.abilityMap.put(Key.FLIGHT, Value.BOOST);
        this.abilityMap.put(Key.COMBAT, Value.MISSILE);
        this.abilityMap.put(Key.DRONE, Value.DEPLOY);
        this.abilityMap.put(Key.HUD, Value.INFRARED);
    }

    @Override
    public Map<Key, Value> getAbilitySelectionMap() {
        return this.abilityMap;
    }

    @Override
    public boolean isHovering() {
        return this.hovering;
    }

    @Override
    public void setHovering(boolean hovering) {
        this.hovering = hovering;
    }

    @Override
    public boolean isVerticallyFlying() {
        return this.verticallyFlying;
    }

    @Override
    public void setVerticallyFlying(boolean verticallyFlying) {
        this.verticallyFlying = verticallyFlying;
    }
}
