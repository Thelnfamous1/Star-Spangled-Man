package com.infamous.captain_america.client.util;

import com.infamous.captain_america.common.item.gauntlet.WeaponGauntletItem;
import com.infamous.captain_america.common.util.CALogicHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class LaserBeamHelper {

    public static void renderBeam(RenderWorldLastEvent event, Player player, float frameTime) {
        Vec3 originVec = player.getEyePosition(frameTime);
        HitResult trace = CALogicHelper.getLaserRayTrace(player);

        float speedModifier = -0.02f;

        drawBeams(event, originVec, trace, 0, 0, 0, 255 / 255f, 0 / 255f, 0 / 255f, 0.02f, player, frameTime, speedModifier);
    }

    private static void drawBeams(RenderWorldLastEvent event, Vec3 originVec, HitResult rayTrace, double xOffset, double yOffset, double zOffset, float r, float g, float b, float thickness, Player player, float ticks, float speedModifier) {
        InteractionHand laserShootingHand;
        if (WeaponGauntletItem.isStackOfThis(player.getMainHandItem())) {
            laserShootingHand = InteractionHand.MAIN_HAND;
        } else if (WeaponGauntletItem.isStackOfThis(player.getOffhandItem())) {
            laserShootingHand = InteractionHand.OFF_HAND;
        } else {
            return;
        }

        VertexConsumer builder;
        double distance = Math.max(1, originVec.subtract(rayTrace.getLocation()).length());
        long gameTime = player.level.getGameTime();
        double scaledGameTime = gameTime * speedModifier;
        float additiveThickness = (thickness * 3.5f) * calculateLaserFlickerModifier(gameTime);

        float beam2r = 255 / 255f;
        float beam2g = 255 / 255f;
        float beam2b = 255 / 255f;

        Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        PoseStack matrix = event.getMatrixStack();

        matrix.pushPose();

        matrix.translate(-view.x(), -view.y(), -view.z());
        matrix.translate(originVec.x, originVec.y, originVec.z);
        matrix.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(ticks, -player.getYRot(), -player.yRotO)));
        matrix.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(ticks, player.getXRot(), player.xRotO)));

        PoseStack.Pose matrixstack$entry = matrix.last();
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
        return 0.9f + 0.1f * Mth.sin(gameTime * 0.99f) * Mth.sin(gameTime * 0.3f) * Mth.sin(gameTime * 0.1f);
    }

    private static void drawBeam(double xOffset, double yOffset, double zOffset, VertexConsumer builder, Matrix4f positionMatrix, Matrix3f matrixNormalIn, float thickness, InteractionHand hand, double distance, double v1, double v2, float ticks, float r, float g, float b, float alpha) {
        Vector3f vector3f = new Vector3f(0.0f, 1.0f, 0.0f);
        vector3f.transform(matrixNormalIn);
        LocalPlayer clientPlayer = Minecraft.getInstance().player;
        // Support for hand sides remembering to take into account of Skin options
        if( Minecraft.getInstance().options.mainHand != HumanoidArm.RIGHT)
            hand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        float startXOffset = -0.25f;
        float startYOffset = -.115f;
        float startZOffset = 0;
        if (clientPlayer != null) {
            startZOffset = 0.65f + (1 - clientPlayer.getFieldOfViewModifier());
        }
        if (hand == InteractionHand.OFF_HAND) {
            startYOffset = -.120f;
            startXOffset = 0.25f;
        }
        float lerpXRot = 0;
        if (clientPlayer != null) {
            lerpXRot = (Mth.lerp(ticks, clientPlayer.xRotO, clientPlayer.getXRot()) - Mth.lerp(ticks, clientPlayer.xBobO, clientPlayer.xBob));
        }
        float lerpYRot = 0;
        if (clientPlayer != null) {
            lerpYRot = (Mth.lerp(ticks, clientPlayer.yRotO, clientPlayer.getYRot()) - Mth.lerp(ticks, clientPlayer.yBobO, clientPlayer.yBob));
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

        if (hand == InteractionHand.MAIN_HAND) {
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