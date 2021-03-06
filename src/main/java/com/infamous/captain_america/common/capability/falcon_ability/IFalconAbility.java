package com.infamous.captain_america.common.capability.falcon_ability;


import com.infamous.captain_america.common.util.FalconAbilityKey;
import com.infamous.captain_america.common.util.FalconAbilityValue;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IFalconAbility {

    boolean put(FalconAbilityKey key, FalconAbilityValue value);

    FalconAbilityValue get(FalconAbilityKey key);

    boolean isHovering();

    void setHovering(boolean hovering);

    boolean isVerticallyFlying();

    void setVerticallyFlying(boolean verticallyFlying);

    default void copyValuesFrom(IFalconAbility oldFalconAbilityCap){
        for(FalconAbilityKey abilityKey : FalconAbilityKey.values()){
            FalconAbilityValue abilityValue = oldFalconAbilityCap.get(abilityKey);
            this.put(abilityKey, abilityValue);
        }
    }

    boolean shouldRenderLaser();

    void setRenderLaser(boolean renderLaser);

    boolean isRolling();

    void setRolling(boolean rolling);

    boolean isFlipping();

    void setFlipping(boolean flipping);

    int getLastHurtById();

    void setLastHurtById(int lastHurtBy);

    int getLastHurtId();

    void setLastHurtId(int lastHurt);
}
