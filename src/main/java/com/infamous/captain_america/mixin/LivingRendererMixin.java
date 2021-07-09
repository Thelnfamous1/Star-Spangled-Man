package com.infamous.captain_america.mixin;

import com.infamous.captain_america.common.registry.EffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingRenderer.class)
public abstract class LivingRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements IEntityRenderer<T, M> {

    protected LivingRendererMixin(EntityRendererManager p_i46179_1_) {
        super(p_i46179_1_);
    }

    @Inject(at = @At("RETURN"), method = "getRenderType", cancellable = true)
    private void checkInfrared(T living, boolean isBodyVisible, boolean isTranslucent, boolean isGlowing, CallbackInfoReturnable<RenderType> cir){
        if(cir.getReturnValue() == null && !isGlowing){
            ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
            if(clientPlayer != null && clientPlayer.hasEffect(EffectRegistry.HUD_INFRARED.get())){
                cir.setReturnValue(RenderType.outline(this.getTextureLocation(living)));
            }
        }
    }
}
