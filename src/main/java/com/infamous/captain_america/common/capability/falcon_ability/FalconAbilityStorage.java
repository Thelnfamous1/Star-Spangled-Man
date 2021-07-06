package com.infamous.captain_america.common.capability.falcon_ability;

import com.infamous.captain_america.CaptainAmerica;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class FalconAbilityStorage implements Capability.IStorage<IFalconAbility> {

    public static final String HOVERING = "hovering";

    @Nullable
    @Override
    public INBT writeNBT(Capability<IFalconAbility> capability, IFalconAbility instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        instance.getAbilitySelectionMap()
                .forEach(
                        (key, value) -> {
                            String keyName = null;
                            try{
                                keyName = key.name();
                            } catch(IllegalArgumentException e){
                                CaptainAmerica.LOGGER.error("Invalid falcon ability key: {}", key);
                            }

                            String valueName = null;
                            try{
                                valueName = value.name();
                            } catch (IllegalArgumentException e){
                                CaptainAmerica.LOGGER.error("Invalid falcon ability value: {}", value);
                            }

                            if(keyName != null && valueName != null){
                                tag.putString(keyName, valueName);
                            }
                        }
                );
        tag.putBoolean(HOVERING, instance.isHovering());
        return tag;
    }

    @Override
    public void readNBT(Capability<IFalconAbility> capability, IFalconAbility instance, Direction side, INBT nbt) {
        CompoundNBT tag = (CompoundNBT) nbt;
        tag.getAllKeys()
                .stream()
                .filter(s -> !s.equals(HOVERING))
                .forEach(s -> {

                    IFalconAbility.Key key = null;
                    try{
                        key = IFalconAbility.Key.valueOf(s);
                    } catch(IllegalArgumentException e){
                        CaptainAmerica.LOGGER.error("Invalid falcon ability key: {}", s);
                    }

                    IFalconAbility.Value value = null;
                    try{
                        value = IFalconAbility.Value.valueOf(tag.getString(s));
                    } catch (IllegalArgumentException e){
                        CaptainAmerica.LOGGER.error("Invalid falcon ability value: {}", s);
                    }

                    if(key != null && value != null){
                        if(!value.isValidForKey(key)){
                            CaptainAmerica.LOGGER.error(
                                    "Invalid falcon ability pairing: {} cannot be paired with {} because it is a child of {}",
                                    key.name(),
                                    value.name(),
                                    value.getParent().name());
                        } else {
                            instance.getAbilitySelectionMap().put(key, value);
                        }
                    }
                });
        instance.setHovering(tag.getBoolean(HOVERING));
    }
}