package com.infamous.captain_america.client.renderer;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.entity.projectile.VibraniumShieldEntity2;
import com.infamous.captain_america.common.registry.EntityTypeRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.ShieldModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VibraniumShieldRenderer extends EntityRenderer<VibraniumShieldEntity2> {
   public static final ResourceLocation VIBRANIUM_SHIELD_TEXTURE = new ResourceLocation(CaptainAmerica.MODID, "textures/entity/shield/vibranium_shield.png");
   public static final ResourceLocation CAPTAIN_AMERICA_SHIELD_TEXTURE = new ResourceLocation(CaptainAmerica.MODID, "textures/entity/shield/captain_america_shield.png");
   private final ShieldModel shieldModel = new ShieldModel();

   public VibraniumShieldRenderer(EntityRendererManager entityRendererManager) {
      super(entityRendererManager);
   }

   public void render(VibraniumShieldEntity2 vibraniumShield, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_225623_6_) {
      matrixStack.pushPose();
      matrixStack.scale(1.0F, -1.0F, -1.0F);
      matrixStack.mulPose(Vector3f.XP.rotationDegrees(270));
      matrixStack.mulPose(Vector3f.ZP.rotationDegrees(270));
      //matrixStack.mulPose(Vector3f.YP.rotationDegrees(vibraniumShield.yRot));
      //matrixStack.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(p_225623_3_, vibraniumShield.yRotO, vibraniumShield.yRot) - 90.0F));
      //matrixStack.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(p_225623_3_, vibraniumShield.xRotO, vibraniumShield.xRot) + 90.0F));
      IVertexBuilder vertexBuilder = net.minecraft.client.renderer.ItemRenderer.getFoilBufferDirect(renderTypeBuffer, this.shieldModel.renderType(this.getTextureLocation(vibraniumShield)), false, vibraniumShield.isFoil());
      this.shieldModel.renderToBuffer(matrixStack, vertexBuilder, p_225623_6_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.popPose();
      super.render(vibraniumShield, p_225623_2_, p_225623_3_, matrixStack, renderTypeBuffer, p_225623_6_);
   }

   public ResourceLocation getTextureLocation(VibraniumShieldEntity2 vibraniumShield) {
      if(vibraniumShield.getType() == EntityTypeRegistry.CAPTAIN_AMERICA_SHIELD.get()){
         return CAPTAIN_AMERICA_SHIELD_TEXTURE;
      }
      return VIBRANIUM_SHIELD_TEXTURE;
   }
}