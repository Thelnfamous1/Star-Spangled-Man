package com.infamous.captain_america.client.renderer;

import com.infamous.captain_america.common.entity.projectile.MissileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.TridentModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class MissileRenderer extends EntityRenderer<MissileEntity> {
   public static final ResourceLocation MISSILE_LOCATION = new ResourceLocation("textures/entity/trident.png");
   private final TridentModel model = new TridentModel();

   public MissileRenderer(EntityRendererManager p_i48828_1_) {
      super(p_i48828_1_);
   }

   @Override
   public void render(MissileEntity missile, float p_225623_2_, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_225623_6_) {
      matrixStack.pushPose();
      matrixStack.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, missile.yRotO, missile.yRot) - 90.0F));
      matrixStack.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, missile.xRotO, missile.xRot) + 90.0F));
      IVertexBuilder foilBufferDirect = ItemRenderer.getFoilBufferDirect(renderTypeBuffer, this.model.renderType(this.getTextureLocation(missile)), false, false);
      this.model.renderToBuffer(matrixStack, foilBufferDirect, p_225623_6_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.popPose();
      super.render(missile, p_225623_2_, partialTicks, matrixStack, renderTypeBuffer, p_225623_6_);
   }

   @Override
   public ResourceLocation getTextureLocation(MissileEntity missile) {
      return MISSILE_LOCATION;
   }
}