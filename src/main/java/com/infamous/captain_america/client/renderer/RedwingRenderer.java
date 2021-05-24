package com.infamous.captain_america.client.renderer;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.entity.RedwingEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.PhantomEyesLayer;
import net.minecraft.client.renderer.entity.model.PhantomModel;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RedwingRenderer extends MobRenderer<RedwingEntity, PhantomModel<RedwingEntity>> {
   private static final ResourceLocation REDWING_LOCATION = new ResourceLocation(CaptainAmerica.MODID, "textures/entity/redwing.png");

   public RedwingRenderer(EntityRendererManager entityRendererManager) {
      super(entityRendererManager, new PhantomModel<>(), 0.75F);
   }

   public ResourceLocation getTextureLocation(RedwingEntity p_110775_1_) {
      return REDWING_LOCATION;
   }

   protected void scale(RedwingEntity redwing, MatrixStack matrixStack, float p_225620_3_) {
      int redwingSize = 0;
      float scaleFactor = 1.0F + 0.15F * (float)redwingSize;
      matrixStack.scale(scaleFactor, scaleFactor, scaleFactor);
      matrixStack.translate(0.0D, 1.3125D, 0.1875D);
   }

   protected void setupRotations(RedwingEntity redwing, MatrixStack matrixStack, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
      super.setupRotations(redwing, matrixStack, p_225621_3_, p_225621_4_, p_225621_5_);
      matrixStack.mulPose(Vector3f.XP.rotationDegrees(redwing.xRot));
   }
}