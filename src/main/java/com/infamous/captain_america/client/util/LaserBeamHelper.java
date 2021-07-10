package com.infamous.captain_america.client.util;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.util.CALogicHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LaserBeamHelper {

    private static final ResourceLocation LASER_BEAM_LOCATION = new ResourceLocation("textures/entity/beacon_beam.png");
    private static final RenderType BEAM_RENDER_TYPE = RenderType.entityCutoutNoCull(LASER_BEAM_LOCATION);

    private static Minecraft minecraft;

    private static WorldRenderer worldRenderer;
    private static RenderTypeBuffers renderTypeBuffers;
    private static Method shouldShowEntityOutlinesMethod;

    public static void renderBeam(RenderWorldLastEvent event, PlayerEntity player) {

        if(minecraft == null){
            minecraft = Minecraft.getInstance();
            worldRenderer = minecraft.levelRenderer;
            renderTypeBuffers = ObfuscationReflectionHelper.getPrivateValue(WorldRenderer.class, worldRenderer, "field_228415_m_");
            shouldShowEntityOutlinesMethod = ObfuscationReflectionHelper.findMethod(WorldRenderer.class, "func_174985_d");
        }

        IRenderTypeBuffer.Impl irendertypebuffer$impl = renderTypeBuffers.bufferSource();
        IRenderTypeBuffer renderTypeBuffer;
        Boolean shouldShowEntityOutlines;
        try {
            shouldShowEntityOutlines = (Boolean) shouldShowEntityOutlinesMethod.invoke(worldRenderer);
        } catch (IllegalAccessException | InvocationTargetException e) {
            CaptainAmerica.LOGGER.error("Reflection error! Unable to determine if the world renderer should show entity outlines!");
            shouldShowEntityOutlines = true; // assume true
        }

        if (shouldShowEntityOutlines && minecraft.shouldEntityAppearGlowing(player)) {
            OutlineLayerBuffer outlinelayerbuffer = renderTypeBuffers.outlineBufferSource();
            renderTypeBuffer = outlinelayerbuffer;
            int teamColor = player.getTeamColor();
            int alpha = 255;
            int red = teamColor >> 16 & alpha;
            int green = teamColor >> 8 & alpha;
            int blue = teamColor & alpha;
            outlinelayerbuffer.setColor(red, green, blue, alpha);
        } else {
            renderTypeBuffer = irendertypebuffer$impl;
        }

        float partialTicks = event.getPartialTicks();
        MatrixStack matrixStack = event.getMatrixStack();

        Vector3d targetVec;
        RayTraceResult rayTraceResult = CALogicHelper.getLaserRayTrace(player);
        if (rayTraceResult instanceof EntityRayTraceResult) {
            Entity target = ((EntityRayTraceResult) rayTraceResult).getEntity();
            targetVec = getPosition(target, 0, (double)target.getBbHeight() * 0.5D, 0, partialTicks);
        } else{
            Vector3d targetLocation = rayTraceResult.getLocation();
            targetVec = new Vector3d(targetLocation.x, targetLocation.y, targetLocation.z);
        }
        renderLaserBeam(player, targetVec, partialTicks, matrixStack, renderTypeBuffer);

    }

    private static void renderLaserBeam(PlayerEntity player, Vector3d targetVec, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer) {
        float gameTime = (float) player.level.getGameTime() + partialTicks;
        float moduloHalfGameTime = gameTime * 0.5F % 1.0F;
        float playerEyeHeight = player.getEyeHeight();
        matrixStack.pushPose();
        matrixStack.translate(0.0D, (double)playerEyeHeight, 0.0D);
        Vector3d originVec = getPosition(player, 0, (double)playerEyeHeight, 0, partialTicks);
        Vector3d diffVec = targetVec.subtract(originVec);
        float diffVecLength = (float)(diffVec.length() + 1.0D);
        diffVec = diffVec.normalize();
        float acos = (float)Math.acos(diffVec.y);
        float atan2 = (float)Math.atan2(diffVec.z, diffVec.x);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees((((float)Math.PI / 2F) - atan2) * (180F / (float)Math.PI)));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(acos * (180F / (float)Math.PI)));
        int i = 1;
        float gameTimePartial = gameTime * 0.05F * -1.5F;
        int red = 255;
        int green = 0;
        int blue = 0;
        float multiplier2 = 0.2F;
        float multiplier1 = 0.282F;
        float x1 = MathHelper.cos(gameTimePartial + (float)Math.PI / 1.33333333F) * multiplier1;
        float z1 = MathHelper.sin(gameTimePartial + (float)Math.PI / 1.33333333F) * multiplier1;
        float x2 = MathHelper.cos(gameTimePartial + ((float)Math.PI / 4F)) * multiplier1;
        float z2 = MathHelper.sin(gameTimePartial + ((float)Math.PI / 4F)) * multiplier1;
        float x4 = MathHelper.cos(gameTimePartial + (float)Math.PI * 1.25F) * multiplier1;
        float z4 = MathHelper.sin(gameTimePartial + (float)Math.PI * 1.25F) * multiplier1;
        float x3 = MathHelper.cos(gameTimePartial + (float)Math.PI * 1.75F) * multiplier1;
        float z3 = MathHelper.sin(gameTimePartial + (float)Math.PI * 1.75F) * multiplier1;
        float x5 = MathHelper.cos(gameTimePartial + (float)Math.PI) * multiplier2;
        float z5 = MathHelper.sin(gameTimePartial + (float)Math.PI) * multiplier2;
        float x6 = MathHelper.cos(gameTimePartial + 0.0F) * multiplier2;
        float z6 = MathHelper.sin(gameTimePartial + 0.0F) * multiplier2;
        float x7 = MathHelper.cos(gameTimePartial + ((float)Math.PI / 2F)) * multiplier2;
        float z7 = MathHelper.sin(gameTimePartial + ((float)Math.PI / 2F)) * multiplier2;
        float x8 = MathHelper.cos(gameTimePartial + ((float)Math.PI * 1.5F)) * multiplier2;
        float z8 = MathHelper.sin(gameTimePartial + ((float)Math.PI * 1.5F)) * multiplier2;
        float u2 = 0.0F;
        float u1 = 0.4999F;
        float v2 = -1.0F + moduloHalfGameTime;
        float v1 = diffVecLength * 2.5F + v2;
        IVertexBuilder ivertexbuilder = renderTypeBuffer.getBuffer(getBeamRenderType());
        MatrixStack.Entry matrixstack$entry = matrixStack.last();
        Matrix4f matrix4f = matrixstack$entry.pose();
        Matrix3f matrix3f = matrixstack$entry.normal();
        vertex(ivertexbuilder, matrix4f, matrix3f, x5, diffVecLength, z5, red, green, blue, u1, v1);
        vertex(ivertexbuilder, matrix4f, matrix3f, x5, 0.0F, z5, red, green, blue, u1, v2);
        vertex(ivertexbuilder, matrix4f, matrix3f, x6, 0.0F, z6, red, green, blue, u2, v2);
        vertex(ivertexbuilder, matrix4f, matrix3f, x6, diffVecLength, z6, red, green, blue, u2, v1);
        vertex(ivertexbuilder, matrix4f, matrix3f, x7, diffVecLength, z7, red, green, blue, u1, v1);
        vertex(ivertexbuilder, matrix4f, matrix3f, x7, 0.0F, z7, red, green, blue, u1, v2);
        vertex(ivertexbuilder, matrix4f, matrix3f, x8, 0.0F, z8, red, green, blue, u2, v2);
        vertex(ivertexbuilder, matrix4f, matrix3f, x8, diffVecLength, z8, red, green, blue, u2, v1);
        float v3 = 0.0F;
        if (player.tickCount % 2 == 0) {
            v3 = 0.5F;
        }

        vertex(ivertexbuilder, matrix4f, matrix3f, x1, diffVecLength, z1, red, green, blue, 0.5F, v3 + 0.5F);
        vertex(ivertexbuilder, matrix4f, matrix3f, x2, diffVecLength, z2, red, green, blue, 1.0F, v3 + 0.5F);
        vertex(ivertexbuilder, matrix4f, matrix3f, x3, diffVecLength, z3, red, green, blue, 1.0F, v3);
        vertex(ivertexbuilder, matrix4f, matrix3f, x4, diffVecLength, z4, red, green, blue, 0.5F, v3);
        matrixStack.popPose();
    }

    private static Vector3d getPosition(Entity entity, double xOffset, double yOffset, double zOffset, float partialTicks) {
        double x = MathHelper.lerp((double)partialTicks, entity.xOld, entity.getX() + xOffset);
        double y = MathHelper.lerp((double)partialTicks, entity.yOld, entity.getY()) + yOffset;
        double z = MathHelper.lerp((double)partialTicks, entity.zOld, entity.getZ()) + zOffset;
        return new Vector3d(x, y, z);
    }

    private static void vertex(IVertexBuilder vertexBuilder, Matrix4f matrix4f, Matrix3f matrix3f, float x, float y, float z, int red, int green, int blue, float u, float v) {
        vertexBuilder
                .vertex(matrix4f, x, y, z)
                .color(red, green, blue, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private static RenderType getBeamRenderType() {
        return BEAM_RENDER_TYPE;
    }
}