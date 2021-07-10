package com.infamous.captain_america.client.layer;

import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.util.CALogicHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class LaserLayer<T extends LivingEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> {
    private static final ResourceLocation LASER_BEAM_LOCATION = new ResourceLocation("textures/entity/beacon_beam.png");
    private static final RenderType BEAM_RENDER_TYPE = RenderType.entityCutoutNoCull(LASER_BEAM_LOCATION);

    public LaserLayer(IEntityRenderer<T, M> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_225628_3_, T shooter, float p_225628_5_, float p_225628_6_, float partialTicks, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
        RayTraceResult rayTraceResult = this.getRayTrace(shooter);
        if (this.shouldRender(shooter)) {
            float attackAnimationScale = this.getAttackAnimationScale(shooter, partialTicks);
            float gameTime = (float)shooter.level.getGameTime() + partialTicks;
            float moduloHalfGameTime = gameTime * 0.5F % 1.0F;
            float sourceXOffset = this.getSourceXOffset(shooter); // default == 0
            float sourceYOffset = this.getSourceYOffset(shooter); // default == shooter.getEyeHeight()
            float sourceZOffset = this.getSourceZOffset(shooter); // default == 0
            matrixStack.pushPose();
            matrixStack.translate(sourceXOffset, sourceYOffset, sourceZOffset);
            Vector3d targetPos = this.getTargetPos(rayTraceResult, partialTicks);
            Vector3d sourcePos = this.getSourcePos(shooter, sourceXOffset, sourceYOffset, sourceZOffset, partialTicks);
            Vector3d targetSourcePosDiff = targetPos.subtract(sourcePos);
            float posDiffLength = (float)(targetSourcePosDiff.length() + 1.0D);
            targetSourcePosDiff = targetSourcePosDiff.normalize();
            float acosY = (float)Math.acos(targetSourcePosDiff.y);
            float atan2ZX = (float)Math.atan2(targetSourcePosDiff.z, targetSourcePosDiff.x);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees((((float)Math.PI / 2F) - atan2ZX) * (180F / (float)Math.PI)));
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(acosY * (180F / (float)Math.PI)));

            int i = 1;
            float gameTimePartial = gameTime * 0.05F * -1.5F;
            float attackAnimScaleSq = attackAnimationScale * attackAnimationScale;
            int red = this.getRed(attackAnimScaleSq);
            int green = this.getGreen(attackAnimScaleSq);
            int blue = this.getBlue(attackAnimScaleSq);
            float xzMultiplier2 = 0.2F;
            float xzMultiplier1 = 0.282F;

            float x1 = MathHelper.cos(gameTimePartial + (float)Math.PI / 1.33333333F) * xzMultiplier1;
            float z1 = MathHelper.sin(gameTimePartial + (float)Math.PI / 1.33333333F) * xzMultiplier1;

            float x2 = MathHelper.cos(gameTimePartial + ((float)Math.PI / 4F)) * xzMultiplier1;
            float z2 = MathHelper.sin(gameTimePartial + ((float)Math.PI / 4F)) * xzMultiplier1;

            float x4 = MathHelper.cos(gameTimePartial + (float)Math.PI * 1.25F) * xzMultiplier1;
            float z4 = MathHelper.sin(gameTimePartial + (float)Math.PI * 1.25F) * xzMultiplier1;

            float x3 = MathHelper.cos(gameTimePartial + (float)Math.PI * 1.75F) * xzMultiplier1;
            float z3 = MathHelper.sin(gameTimePartial + (float)Math.PI * 1.75F) * xzMultiplier1;

            float x5 = MathHelper.cos(gameTimePartial + (float)Math.PI) * xzMultiplier2;
            float z5 = MathHelper.sin(gameTimePartial + (float)Math.PI) * xzMultiplier2;

            float x6 = MathHelper.cos(gameTimePartial + 0.0F) * xzMultiplier2;
            float z6 = MathHelper.sin(gameTimePartial + 0.0F) * xzMultiplier2;

            float x7 = MathHelper.cos(gameTimePartial + ((float)Math.PI / 2F)) * xzMultiplier2;
            float z7 = MathHelper.sin(gameTimePartial + ((float)Math.PI / 2F)) * xzMultiplier2;

            float x8 = MathHelper.cos(gameTimePartial + ((float)Math.PI * 1.5F)) * xzMultiplier2;
            float z8 = MathHelper.sin(gameTimePartial + ((float)Math.PI * 1.5F)) * xzMultiplier2;

            float u1 = 0.0F;
            float u2 = 0.4999F;

            float v1 = -1.0F + moduloHalfGameTime;
            float v2 = posDiffLength * 2.5F + v1;

            IVertexBuilder beamVertexBuilder = renderTypeBuffer.getBuffer(this.getBeamRenderType());
            MatrixStack.Entry matrixstack$entry = matrixStack.last();
            Matrix4f msePose = matrixstack$entry.pose();
            Matrix3f mseNormal = matrixstack$entry.normal();
            this.drawVertex(beamVertexBuilder, msePose, mseNormal, x5, posDiffLength, z5, red, green, blue, u2, v2);
            this.drawVertex(beamVertexBuilder, msePose, mseNormal, x5, 0.0F, z5, red, green, blue, u2, v1);
            this.drawVertex(beamVertexBuilder, msePose, mseNormal, x6, 0.0F, z6, red, green, blue, u1, v1);
            this.drawVertex(beamVertexBuilder, msePose, mseNormal, x6, posDiffLength, z6, red, green, blue, u1, v2);
            this.drawVertex(beamVertexBuilder, msePose, mseNormal, x7, posDiffLength, z7, red, green, blue, u2, v2);
            this.drawVertex(beamVertexBuilder, msePose, mseNormal, x7, 0.0F, z7, red, green, blue, u2, v1);
            this.drawVertex(beamVertexBuilder, msePose, mseNormal, x8, 0.0F, z8, red, green, blue, u1, v1);
            this.drawVertex(beamVertexBuilder, msePose, mseNormal, x8, posDiffLength, z8, red, green, blue, u1, v2);

            float v3 = 0.0F;
            if (shooter.tickCount % 2 == 0) {
                v3 = 0.5F;
            }

            this.drawVertex(beamVertexBuilder, msePose, mseNormal, x1, posDiffLength, z1, red, green, blue, 0.5F, v3 + 0.5F);
            this.drawVertex(beamVertexBuilder, msePose, mseNormal, x2, posDiffLength, z2, red, green, blue, 1.0F, v3 + 0.5F);
            this.drawVertex(beamVertexBuilder, msePose, mseNormal, x3, posDiffLength, z3, red, green, blue, 1.0F, v3);
            this.drawVertex(beamVertexBuilder, msePose, mseNormal, x4, posDiffLength, z4, red, green, blue, 0.5F, v3);
            matrixStack.popPose();
        }
    }

    protected float getSourceXOffset(T shooter) {
        return 0.0F;
    }

    protected float getSourceYOffset(T shooter) {
        return shooter.getEyeHeight();
    }

    protected float getSourceZOffset(T shooter) {
        return 0.0F;
    }

    protected RenderType getBeamRenderType() {
        return BEAM_RENDER_TYPE;
    }

    protected float getAttackAnimationScale(T shooter, float partialTicks) {
        return 1.0F; //shooter.getAttackAnimationScale(partialTicks);
    }

    protected int getRed(float attackAnimScaleSq) {
        return 255;  //64 + (int)(attackAnimScaleSq * 191.0F);
    }

    protected int getGreen(float attackAnimScaleSq) {
        return 0; //32 + (int)(attackAnimScaleSq * 191.0F);
    }

    protected int getBlue(float attackAnimScaleSq) {
        return 0; //128 - (int)(attackAnimScaleSq * 64.0F);
    }

    protected Vector3d getSourcePos(T shooter, double xOffset, double yOffset, double zOffset, float partialTicks) {
        return this.getPosition(shooter, xOffset, yOffset, zOffset, partialTicks);
    }

    protected Vector3d getPosition(Entity entity, double xOffset, double yOffset, double zOffset, float partialTicks) {
        double lerpX = MathHelper.lerp((double)partialTicks, entity.xOld, entity.getX()) + xOffset;
        double lerpY = MathHelper.lerp((double)partialTicks, entity.yOld, entity.getY()) + yOffset;
        double lerpZ = MathHelper.lerp((double)partialTicks, entity.zOld, entity.getZ()) + zOffset;
        return new Vector3d(lerpX, lerpY, lerpZ);
    }

    protected Vector3d getTargetPos(RayTraceResult rtr, float partialTicks) {
        if(rtr instanceof EntityRayTraceResult){
            Entity target = ((EntityRayTraceResult) rtr).getEntity();
            return this.getPosition(target, 0.0D, (double)target.getBbHeight() * 0.5D, 0.0D, partialTicks);
        } else{
            return rtr.getLocation();
        }
    }

    protected boolean shouldRender(T shooter) {
        IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(shooter);
        if(falconAbilityCap == null){
            return false;
        }
        return falconAbilityCap.isShootingLaser();
    }

    protected RayTraceResult getRayTrace(T shooter) {
        return CALogicHelper.getLaserRayTrace(shooter);
    }

    protected void drawVertex(IVertexBuilder beamVertexBuilder, Matrix4f pose, Matrix3f normal, float vertexX, float vertexY, float vertexZ, int red, int green, int blue, float u, float v) {
        beamVertexBuilder
                .vertex(pose, vertexX, vertexY, vertexZ)
                .color(red, green, blue, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(normal, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }
}
