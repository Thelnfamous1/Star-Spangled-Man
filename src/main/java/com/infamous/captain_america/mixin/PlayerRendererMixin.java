package com.infamous.captain_america.mixin;

import com.infamous.captain_america.client.layer.MetalArmLayer;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.metal_arm.IMetalArm;
import com.infamous.captain_america.common.item.MetalArmItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PlayerRendererMixin(EntityRenderDispatcher p_i50965_1_, PlayerModel<AbstractClientPlayer> p_i50965_2_, float p_i50965_3_) {
        super(p_i50965_1_, p_i50965_2_, p_i50965_3_);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;getSkinTextureLocation()Lnet/minecraft/util/ResourceLocation;"), method = "renderHand")
    private ResourceLocation getTextureLocation(AbstractClientPlayer clientPlayer1, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int partialTicks, AbstractClientPlayer clientPlayer, ModelPart arm, ModelPart sleeve){
        IMetalArm metalArmCap = CapabilityHelper.getMetalArmCap(clientPlayer1);

        if(metalArmCap != null){
            if(arm == (this.getModel()).rightArm){
                return getResourceLocation(clientPlayer1, HumanoidArm.RIGHT, metalArmCap);
            } else if(arm == (this.getModel()).leftArm){
                return getResourceLocation(clientPlayer1, HumanoidArm.LEFT, metalArmCap);
            }
        }

        return clientPlayer.getSkinTextureLocation();
    }

    private ResourceLocation getResourceLocation(AbstractClientPlayer clientPlayer, HumanoidArm handSide, IMetalArm metalArmCap) {
        HumanoidArm mainArm = clientPlayer.getMainArm();

        EquipmentSlot slotType;
        if (mainArm == handSide) {
            slotType = EquipmentSlot.MAINHAND;
        } else {
            slotType = EquipmentSlot.OFFHAND;
        }

        ItemStack stackForRender = slotType == EquipmentSlot.MAINHAND ? metalArmCap.getMetalArmMainHand() : metalArmCap.getMetalArmOffHand();

        if (MetalArmItem.isMetalArmStack(stackForRender)) {
            return MetalArmLayer.getArmsLocation(clientPlayer, stackForRender, slotType, null);
        } else{
            return clientPlayer.getSkinTextureLocation();
        }
    }
}
