package com.infamous.captain_america.mixin;

import com.infamous.captain_america.client.layer.MetalArmLayer;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.metal_arm.IMetalArm;
import com.infamous.captain_america.common.item.MetalArmItem;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

    public PlayerRendererMixin(EntityRendererManager p_i50965_1_, PlayerModel<AbstractClientPlayerEntity> p_i50965_2_, float p_i50965_3_) {
        super(p_i50965_1_, p_i50965_2_, p_i50965_3_);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;getSkinTextureLocation()Lnet/minecraft/util/ResourceLocation;"), method = "renderHand")
    private ResourceLocation getTextureLocation(AbstractClientPlayerEntity clientPlayer1, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int partialTicks, AbstractClientPlayerEntity clientPlayer, ModelRenderer arm, ModelRenderer sleeve){
        IMetalArm metalArmCap = CapabilityHelper.getMetalArmCap(clientPlayer1);

        if(metalArmCap != null){
            if(arm == (this.getModel()).rightArm){
                return getResourceLocation(clientPlayer1, HandSide.RIGHT, metalArmCap);
            } else if(arm == (this.getModel()).leftArm){
                return getResourceLocation(clientPlayer1, HandSide.LEFT, metalArmCap);
            }
        }

        return clientPlayer.getSkinTextureLocation();
    }

    private ResourceLocation getResourceLocation(AbstractClientPlayerEntity clientPlayer, HandSide handSide, IMetalArm metalArmCap) {
        HandSide mainArm = clientPlayer.getMainArm();

        EquipmentSlotType slotType;
        if (mainArm == handSide) {
            slotType = EquipmentSlotType.MAINHAND;
        } else {
            slotType = EquipmentSlotType.OFFHAND;
        }

        ItemStack stackForRender = slotType == EquipmentSlotType.MAINHAND ? metalArmCap.getMetalArmMainHand() : metalArmCap.getMetalArmOffHand();

        if (MetalArmItem.isMetalArmStack(stackForRender)) {
            return MetalArmLayer.getArmsLocation(clientPlayer, stackForRender, slotType, null);
        } else{
            return clientPlayer.getSkinTextureLocation();
        }
    }
}
