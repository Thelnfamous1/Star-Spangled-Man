package com.infamous.captain_america.client.layer;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.renderer.model.EXO7FalconModel2;
import com.infamous.captain_america.common.item.EXO7FalconItem;
import com.infamous.captain_america.common.registry.ItemRegistry;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class EXO7FalconLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation FALCON_WINGS_TEXTURE = new ResourceLocation(CaptainAmerica.MODID, "textures/entity/falcon_wings.png");
    private static final ResourceLocation CAPTAIN_AMERICA_WINGS_TEXTURE = new ResourceLocation(CaptainAmerica.MODID, "textures/entity/captain_america_wings.png");

    private final EXO7FalconModel2<T> elytraModel;

    public EXO7FalconLayer(RenderLayerParent<T, M> parent, EntityModelSet entityModelSet) {
        super(parent);
        this.elytraModel = new EXO7FalconModel2<>(entityModelSet.bakeLayer(ModelLayers.ELYTRA));
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int p_225628_3_, T flyer, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
        ItemStack exo7FalconStack = FalconFlightHelper.getEXO7FalconStack(flyer);
        if (shouldRender(exo7FalconStack, flyer)) {
            ResourceLocation resourcelocation = getEXO7FalconTexture(exo7FalconStack, flyer);

            matrixStack.pushPose();
            this.getParentModel().copyPropertiesTo(this.elytraModel);

            boolean renderWings = EXO7FalconItem.isFlightEnabled(exo7FalconStack);
            this.elytraModel.leftWing.visible = renderWings;
            this.elytraModel.rightWing.visible = renderWings;

            this.elytraModel.setupAnim(flyer, p_225628_5_, p_225628_6_, p_225628_8_, p_225628_9_, p_225628_10_);
            VertexConsumer armorFoilBuffer = ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(resourcelocation), false, exo7FalconStack.hasFoil());
            this.elytraModel.renderToBuffer(matrixStack, armorFoilBuffer, p_225628_3_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.popPose();
        }
    }

    public boolean shouldRender(ItemStack stack, T entity) {
        return EXO7FalconItem.isEXO7FalconStack(stack);
    }

    public ResourceLocation getEXO7FalconTexture(ItemStack stack, T entity) {
        if(stack.getItem() == ItemRegistry.FALCON_WINGSUIT.get()){
            return FALCON_WINGS_TEXTURE;
        } else if(stack.getItem() == ItemRegistry.CAPTAIN_AMERICA_WINGSUIT.get()){
            return CAPTAIN_AMERICA_WINGS_TEXTURE;
        } else{
            return FALCON_WINGS_TEXTURE;
        }
    }
}
