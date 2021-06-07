package com.infamous.captain_america.client.layer;

import com.google.common.collect.Maps;
import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.item.MetalArmItem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;

public class MetalArmLayer<T extends LivingEntity, M extends BipedModel<T>, A extends BipedModel<T>> extends LayerRenderer<T, M> {
   private static final Map<String, ResourceLocation> ARMS_LOCATION_CACHE = Maps.newHashMap();
   protected final A armsModel;

   public MetalArmLayer(IEntityRenderer<T, M> entityRenderer, A bipedModelIn) {
      super(entityRenderer);
      this.armsModel = bipedModelIn;
   }

   public void render(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_225628_3_, T living, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      this.renderArm(matrixStack, renderTypeBuffer, living, EquipmentSlotType.MAINHAND, p_225628_3_, this.getArmModel());
      this.renderArm(matrixStack, renderTypeBuffer, living, EquipmentSlotType.OFFHAND, p_225628_3_, this.getArmModel());
   }

   private void renderArm(MatrixStack matrixStack, IRenderTypeBuffer p_241739_2_, T living, EquipmentSlotType slotIn, int p_241739_5_, A armsModel) {
      ItemStack itemstack = living.getItemBySlot(slotIn);
      if (shouldRenderArm(living, itemstack)) {
         if (slotIn.getType() == EquipmentSlotType.Group.HAND) {
            armsModel = getArmsModelHook(living, itemstack, slotIn, armsModel);
            this.getParentModel().copyPropertiesTo(armsModel);
            this.setPartVisibility(armsModel, slotIn, living.getMainArm());
            boolean hasFoil = itemstack.hasFoil();
            this.renderModel(matrixStack, p_241739_2_, p_241739_5_, hasFoil, armsModel, 1.0F, 1.0F, 1.0F, this.getArmsLocation(living, itemstack, slotIn, null));
         }
      }
   }

   protected boolean shouldRenderArm(T living, ItemStack itemstack) {
      return MetalArmItem.isMetalArmStack(itemstack);
   }

   protected void setPartVisibility(A armsModel, EquipmentSlotType slotType, HandSide mainArm) {
      armsModel.setAllVisible(false);
      switch(slotType) {
         case MAINHAND:
            armsModel.rightArm.visible = mainArm == HandSide.RIGHT;
            armsModel.leftArm.visible =  mainArm == HandSide.LEFT;
            break;
         case OFFHAND:
            armsModel.rightArm.visible =  mainArm == HandSide.LEFT;
            armsModel.leftArm.visible =  mainArm == HandSide.RIGHT;
            break;
      }

   }
   private void renderModel(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_241738_3_, boolean p_241738_5_, A armsModel, float p_241738_8_, float p_241738_9_, float p_241738_10_, ResourceLocation armsResource) {
      IVertexBuilder ivertexbuilder = ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(armsResource), false, p_241738_5_);
      armsModel.renderToBuffer(matrixStack, ivertexbuilder, p_241738_3_, OverlayTexture.NO_OVERLAY, p_241738_8_, p_241738_9_, p_241738_10_, 1.0F);
   }

   private A getArmModel() {
      return this.armsModel;
   }

   protected A getArmsModelHook(T entity, ItemStack itemStack, EquipmentSlotType slot, A model) {
      return net.minecraftforge.client.ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
   }

   public ResourceLocation getArmsLocation(net.minecraft.entity.Entity entity, ItemStack stack, EquipmentSlotType slot, @Nullable String type) {
      MetalArmItem armItem = (MetalArmItem)stack.getItem();
      String texture = armItem.getTier().getName();
      String domain = CaptainAmerica.MODID;
      int idx = texture.indexOf(':');
      if (idx != -1) {
         domain = texture.substring(0, idx);
         texture = texture.substring(idx + 1);
      }
      String texturePath = String.format("%s:textures/models/arms/%s%s.png", domain, texture, type == null ? "" : String.format("_%s", type));

      texturePath = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, texturePath, slot, type);
      ResourceLocation resourcelocation = ARMS_LOCATION_CACHE.get(texturePath);

      if (resourcelocation == null) {
         resourcelocation = new ResourceLocation(texturePath);
         ARMS_LOCATION_CACHE.put(texturePath, resourcelocation);
      }

      return resourcelocation;
   }
}