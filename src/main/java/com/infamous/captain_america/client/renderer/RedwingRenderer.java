package com.infamous.captain_america.client.renderer;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.renderer.model.RedwingModel;
import com.infamous.captain_america.common.entity.drone.RedwingEntity;
import com.infamous.captain_america.common.registry.EntityTypeRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import ResourceLocation;

@OnlyIn(Dist.CLIENT)
public class RedwingRenderer extends MobRenderer<RedwingEntity, RedwingModel<RedwingEntity>> {
   private static final ResourceLocation FALCON_REDWING_LOCATION = new ResourceLocation(CaptainAmerica.MODID, "textures/entity/falcon_redwing.png");
   private static final ResourceLocation CAPTAIN_AMERICA_REDWING_LOCATION = new ResourceLocation(CaptainAmerica.MODID, "textures/entity/falcon_redwing.png");

   public RedwingRenderer(EntityRenderDispatcher entityRendererManager) {
      super(entityRendererManager, new RedwingModel<>(), 0.75F);
   }

   public ResourceLocation getTextureLocation(RedwingEntity redwingEntity) {
      if(redwingEntity.getType() == EntityTypeRegistry.CAPTAIN_AMERICA_REDWING.get()){
         return CAPTAIN_AMERICA_REDWING_LOCATION;
      }
      return FALCON_REDWING_LOCATION;
   }

   protected void scale(RedwingEntity redwing, PoseStack matrixStack, float p_225620_3_) {
      int redwingSize = 0;
      float scaleFactor = 1.0F + 0.15F * (float)redwingSize;
      matrixStack.scale(scaleFactor, scaleFactor, scaleFactor);
      //matrixStack.translate(0.0D, 1.3125D, 0.1875D);
   }

   protected void setupRotations(RedwingEntity redwing, PoseStack matrixStack, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
      super.setupRotations(redwing, matrixStack, p_225621_3_, p_225621_4_, p_225621_5_);
      matrixStack.mulPose(Vector3f.XP.rotationDegrees(redwing.xRot));
   }
}