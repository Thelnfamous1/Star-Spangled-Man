package com.infamous.captain_america.common.capability.falcon_ability;

import com.google.common.collect.Maps;
import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.util.FalconAbilityKey;
import com.infamous.captain_america.common.util.FalconAbilityValue;

import java.util.Map;

public class FalconAbility implements IFalconAbility {

    private final Map<FalconAbilityKey, FalconAbilityValue> abilityMap = Maps.newHashMap();
    private boolean hovering;
    private boolean verticallyFlying;
    private boolean shootingLaser;

    public FalconAbility(){
        this.abilityMap.put(FalconAbilityKey.FLIGHT, FalconAbilityValue.TOGGLE_HOVER);
        this.abilityMap.put(FalconAbilityKey.COMBAT, FalconAbilityValue.MISSILE);
        this.abilityMap.put(FalconAbilityKey.DRONE, FalconAbilityValue.DEPLOY);
        this.abilityMap.put(FalconAbilityKey.HUD, FalconAbilityValue.INFRARED);
    }

    @Override
    public boolean put(FalconAbilityKey key, FalconAbilityValue value) {
        if(key.isValidForValue(value)){
            this.abilityMap.put(key, value);
            return true;
        } else{
            CaptainAmerica.LOGGER.error(
                    "Cannot map key {} to value {} because the value is not valid for the key",
                    key.name(),
                    value.name());
            return false;
        }
    }

    @Override
    public FalconAbilityValue get(FalconAbilityKey key) {
        return this.abilityMap.get(key);
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

    @Override
    public boolean isShootingLaser() {
        return this.shootingLaser;
    }

    @Override
    public void setShootingLaser(boolean shootingLaser) {
        this.shootingLaser = shootingLaser;
    }
}
