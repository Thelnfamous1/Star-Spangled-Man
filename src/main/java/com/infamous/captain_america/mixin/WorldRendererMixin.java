package com.infamous.captain_america.mixin;

import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.registry.EffectRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.OutlineLayerBuffer;
import net.minecraft.client.renderer.RenderTypeBuffers;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Shadow @Final private Minecraft minecraft;

    @Final
    @Shadow
    private RenderTypeBuffers renderBuffers;

    @Shadow protected abstract boolean shouldShowEntityOutlines();

    @Shadow @Final private EntityRendererManager entityRenderDispatcher;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRendererManager;render(Lnet/minecraft/entity/Entity;DDDFFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V"), method = "renderEntity", cancellable = true)
    private void checkInfrared(Entity entityToRender, double renderInfoX, double renderInfoY, double renderInfoZ, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, CallbackInfo ci){
        ClientPlayerEntity localPlayer = this.minecraft.player;
        if(entityToRender == localPlayer || localPlayer == null){
            return;
        }
        boolean entityIsAlreadyGlowing = this.minecraft.shouldEntityAppearGlowing(entityToRender);
        boolean clientHasInfrared = localPlayer.hasEffect(EffectRegistry.HUD_INFRARED.get());
        boolean clientHasCombatTracker = localPlayer.hasEffect(EffectRegistry.HUD_COMBAT_TRACKER.get());
        IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(localPlayer);
        int entityToRenderId = entityToRender.getId();
        boolean shouldTrackCombat = clientHasCombatTracker
                && falconAbilityCap != null
                && (entityToRenderId == falconAbilityCap.getLastHurtById() || entityToRenderId == falconAbilityCap.getLastHurtId());
        if(this.shouldShowEntityOutlines() && (clientHasInfrared || shouldTrackCombat) && !entityIsAlreadyGlowing){

            OutlineLayerBuffer outlinelayerbuffer;
            if(clientHasInfrared){
                outlinelayerbuffer = this.getColoredOutlineBuffer(255, 0, 0, 255);
            } else { // shouldTrackCombat
                outlinelayerbuffer = this.getColoredOutlineBuffer(204, 85, 0, 255);
            }


            double lerpX = MathHelper.lerp((double)partialTicks, entityToRender.xOld, entityToRender.getX());
            double lerpY = MathHelper.lerp((double)partialTicks, entityToRender.yOld, entityToRender.getY());
            double lerpZ = MathHelper.lerp((double)partialTicks, entityToRender.zOld, entityToRender.getZ());
            float lerpYRot = MathHelper.lerp(partialTicks, entityToRender.yRotO, entityToRender.yRot);
            this.entityRenderDispatcher.render(entityToRender, lerpX - renderInfoX, lerpY - renderInfoY, lerpZ - renderInfoZ, lerpYRot, partialTicks, matrixStack, outlinelayerbuffer, this.entityRenderDispatcher.getPackedLightCoords(entityToRender, partialTicks));
            ci.cancel();
        }
    }

    private OutlineLayerBuffer getColoredOutlineBuffer(int red, int green, int blue, int alpha) {
        OutlineLayerBuffer outlinelayerbuffer = this.renderBuffers.outlineBufferSource();
        outlinelayerbuffer.setColor(red, green, blue, alpha);
        return outlinelayerbuffer;
    }
}
