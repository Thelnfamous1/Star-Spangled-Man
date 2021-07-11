package com.infamous.captain_america.client.util;

import com.infamous.captain_america.common.util.CALogicHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class LaserBeamHelper {

    public static void renderBeam(RenderWorldLastEvent event, PlayerEntity player, float frameTime) {
        Vector3d originVec = player.getEyePosition(frameTime);
        RayTraceResult trace = CALogicHelper.getLaserRayTrace(player);

        float speedModifier = -0.02f;

        drawBeams(event, originVec, trace, 0, 0, 0, 255 / 255f, 0 / 255f, 0 / 255f, 0.02f, player, frameTime, speedModifier);
    }

    private static void drawBeams(RenderWorldLastEvent event, Vector3d originVec, RayTraceResult rayTrace, double xOffset, double yOffset, double zOffset, float r, float g, float b, float thickness, PlayerEntity player, float ticks, float speedModifier) {
        Hand laserShootingHand = Hand.MAIN_HAND;

        IVertexBuilder builder;
        double distance = Math.max(1, originVec.subtract(rayTrace.getLocation()).length());
        long gameTime = player.level.getGameTime();
        double scaledGameTime = gameTime * speedModifier;
        float additiveThickness = (thickness * 3.5f) * calculateLaserFlickerModifier(gameTime);

        float beam2r = 255 / 255f;
        float beam2g = 255 / 255f;
        float beam2b = 255 / 255f;

        Vector3d view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        MatrixStack matrix = event.getMatrixStack();

        matrix.pushPose();

        matrix.translate(-view.x(), -view.y(), -view.z());
        matrix.translate(originVec.x, originVec.y, originVec.z);
        matrix.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(ticks, -player.yRot, -player.yRotO)));
        matrix.mulPose(Vector3f.XP.rotationDegrees(MathHelper.lerp(ticks, player.xRot, player.xRotO)));

        MatrixStack.Entry matrixstack$entry = matrix.last();
        Matrix3f matrixNormal = matrixstack$entry.normal();
        Matrix4f positionMatrix = matrixstack$entry.pose();

        //additive laser beam
        builder = buffer.getBuffer(CARenderType.BEACON_BEAM_GLOW);
        drawBeam(xOffset, yOffset, zOffset, builder, positionMatrix, matrixNormal, additiveThickness, laserShootingHand, distance, 0.5, 1, ticks, r,g,b,0.7f);

        //main laser, colored part
        builder = buffer.getBuffer(CARenderType.BEACON_BEAM_MAIN);
        drawBeam(xOffset, yOffset, zOffset, builder, positionMatrix, matrixNormal, thickness, laserShootingHand, distance, scaledGameTime, scaledGameTime + distance * 1.5, ticks, r,g,b,1f);

        //core
        builder = buffer.getBuffer(CARenderType.BEACON_BEAM_CORE);
        drawBeam(xOffset, yOffset, zOffset, builder, positionMatrix, matrixNormal, thickness/2, laserShootingHand, distance, scaledGameTime, scaledGameTime + distance * 1.5, ticks, beam2r,beam2g,beam2b,1f);
        matrix.popPose();
        //RenderSystem.disableDepthTest();
        buffer.endBatch();
    }

    private static float calculateLaserFlickerModifier(long gameTime) {
        return 0.9f + 0.1f * MathHelper.sin(gameTime * 0.99f) * MathHelper.sin(gameTime * 0.3f) * MathHelper.sin(gameTime * 0.1f);
    }

    private static void drawBeam(double xOffset, double yOffset, double zOffset, IVertexBuilder builder, Matrix4f positionMatrix, Matrix3f matrixNormalIn, float thickness, Hand hand, double distance, double v1, double v2, float ticks, float r, float g, float b, float alpha) {
        Vector3f vector3f = new Vector3f(0.0f, 1.0f, 0.0f);
        vector3f.transform(matrixNormalIn);
        ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
        // Support for hand sides remembering to take into account of Skin options
        if( Minecraft.getInstance().options.mainHand != HandSide.RIGHT)
            hand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        float startXOffset = -0.25f;
        float startYOffset = -.115f;
        float startZOffset = 0;
        if (clientPlayer != null) {
            startZOffset = 0.65f + (1 - clientPlayer.getFieldOfViewModifier());
        }
        if (hand == Hand.OFF_HAND) {
            startYOffset = -.120f;
            startXOffset = 0.25f;
        }
        float lerpXRot = 0;
        if (clientPlayer != null) {
            lerpXRot = (MathHelper.lerp(ticks, clientPlayer.xRotO, clientPlayer.xRot) - MathHelper.lerp(ticks, clientPlayer.xBobO, clientPlayer.xBob));
        }
        float lerpYRot = 0;
        if (clientPlayer != null) {
            lerpYRot = (MathHelper.lerp(ticks, clientPlayer.yRotO, clientPlayer.yRot) - MathHelper.lerp(ticks, clientPlayer.yBobO, clientPlayer.yBob));
        }
        startXOffset = startXOffset + (lerpYRot / 750);
        startYOffset = startYOffset + (lerpXRot / 750);

        Vector4f vec1 = new Vector4f(startXOffset, -thickness + startYOffset, startZOffset, 1.0F);
        vec1.transform(positionMatrix);
        Vector4f vec2 = new Vector4f((float) xOffset, -thickness + (float) yOffset, (float) distance + (float) zOffset, 1.0F);
        vec2.transform(positionMatrix);
        Vector4f vec3 = new Vector4f((float) xOffset, thickness + (float) yOffset, (float) distance + (float) zOffset, 1.0F);
        vec3.transform(positionMatrix);
        Vector4f vec4 = new Vector4f(startXOffset, thickness + startYOffset, startZOffset, 1.0F);
        vec4.transform(positionMatrix);

        if (hand == Hand.MAIN_HAND) {
            builder.vertex(vec4.x(), vec4.y(), vec4.z(), r, g, b, alpha, 0, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec3.x(), vec3.y(), vec3.z(), r, g, b, alpha, 0, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec2.x(), vec2.y(), vec2.z(), r, g, b, alpha, 1, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec1.x(), vec1.y(), vec1.z(), r, g, b, alpha, 1, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            //Rendering a 2nd time to allow you to see both sides in multiplayer, shouldn't be necessary with culling disabled but here we are....
            builder.vertex(vec1.x(), vec1.y(), vec1.z(), r, g, b, alpha, 1, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec2.x(), vec2.y(), vec2.z(), r, g, b, alpha, 1, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec3.x(), vec3.y(), vec3.z(), r, g, b, alpha, 0, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec4.x(), vec4.y(), vec4.z(), r, g, b, alpha, 0, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
        } else {
            builder.vertex(vec1.x(), vec1.y(), vec1.z(), r, g, b, alpha, 1, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec2.x(), vec2.y(), vec2.z(), r, g, b, alpha, 1, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec3.x(), vec3.y(), vec3.z(), r, g, b, alpha, 0, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec4.x(), vec4.y(), vec4.z(), r, g, b, alpha, 0, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            //Rendering a 2nd time to allow you to see both sides in multiplayer, shouldn't be necessary with culling disabled but here we are....
            builder.vertex(vec4.x(), vec4.y(), vec4.z(), r, g, b, alpha, 0, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec3.x(), vec3.y(), vec3.z(), r, g, b, alpha, 0, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec2.x(), vec2.y(), vec2.z(), r, g, b, alpha, 1, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec1.x(), vec1.y(), vec1.z(), r, g, b, alpha, 1, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
        }
    }
}