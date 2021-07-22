package com.infamous.captain_america.client.renderer;

import com.infamous.captain_america.client.renderer.model.VibraniumShieldModel1;
import com.infamous.captain_america.common.item.VibraniumShieldItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.Callable;

public class CAItemStackTileEntityRenderer extends BlockEntityWithoutLevelRenderer {
    private final VibraniumShieldModel1 shieldModel = new VibraniumShieldModel1();

    public CAItemStackTileEntityRenderer(BlockEntityRenderDispatcher berd, EntityModelSet ems) {
        super(berd, ems);
    }

    @Override
    public void renderByItem(ItemStack renderStack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource bufferSource, int p_108834_, int p_108835_) {
        Item renderItem = renderStack.getItem();
        if (renderItem instanceof VibraniumShieldItem vibraniumShieldItem) {
            Material shieldTexture = vibraniumShieldItem.getRenderMaterial();
            if(shieldTexture == null) return;

            poseStack.pushPose();
            poseStack.scale(1.0F, -1.0F, -1.0F);
            VertexConsumer vertexBuilder = shieldTexture.sprite().wrap(ItemRenderer.getFoilBufferDirect(bufferSource, this.shieldModel.renderType(shieldTexture.atlasLocation()), true, renderStack.hasFoil()));
            this.shieldModel.shield().render(poseStack, vertexBuilder, p_108834_, p_108835_, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        } else{
            super.renderByItem(renderStack, transformType, poseStack, bufferSource, p_108834_, p_108835_);
        }
    }
}