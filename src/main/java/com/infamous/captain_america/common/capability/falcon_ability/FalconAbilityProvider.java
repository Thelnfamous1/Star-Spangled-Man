package com.infamous.captain_america.common.capability.falcon_ability;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.util.FalconAbilityKey;
import com.infamous.captain_america.common.util.FalconAbilityValue;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FalconAbilityProvider implements ICapabilitySerializable<CompoundTag> {

    @CapabilityInject(IFalconAbility.class)
    public static final Capability<IFalconAbility> FALCON_ABILITY_CAPABILITY = null;

    private LazyOptional<IFalconAbility> instance = LazyOptional.of(FalconAbility::new);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == FALCON_ABILITY_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        IFalconAbility instance = this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!"));
        CompoundTag tag = new CompoundTag();
        for(FalconAbilityKey key : FalconAbilityKey.values()){
            tag.putString(key.name(), instance.get(key).name());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        IFalconAbility instance = this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!"));
        for(FalconAbilityKey key : FalconAbilityKey.values()){
            String keyName = key.name();
            String valueName = nbt.getString(keyName);
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
    }
}