package com.infamous.captain_america.mixin;

import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.registry.EffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class WorldRendererMixin {

    @Shadow @Final private Minecraft minecraft;

    @Final
    @Shadow
    private RenderBuffers renderBuffers;

    @Shadow protected abstract boolean shouldShowEntityOutlines();

    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRendererManager;render(Lnet/minecraft/entity/Entity;DDDFFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V"), method = "renderEntity", cancellable = true)
    private void checkInfrared(Entity entityToRender, double renderInfoX, double renderInfoY, double renderInfoZ, float partialTicks, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, CallbackInfo ci){
        LocalPlayer localPlayer = this.minecraft.player;
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

            OutlineBufferSource outlinelayerbuffer;
            if(clientHasInfrared){
                outlinelayerbuffer = this.getColoredOutlineBuffer(255, 0, 0, 255);
            } else { // shouldTrackCombat
                outlinelayerbuffer = this.getColoredOutlineBuffer(204, 85, 0, 255);
            }


            double lerpX = Mth.lerp((double)partialTicks, entityToRender.xOld, entityToRender.getX());
            double lerpY = Mth.lerp((double)partialTicks, entityToRender.yOld, entityToRender.getY());
            double lerpZ = Mth.lerp((double)partialTicks, entityToRender.zOld, entityToRender.getZ());
            float lerpYRot = Mth.lerp(partialTicks, entityToRender.yRotO, entityToRender.yRot);
            this.entityRenderDispatcher.render(entityToRender, lerpX - renderInfoX, lerpY - renderInfoY, lerpZ - renderInfoZ, lerpYRot, partialTicks, matrixStack, outlinelayerbuffer, this.entityRenderDispatcher.getPackedLightCoords(entityToRender, partialTicks));
            ci.cancel();
        }
    }

    private OutlineBufferSource getColoredOutlineBuffer(int red, int green, int blue, int alpha) {
        OutlineBufferSource outlinelayerbuffer = this.renderBuffers.outlineBufferSource();
        outlinelayerbuffer.setColor(red, green, blue, alpha);
        return outlinelayerbuffer;
    }
}
