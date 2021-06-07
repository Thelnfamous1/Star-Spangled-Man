package com.infamous.captain_america.client.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;

public class PlayerMetalArmLayer extends MetalArmLayer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>, PlayerModel<AbstractClientPlayerEntity>>{

    public PlayerMetalArmLayer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> entityRenderer, PlayerModel<AbstractClientPlayerEntity> playerModelIn) {
        super(entityRenderer, playerModelIn);
    }

    public void renderRightHand(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_229144_3_, AbstractClientPlayerEntity clientPlayer) {
        this.renderHand(matrixStack, renderTypeBuffer, p_229144_3_, clientPlayer, (this.armsModel).rightArm, (this.armsModel).rightSleeve);
    }

    public void renderLeftHand(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_229146_3_, AbstractClientPlayerEntity clientPlayer) {
        this.renderHand(matrixStack, renderTypeBuffer, p_229146_3_, clientPlayer, (this.armsModel).leftArm, (this.armsModel).leftSleeve);
    }

    private void renderHand(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_229145_3_, AbstractClientPlayerEntity clientPlayer, ModelRenderer arm, ModelRenderer sleeve) {
        PlayerModel<AbstractClientPlayerEntity> playerArmsModel = this.armsModel;
        this.setModelProperties(clientPlayer);
        playerArmsModel.attackTime = 0.0F;
        playerArmsModel.crouching = false;
        playerArmsModel.swimAmount = 0.0F;
        playerArmsModel.setupAnim(clientPlayer, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        arm.xRot = 0.0F;
        arm.render(matrixStack, renderTypeBuffer.getBuffer(RenderType.entitySolid(clientPlayer.getSkinTextureLocation())), p_229145_3_, OverlayTexture.NO_OVERLAY);
        sleeve.xRot = 0.0F;
        sleeve.render(matrixStack, renderTypeBuffer.getBuffer(RenderType.entityTranslucent(clientPlayer.getSkinTextureLocation())), p_229145_3_, OverlayTexture.NO_OVERLAY);
    }

    private void setModelProperties(AbstractClientPlayerEntity clientPlayer) {
        PlayerModel<AbstractClientPlayerEntity> playerArmsModel = this.armsModel;
        if (clientPlayer.isSpectator()) {
            playerArmsModel.setAllVisible(false);
            playerArmsModel.head.visible = true;
            playerArmsModel.hat.visible = true;
        } else {
            playerArmsModel.setAllVisible(true);
            playerArmsModel.hat.visible = clientPlayer.isModelPartShown(PlayerModelPart.HAT);
            playerArmsModel.jacket.visible = clientPlayer.isModelPartShown(PlayerModelPart.JACKET);
            playerArmsModel.leftPants.visible = clientPlayer.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
            playerArmsModel.rightPants.visible = clientPlayer.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
            playerArmsModel.leftSleeve.visible = clientPlayer.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
            playerArmsModel.rightSleeve.visible = clientPlayer.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
            playerArmsModel.crouching = clientPlayer.isCrouching();
            BipedModel.ArmPose bipedmodel$armpose = getArmPose(clientPlayer, Hand.MAIN_HAND);
            BipedModel.ArmPose bipedmodel$armpose1 = getArmPose(clientPlayer, Hand.OFF_HAND);
            if (bipedmodel$armpose.isTwoHanded()) {
                bipedmodel$armpose1 = clientPlayer.getOffhandItem().isEmpty() ? BipedModel.ArmPose.EMPTY : BipedModel.ArmPose.ITEM;
            }

            if (clientPlayer.getMainArm() == HandSide.RIGHT) {
                playerArmsModel.rightArmPose = bipedmodel$armpose;
                playerArmsModel.leftArmPose = bipedmodel$armpose1;
            } else {
                playerArmsModel.rightArmPose = bipedmodel$armpose1;
                playerArmsModel.leftArmPose = bipedmodel$armpose;
            }
        }

    }

    private static BipedModel.ArmPose getArmPose(AbstractClientPlayerEntity clientPlayer, Hand hand) {
        ItemStack itemstack = clientPlayer.getItemInHand(hand);
        if (itemstack.isEmpty()) {
            return BipedModel.ArmPose.EMPTY;
        } else {
            if (clientPlayer.getUsedItemHand() == hand && clientPlayer.getUseItemRemainingTicks() > 0) {
                UseAction useaction = itemstack.getUseAnimation();
                if (useaction == UseAction.BLOCK) {
                    return BipedModel.ArmPose.BLOCK;
                }

                if (useaction == UseAction.BOW) {
                    return BipedModel.ArmPose.BOW_AND_ARROW;
                }

                if (useaction == UseAction.SPEAR) {
                    return BipedModel.ArmPose.THROW_SPEAR;
                }

                if (useaction == UseAction.CROSSBOW && hand == clientPlayer.getUsedItemHand()) {
                    return BipedModel.ArmPose.CROSSBOW_CHARGE;
                }
            } else if (!clientPlayer.swinging && itemstack.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemstack)) {
                return BipedModel.ArmPose.CROSSBOW_HOLD;
            }

            return BipedModel.ArmPose.ITEM;
        }
    }
}
