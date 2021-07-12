package com.infamous.captain_america.client.renderer;

import com.infamous.captain_america.client.renderer.model.TimedGrenadeModel;
import com.infamous.captain_america.common.entity.projectile.TimedGrenadeEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class TimedGrenadeRenderer extends EntityRenderer<TimedGrenadeEntity> {
   private static final ResourceLocation TIMED_GRENADE_LOCATION = new ResourceLocation("textures/entity/llama/spit.png");
   private final TimedGrenadeModel<TimedGrenadeEntity> model = new TimedGrenadeModel<>();

   public TimedGrenadeRenderer(EntityRendererManager p_i47202_1_) {
      super(p_i47202_1_);
   }

   public void render(TimedGrenadeEntity timedGrenade, float p_225623_2_, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_225623_6_) {
      matrixStack.pushPose();
      matrixStack.translate(0.0D, (double)0.15F, 0.0D);
      matrixStack.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, timedGrenade.yRotO, timedGrenade.yRot) - 90.0F));
      matrixStack.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, timedGrenade.xRotO, timedGrenade.xRot)));
      this.model.setupAnim(timedGrenade, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
      IVertexBuilder ivertexbuilder = renderTypeBuffer.getBuffer(this.model.renderType(TIMED_GRENADE_LOCATION));
      this.model.renderToBuffer(matrixStack, ivertexbuilder, p_225623_6_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.popPose();
      super.render(timedGrenade, p_225623_2_, partialTicks, matrixStack, renderTypeBuffer, p_225623_6_);
   }

   public ResourceLocation getTextureLocation(TimedGrenadeEntity timedGrenade) {
      return TIMED_GRENADE_LOCATION;
   }
}