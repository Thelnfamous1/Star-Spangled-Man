package com.infamous.captain_america.common.potion;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectType;

import java.util.ArrayList;
import java.util.List;

public class UncurableEffect extends CAEffect{
    public UncurableEffect(EffectType effectType, int color) {
        super(effectType, color);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<ItemStack>();
    }
}
