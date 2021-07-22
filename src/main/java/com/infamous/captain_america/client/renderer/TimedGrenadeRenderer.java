package com.infamous.captain_america.client.renderer;

import com.infamous.captain_america.client.renderer.model.TimedGrenadeModel;
import com.infamous.captain_america.common.entity.projectile.TimedGrenadeEntity;
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

import ResourceLocation;

public class TimedGrenadeRenderer extends EntityRenderer<TimedGrenadeEntity> {
   private static final ResourceLocation TIMED_GRENADE_LOCATION = new ResourceLocation("textures/entity/llama/spit.png");
   private final TimedGrenadeModel<TimedGrenadeEntity> model = new TimedGrenadeModel<>();

   public TimedGrenadeRenderer(EntityRenderDispatcher p_i47202_1_) {
      super(p_i47202_1_);
   }

   public void render(TimedGrenadeEntity timedGrenade, float p_225623_2_, float partialTicks, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int p_225623_6_) {
      matrixStack.pushPose();
      matrixStack.translate(0.0D, (double)0.15F, 0.0D);
      matrixStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, timedGrenade.yRotO, timedGrenade.yRot) - 90.0F));
      matrixStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTicks, timedGrenade.xRotO, timedGrenade.xRot)));
      this.model.setupAnim(timedGrenade, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
      VertexConsumer ivertexbuilder = renderTypeBuffer.getBuffer(this.model.renderType(TIMED_GRENADE_LOCATION));
      this.model.renderToBuffer(matrixStack, ivertexbuilder, p_225623_6_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.popPose();
      super.render(timedGrenade, p_225623_2_, partialTicks, matrixStack, renderTypeBuffer, p_225623_6_);
   }

   public ResourceLocation getTextureLocation(TimedGrenadeEntity timedGrenade) {
      return TIMED_GRENADE_LOCATION;
   }
}