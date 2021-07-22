package com.infamous.captain_america.common.potion;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import javax.annotation.Nullable;

public class CAEffect extends MobEffect {

    public CAEffect(MobEffectCategory effectType, int color) {
        super(effectType, color);
    }

    @Override
    public void applyEffectTick(LivingEntity p_76394_1_, int p_76394_2_) {
        // NO-OP
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity p_180793_1_, @Nullable Entity p_180793_2_, LivingEntity p_180793_3_, int p_180793_4_, double p_180793_5_) {
        // NO-OP
    }
}
