package com.infamous.captain_america.mixin;

import com.infamous.captain_america.common.registry.EffectRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
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
        if(entityToRender == this.minecraft.player){
            return;
        }
        boolean entityIsAlreadyGlowing = this.minecraft.shouldEntityAppearGlowing(entityToRender);
        boolean clientHasInfrared = this.minecraft.player != null && this.minecraft.player.hasEffect(EffectRegistry.HUD_INFRARED.get());
        if(this.shouldShowEntityOutlines() && clientHasInfrared && !entityIsAlreadyGlowing){
            OutlineLayerBuffer outlinelayerbuffer = this.getBufferForInfrared();
            double lerpX = MathHelper.lerp((double)partialTicks, entityToRender.xOld, entityToRender.getX());
            double lerpY = MathHelper.lerp((double)partialTicks, entityToRender.yOld, entityToRender.getY());
            double lerpZ = MathHelper.lerp((double)partialTicks, entityToRender.zOld, entityToRender.getZ());
            float lerpYRot = MathHelper.lerp(partialTicks, entityToRender.yRotO, entityToRender.yRot);
            this.entityRenderDispatcher.render(entityToRender, lerpX - renderInfoX, lerpY - renderInfoY, lerpZ - renderInfoZ, lerpYRot, partialTicks, matrixStack, outlinelayerbuffer, this.entityRenderDispatcher.getPackedLightCoords(entityToRender, partialTicks));
            ci.cancel();
        }
    }

    private OutlineLayerBuffer getBufferForInfrared() {
        OutlineLayerBuffer outlinelayerbuffer = this.renderBuffers.outlineBufferSource();
        int teamColor = 16777215; // default result getTeam returns for an entity without a scoreboard team
        int red = 255; //teamColor >> 16 & 255
        int green = 0; //teamColor >> 8 & 255
        int blue = 0; //teamColor & 255
        int alpha = 255;
        outlinelayerbuffer.setColor(red, green, blue, alpha);
        return outlinelayerbuffer;
    }
}
