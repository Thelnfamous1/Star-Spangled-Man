package com.infamous.captain_america.common.capability.shield_thrower;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class ShieldThrowerStorage implements Capability.IStorage<IShieldThrower> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<IShieldThrower> capability, IShieldThrower instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        return tag;
    }

    @Override
    public void readNBT(Capability<IShieldThrower> capability, IShieldThrower instance, Direction side, INBT nbt) {

    }
}