package com.infamous.captain_america.common.capability.falcon_ability;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.util.FalconAbilityKey;
import com.infamous.captain_america.common.util.FalconAbilityValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

import Tag;

public class FalconAbilityStorage implements Capability.IStorage<IFalconAbility> {

    public static final String HOVERING = "hovering";

    @Nullable
    @Override
    public Tag writeNBT(Capability<IFalconAbility> capability, IFalconAbility instance, Direction side) {
        CompoundTag tag = new CompoundTag();
        for(FalconAbilityKey key : FalconAbilityKey.values()){
            tag.putString(key.name(), instance.get(key).name());
        }
        //tag.putBoolean(HOVERING, instance.isHovering());
        return tag;
    }

    @Override
    public void readNBT(Capability<IFalconAbility> capability, IFalconAbility instance, Direction side, Tag nbt) {
        CompoundTag tag = (CompoundTag) nbt;
        for(FalconAbilityKey key : FalconAbilityKey.values()){
            String keyName = key.name();
            String valueName = tag.getString(keyName);
            FalconAbilityValue value = null;
            try{
                value = FalconAbilityValue.valueOf(valueName);
            } catch (IllegalArgumentException e){
                CaptainAmerica.LOGGER.info("Unable to load stored value for key {}", key.name());
            }
            if(value != null){
                instance.put(key, value);
            }
        }
        //instance.setHovering(tag.getBoolean(HOVERING));
    }
}