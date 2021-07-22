package com.infamous.captain_america.mixin;

import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.registry.EffectRegistry;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.LivingEntity;
import com.mojang.math.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {

    protected LivingRendererMixin(EntityRenderDispatcher p_i46179_1_) {
        super(p_i46179_1_);
    }

    @Inject(at = @At("RETURN"), method = "getRenderType", cancellable = true)
    private void checkInfrared(T livingToRender, boolean isBodyVisible, boolean isTranslucent, boolean isGlowing, CallbackInfoReturnable<RenderType> cir){
        if(cir.getReturnValue() == null && !isGlowing){
            LocalPlayer clientPlayer = Minecraft.getInstance().player;
            if(clientPlayer != null && clientPlayer.hasEffect(EffectRegistry.HUD_INFRARED.get())){
                cir.setReturnValue(RenderType.outline(this.getTextureLocation(livingToRender)));
            }
            else if(clientPlayer != null && clientPlayer.hasEffect(EffectRegistry.HUD_COMBAT_TRACKER.get())){
                IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(clientPlayer);
                if(falconAbilityCap != null){
                    int livingToRenderId = livingToRender.getId();
                    if(livingToRenderId == falconAbilityCap.getLastHurtById() || livingToRenderId == falconAbilityCap.getLastHurtId()){
                        cir.setReturnValue(RenderType.outline(this.getTextureLocation(livingToRender)));
                    }
                }
            }
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/matrix/MatrixStack;scale(FFF)V"), method = "render")
    private void checkFlipping(T living, float partialTicks, float partialRenderTicks, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light, CallbackInfo ci){
        if(FalconFlightHelper.isFlipFlying(living)){
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(((float)living.tickCount + partialRenderTicks) * -75.0F));
        }
    }

}
