package com.infamous.captain_america.client.renderer.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import Entity;

public class TimedGrenadeModel<T extends Entity> extends ListModel<T> {
   private final ModelPart main = new ModelPart(this);

   public TimedGrenadeModel() {
      this(0.0F);
   }

   public TimedGrenadeModel(float p_i47225_1_) {
      int i = 2;
      this.main.texOffs(0, 0).addBox(-4.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, p_i47225_1_);
      this.main.texOffs(0, 0).addBox(0.0F, -4.0F, 0.0F, 2.0F, 2.0F, 2.0F, p_i47225_1_);
      this.main.texOffs(0, 0).addBox(0.0F, 0.0F, -4.0F, 2.0F, 2.0F, 2.0F, p_i47225_1_);
      this.main.texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, p_i47225_1_);
      this.main.texOffs(0, 0).addBox(2.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, p_i47225_1_);
      this.main.texOffs(0, 0).addBox(0.0F, 2.0F, 0.0F, 2.0F, 2.0F, 2.0F, p_i47225_1_);
      this.main.texOffs(0, 0).addBox(0.0F, 0.0F, 2.0F, 2.0F, 2.0F, 2.0F, p_i47225_1_);
      this.main.setPos(0.0F, 0.0F, 0.0F);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
   }

   public Iterable<ModelPart> parts() {
      return ImmutableList.of(this.main);
   }
}