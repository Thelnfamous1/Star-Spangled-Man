package com.infamous.captain_america.common.potion;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectType;

import java.util.ArrayList;
import java.util.List;

public class SuperSoldierEffect extends CAEffect{
    public SuperSoldierEffect() {
        super(EffectType.BENEFICIAL, 0x0087ff);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<ItemStack>();
    }
}
