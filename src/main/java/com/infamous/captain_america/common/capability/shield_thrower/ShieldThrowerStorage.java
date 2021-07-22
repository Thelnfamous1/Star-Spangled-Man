package com.infamous.captain_america.common.capability.shield_thrower;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

import Tag;

public class ShieldThrowerStorage implements Capability.IStorage<IShieldThrower> {

    @Nullable
    @Override
    public Tag writeNBT(Capability<IShieldThrower> capability, IShieldThrower instance, Direction side) {
        CompoundTag tag = new CompoundTag();
        return tag;
    }

    @Override
    public void readNBT(Capability<IShieldThrower> capability, IShieldThrower instance, Direction side, Tag nbt) {

    }
}