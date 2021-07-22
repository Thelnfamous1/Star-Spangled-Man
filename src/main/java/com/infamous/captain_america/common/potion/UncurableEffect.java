package com.infamous.captain_america.common.potion;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectCategory;

import java.util.ArrayList;
import java.util.List;

public class UncurableEffect extends CAEffect{
    public UncurableEffect(MobEffectCategory effectType, int color) {
        super(effectType, color);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<ItemStack>();
    }
}
