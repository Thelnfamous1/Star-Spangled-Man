package com.infamous.captain_america.client.renderer.model;// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.15 - 1.16
// Paste this class into your mod and generate all required imports


import com.google.common.collect.ImmutableList;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

public class EXO7FalconModel1<T extends LivingEntity> extends AgeableModel<T> {
	public final ModelRenderer leftWing;
	public final ModelRenderer rightWing;
	private final ModelRenderer wingPack;

	public EXO7FalconModel1() {
		this.texWidth = 64;
		this.texHeight = 64;

		ModelRenderer body = new ModelRenderer(this);
		body.setPos(0.0F, 0.0F, 0.0F);
		body.texOffs(0, 48).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.51F, false);

		this.leftWing = new ModelRenderer(this);
		this.leftWing.setPos(4.25F, 0.5F, 4.0F);
		body.addChild(this.leftWing);
		this.setDefaultWingRotation(this.leftWing, false);
		this.leftWing.texOffs(8, 2).addBox(-11.75F, -2.25F, 1.25F, 23.0F, 34.0F, 1.0F, -2.24F, false);

		this.rightWing = new ModelRenderer(this);
		this.rightWing.setPos(-4.25F, 0.75F, 5.0F);
		body.addChild(this.rightWing);
		this.setDefaultWingRotation(this.rightWing, true);
		this.rightWing.texOffs(8, 2).addBox(-11.0F, -2.25F, 2.25F, 23.0F, 34.0F, 1.0F, -2.24F, true);

		this.wingPack = new ModelRenderer(this);
		this.wingPack.setPos(0.0F, 0.0F, 0.0F);
		body.addChild(this.wingPack);
		this.wingPack.texOffs(24, 47).addBox(-3.5F, 0.25F, 3.25F, 7.0F, 8.0F, 1.0F, 0.76F, false);
		this.wingPack.texOffs(32, 49).addBox(-3.5F, 1.5F, 3.25F, 7.0F, 0.0F, 1.0F, 0.75F, false);
	}

	private void setDefaultWingRotation(ModelRenderer wing, boolean right) {
		this.setRotationAngle(wing, (float) -Math.PI, 0.0F, ((float)Math.PI / 2) * (right ? -1 : 1));
	}

	protected Iterable<ModelRenderer> headParts() {
		return ImmutableList.of();
	}

	protected Iterable<ModelRenderer> bodyParts() {
		return ImmutableList.of(this.wingPack, this.leftWing, this.rightWing);
	}

	public void setupAnim(T living, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
		this.wingPack.y = 0.0F;
		this.wingPack.xRot = 0.0F;

		float leftWingXRot = (float) -Math.PI;
		float leftWingYRot = 0.0F;
		float leftWingZRot = (float)Math.PI / 2;
		float leftWingYPos = 0.5F; // default Y pos

		if (living.isFallFlying()) {
			float fallFactor = 1.0F;
			Vector3d deltaMove = living.getDeltaMovement();
			if (deltaMove.y < 0.0D) {
				Vector3d normalDeltaMove = deltaMove.normalize();
				fallFactor = (1.0F - (float)Math.pow(-normalDeltaMove.y, 1.5D)) * -1;
			}
			leftWingZRot = fallFactor * ((float)Math.PI / 2F) + (1.0F - fallFactor) * -leftWingZRot;

		} else if (living.isCrouching()) {
			this.wingPack.y += 3.0F;
			this.wingPack.xRot += 0.5F;
			leftWingXRot *= (8.0F / 3.0F);
			leftWingYRot = leftWingXRot / 3;
			leftWingZRot *= 3.0F;
			leftWingYPos += 3.0F;
		}
		this.leftWing.y = leftWingYPos;
		if (living instanceof AbstractClientPlayerEntity) {
			AbstractClientPlayerEntity abstractclientplayerentity = (AbstractClientPlayerEntity)living;
			abstractclientplayerentity.elytraRotX = (float)((double)abstractclientplayerentity.elytraRotX + (double)(leftWingXRot - abstractclientplayerentity.elytraRotX) * 0.2D);
			abstractclientplayerentity.elytraRotY = (float)((double)abstractclientplayerentity.elytraRotY + (double)(leftWingYRot - abstractclientplayerentity.elytraRotY) * 0.2D);
			abstractclientplayerentity.elytraRotZ = (float)((double)abstractclientplayerentity.elytraRotZ + (double)(leftWingZRot - abstractclientplayerentity.elytraRotZ) * 0.2D);
			this.leftWing.xRot = abstractclientplayerentity.elytraRotX;
			this.leftWing.yRot = abstractclientplayerentity.elytraRotY;
			this.leftWing.zRot = abstractclientplayerentity.elytraRotZ;
		} else {
			this.leftWing.xRot = leftWingXRot;
			this.leftWing.yRot = leftWingYRot;
			this.leftWing.zRot = leftWingZRot;
		}
		this.rightWing.x = -this.leftWing.x;
		this.rightWing.y = this.leftWing.y;
		this.rightWing.xRot = this.leftWing.xRot;
		this.rightWing.yRot = this.leftWing.yRot; // multiply by -1 to properly mirror
		this.rightWing.zRot = -this.leftWing.zRot;
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}