package com.infamous.captain_america.client.renderer;

import com.infamous.captain_america.common.entity.projectile.MissileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.gui.spectator.categories.TeleportToPlayerMenuCategory;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;

import ResourceLocation;

public class MissileRenderer extends EntityRenderer<MissileEntity> {
   public static final ResourceLocation MISSILE_LOCATION = new ResourceLocation("textures/entity/trident.png");
   private final TridentModel model = new TridentModel();

   public MissileRenderer(EntityRenderDispatcher p_i48828_1_) {
      super(p_i48828_1_);
   }

   @Override
   public void render(MissileEntity missile, float p_225623_2_, float partialTicks, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int p_225623_6_) {
      matrixStack.pushPose();
      matrixStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, missile.yRotO, missile.yRot) - 90.0F));
      matrixStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTicks, missile.xRotO, missile.xRot) + 90.0F));
      VertexConsumer foilBufferDirect = ItemRenderer.getFoilBufferDirect(renderTypeBuffer, this.model.renderType(this.getTextureLocation(missile)), false, false);
      this.model.renderToBuffer(matrixStack, foilBufferDirect, p_225623_6_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.popPose();
      super.render(missile, p_225623_2_, partialTicks, matrixStack, renderTypeBuffer, p_225623_6_);
   }

   @Override
   public ResourceLocation getTextureLocation(MissileEntity missile) {
      return MISSILE_LOCATION;
   }
}