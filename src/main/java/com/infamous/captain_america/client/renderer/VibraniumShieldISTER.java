package com.infamous.captain_america.client.renderer;

import com.infamous.captain_america.client.renderer.model.VibraniumShieldModel;
import com.infamous.captain_america.client.renderer.model.VibraniumShieldTextures;
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

public class VibraniumShieldISTER extends ItemStackTileEntityRenderer {
    private final VibraniumShieldModel shieldModel = new VibraniumShieldModel();

    public void renderByItem(ItemStack stackToRender, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_239207_5_, int p_239207_6_) {
        Item item = stackToRender.getItem();
        if (item instanceof VibraniumShieldItem) {
            matrixStack.pushPose();
            matrixStack.scale(1.0F, -1.0F, -1.0F);
            RenderMaterial shieldTexture = VibraniumShieldTextures.getShieldTexture(((VibraniumShieldItem)item));
            IVertexBuilder vertexBuilder = shieldTexture.sprite().wrap(ItemRenderer.getFoilBufferDirect(renderTypeBuffer, this.shieldModel.renderType(shieldTexture.atlasLocation()), true, stackToRender.hasFoil()));
            this.shieldModel.handle().render(matrixStack, vertexBuilder, p_239207_5_, p_239207_6_, 1.0F, 1.0F, 1.0F, 1.0F);
            this.shieldModel.plate().render(matrixStack, vertexBuilder, p_239207_5_, p_239207_6_, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.popPose();
        }
    }
}