package com.infamous.captain_america.client.renderer;

import com.infamous.captain_america.client.renderer.model.VibraniumShieldModel;
import com.infamous.captain_america.common.item.VibraniumShieldItem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.concurrent.Callable;

public class CAItemStackTileEntityRenderer extends ItemStackTileEntityRenderer {
    private final VibraniumShieldModel shieldModel = new VibraniumShieldModel();

    public static Callable<ItemStackTileEntityRenderer> getISTER() {
        return CAItemStackTileEntityRenderer::new;
    }

    public void renderByItem(ItemStack renderStack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_239207_5_, int p_239207_6_) {
        Item renderItem = renderStack.getItem();
        if (renderItem instanceof VibraniumShieldItem) {
            RenderMaterial shieldTexture = ((VibraniumShieldItem)renderItem).getRenderMaterial();
            if(shieldTexture == null) return;

            matrixStack.pushPose();
            matrixStack.scale(1.0F, -1.0F, -1.0F);
            IVertexBuilder vertexBuilder = shieldTexture.sprite().wrap(ItemRenderer.getFoilBufferDirect(renderTypeBuffer, this.shieldModel.renderType(shieldTexture.atlasLocation()), true, renderStack.hasFoil()));
            this.shieldModel.handle().render(matrixStack, vertexBuilder, p_239207_5_, p_239207_6_, 1.0F, 1.0F, 1.0F, 1.0F);
            this.shieldModel.plate().render(matrixStack, vertexBuilder, p_239207_5_, p_239207_6_, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.popPose();
        }
    }
}