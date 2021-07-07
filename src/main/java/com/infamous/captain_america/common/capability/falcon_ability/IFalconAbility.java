package com.infamous.captain_america.common.capability.falcon_ability;


import com.infamous.captain_america.common.util.FalconAbilityKey;
import com.infamous.captain_america.common.util.FalconAbilityValue;

public interface IFalconAbility {

    void put(FalconAbilityKey key, FalconAbilityValue value);

    FalconAbilityValue get(FalconAbilityKey key);

    boolean isHovering();

    void setHovering(boolean hovering);

    boolean isVerticallyFlying();

    void setVerticallyFlying(boolean verticallyFlying);

}
