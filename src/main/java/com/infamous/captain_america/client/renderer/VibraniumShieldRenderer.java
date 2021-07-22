package com.infamous.captain_america.client.renderer;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.renderer.model.VibraniumShieldModel1;
import com.infamous.captain_america.common.entity.projectile.VibraniumShieldEntity;
import com.infamous.captain_america.common.registry.EntityTypeRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.gui.spectator.categories.TeleportToPlayerMenuCategory;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import ResourceLocation;

@OnlyIn(Dist.CLIENT)
public class VibraniumShieldRenderer extends EntityRenderer<VibraniumShieldEntity> {
   public static final ResourceLocation VIBRANIUM_SHIELD_TEXTURE = new ResourceLocation(CaptainAmerica.MODID, "textures/entity/shield/vibranium_shield.png");
   public static final ResourceLocation CAPTAIN_AMERICA_SHIELD_TEXTURE = new ResourceLocation(CaptainAmerica.MODID, "textures/entity/shield/captain_america_shield.png");
   public static final ResourceLocation US_AGENT_SHIELD_TEXTURE = new ResourceLocation(CaptainAmerica.MODID, "textures/entity/shield/us_agent_shield.png");
   private final VibraniumShieldModel1 shieldModel = new VibraniumShieldModel1();

   public VibraniumShieldRenderer(EntityRenderDispatcher entityRendererManager) {
      super(entityRendererManager);
   }

   public void render(VibraniumShieldEntity vibraniumShield, float p_225623_2_, float partialTicks, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int p_225623_6_) {
      matrixStack.pushPose();
      matrixStack.scale(1.0F, -1.0F, -1.0F);


      matrixStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, vibraniumShield.yRotO, vibraniumShield.yRot) - 90.0F));
      matrixStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTicks, vibraniumShield.xRotO, vibraniumShield.xRot)));
      matrixStack.mulPose(Vector3f.XP.rotationDegrees(270));
      //matrixStack.mulPose(Vector3f.ZP.rotationDegrees(270));
      //matrixStack.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, vibraniumShield.yRotO, vibraniumShield.yRot) - 90.0F));
      //matrixStack.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, vibraniumShield.xRotO, vibraniumShield.xRot) + 90.0F));
      VertexConsumer vertexBuilder = net.minecraft.client.renderer.entity.ItemRenderer.getFoilBufferDirect(renderTypeBuffer, this.shieldModel.renderType(this.getTextureLocation(vibraniumShield)), false, vibraniumShield.isFoil());
      this.shieldModel.renderToBuffer(matrixStack, vertexBuilder, p_225623_6_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.popPose();
      super.render(vibraniumShield, p_225623_2_, partialTicks, matrixStack, renderTypeBuffer, p_225623_6_);
   }

   public ResourceLocation getTextureLocation(VibraniumShieldEntity vibraniumShield) {
      if(vibraniumShield.getType() == EntityTypeRegistry.CAPTAIN_AMERICA_SHIELD.get()){
         return CAPTAIN_AMERICA_SHIELD_TEXTURE;
      } else if(vibraniumShield.getType() == EntityTypeRegistry.US_AGENT_SHIELD.get()){
         return US_AGENT_SHIELD_TEXTURE;
      } else{
         return VIBRANIUM_SHIELD_TEXTURE;
      }
   }
}