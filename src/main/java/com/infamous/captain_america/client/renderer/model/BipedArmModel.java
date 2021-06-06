package com.infamous.captain_america.client.renderer.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

public class BipedArmModel<T extends LivingEntity> extends BipedModel<T> {
    public BipedArmModel(float p_i1148_1_) {
        this(RenderType::entityCutoutNoCull, p_i1148_1_, 0.0F, 64, 32);
    }

    protected BipedArmModel(float p_i1149_1_, float p_i1149_2_, int p_i1149_3_, int p_i1149_4_) {
        this(RenderType::entityCutoutNoCull, p_i1149_1_, p_i1149_2_, p_i1149_3_, p_i1149_4_);
    }

    public BipedArmModel(Function<ResourceLocation, RenderType> p_i225946_1_, float p_i225946_2_, float p_i225946_3_, int p_i225946_4_, int p_i225946_5_) {
        super(p_i225946_1_, p_i225946_2_, p_i225946_3_, p_i225946_4_, p_i225946_5_);
        this.head = new ModelRenderer(this, 0, 0);
        this.hat = new ModelRenderer(this, 0, 0);
        this.body = new ModelRenderer(this, 0, 0);
        this.leftLeg = new ModelRenderer(this, 0, 0);
        this.rightLeg = new ModelRenderer(this, 0, 0);
    }
}
