package com.infamous.captain_america.client.layer;

import com.google.common.collect.Maps;
import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.metal_arm.IMetalArm;
import com.infamous.captain_america.common.item.MetalArmItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Map;

public class MetalArmLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
   private static final Map<String, ResourceLocation> ARMS_LOCATION_CACHE = Maps.newHashMap();
   protected final A armsModel;

   public MetalArmLayer(RenderLayerParent<T, M> entityRenderer, A bipedModelIn) {
      super(entityRenderer);
      this.armsModel = bipedModelIn;
   }

   @Override
   public void render(PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int p_225628_3_, T living, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      this.renderArm(matrixStack, renderTypeBuffer, living, true, p_225628_3_, this.getArmModel());
      this.renderArm(matrixStack, renderTypeBuffer, living, false, p_225628_3_, this.getArmModel());
   }

   private void renderArm(PoseStack matrixStack, MultiBufferSource p_241739_2_, T living, boolean mainHand, int p_241739_5_, A armsModel) {
      IMetalArm metalArmCap = CapabilityHelper.getMetalArmCap(living);
      if(metalArmCap == null) return;

      ItemStack itemstack;
      if(mainHand){
         itemstack = metalArmCap.getMetalArmMainHand();
      } else{
         itemstack = metalArmCap.getMetalArmOffHand();
      }

      if (shouldRenderArm(itemstack)) {
         EquipmentSlot slot = mainHand ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
         armsModel = getArmsModelHook(living, itemstack, slot, armsModel);
         this.getParentModel().copyPropertiesTo(armsModel);
         this.setPartVisibility(armsModel, slot, living.getMainArm());
         boolean hasFoil = itemstack.hasFoil();
         this.renderModel(matrixStack, p_241739_2_, p_241739_5_, hasFoil, armsModel, 1.0F, 1.0F, 1.0F, getArmsLocation(living, itemstack, slot, null));
      }
   }

   protected boolean shouldRenderArm(ItemStack itemstack) {
      return MetalArmItem.isMetalArmStack(itemstack);
   }

   protected void setPartVisibility(A armsModel, EquipmentSlot slotType, HumanoidArm mainArm) {
      armsModel.setAllVisible(false);
      switch(slotType) {
         case MAINHAND:
            armsModel.rightArm.visible = mainArm == HumanoidArm.RIGHT
                    && this.getParentModel().rightArm.visible;
            armsModel.leftArm.visible =  mainArm == HumanoidArm.LEFT
                    && this.getParentModel().leftArm.visible;
            break;
         case OFFHAND:
            armsModel.rightArm.visible =  mainArm == HumanoidArm.LEFT
                    && this.getParentModel().rightArm.visible;
            armsModel.leftArm.visible =  mainArm == HumanoidArm.RIGHT
                    && this.getParentModel().leftArm.visible;
            break;
      }

   }
   private void renderModel(PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int p_241738_3_, boolean p_241738_5_, A armsModel, float p_241738_8_, float p_241738_9_, float p_241738_10_, ResourceLocation armsResource) {
      VertexConsumer ivertexbuilder = ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(armsResource), false, p_241738_5_);
      armsModel.renderToBuffer(matrixStack, ivertexbuilder, p_241738_3_, OverlayTexture.NO_OVERLAY, p_241738_8_, p_241738_9_, p_241738_10_, 1.0F);
   }

   private A getArmModel() {
      return this.armsModel;
   }

   protected A getArmsModelHook(T entity, ItemStack itemStack, EquipmentSlot slot, A model) {
      return net.minecraftforge.client.ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
   }

   public static ResourceLocation getArmsLocation(net.minecraft.world.entity.Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
      String playerModelName = "";
      if(entity instanceof AbstractClientPlayer) {
         AbstractClientPlayer clientPlayer = (AbstractClientPlayer) entity;
         playerModelName = String.format("%s_", clientPlayer.getModelName());
      }
      MetalArmItem armItem = (MetalArmItem)stack.getItem();
      String texture = armItem.getTier().getName();
      String domain = CaptainAmerica.MODID;
      int idx = texture.indexOf(':');
      if (idx != -1) {
         domain = texture.substring(0, idx);
         texture = texture.substring(idx + 1);
      }
      String texturePath = String.format("%s:textures/models/arms/%s%s%s.png", domain, playerModelName, texture, type == null ? "" : String.format("_%s", type));

      texturePath = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, texturePath, slot, type);
      ResourceLocation resourcelocation = ARMS_LOCATION_CACHE.get(texturePath);

      if (resourcelocation == null) {
         resourcelocation = new ResourceLocation(texturePath);
         ARMS_LOCATION_CACHE.put(texturePath, resourcelocation);
      }

      return resourcelocation;
   }
}