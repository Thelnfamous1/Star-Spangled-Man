package com.infamous.captain_america.client.renderer.model;// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.15 - 1.16
// Paste this class into your mod and generate all required imports


import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class EXO7FalconModel2<T extends LivingEntity> extends AgeableListModel<T> {
	public final ModelPart leftWing;
	public final ModelPart rightWing;
	private final ModelPart wingPack;

	public EXO7FalconModel2() {
		texWidth = 64;
		texHeight = 64;

		ModelPart body = new ModelPart(this);
		body.setPos(0.0F, 0.0F, 0.0F);

		this.leftWing = new ModelPart(this);
		this.leftWing.setPos(4.25F, 0.75F, 3.75F);
		body.addChild(this.leftWing);
		this.setRotationAngle(this.leftWing, (float) -Math.PI, 0.0F, (float)Math.PI / 2);
		this.buildWings(this.leftWing);

		this.rightWing = new ModelPart(this);
		this.rightWing.setPos(-4.25F, 0.75F, 3.75F);
		body.addChild(this.rightWing);
		this.setRotationAngle(this.rightWing, (float) Math.PI /*0.0F*/, 0.0F,  (float)-Math.PI / 2 /*(float)Math.PI / 2*/);
		this.buildWings(this.rightWing);

		this.wingPack = new ModelPart(this);
		this.wingPack.setPos(0.0F, 0.0F, 0.0F);
		body.addChild(this.wingPack);
		//wingPack.texOffs(0, 48).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.51F, false);
		this.wingPack.texOffs(24, 47).addBox(-3.5F, 0.25F, 3.25F, 7.0F, 8.0F, 1.0F, 0.76F, false);
		this.wingPack.texOffs(32, 49).addBox(-3.5F, 1.5F, 3.25F, 7.0F, 0.0F, 1.0F, 0.75F, false);

		ModelPart bottom = new ModelPart(this);
		bottom.setPos(0.0F, 7.25F, 3.75F);
		this.wingPack.addChild(bottom);
		this.setRotationAngle(bottom, (float) Math.PI, 0.0F, 0.0F);
		bottom.texOffs(32, 49).addBox(-3.5F, 1.5F, -0.5F, 7.0F, 0.0F, 1.0F, 0.75F, false);
	}

	private void buildWings(ModelPart wing) {
		wing.texOffs(8, 2).addBox(-12.0F, -2.25F, 0.75F, 23.0F, 34.0F, 1.0F, -2.24F, false);
		wing.texOffs(8, 2).addBox(-12.0F, -2.25F, 1.75F, 23.0F, 34.0F, 1.0F, -2.24F, false);
		wing.texOffs(43, 2).addBox(-0.1F, 0.0F, -0.5F, 0.0F, 8.0F, 1.0F, 0.0F, false);
		wing.texOffs(43, 12).addBox(-0.9F, 7.8F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, false);
		wing.texOffs(44, 15).addBox(-1.7F, 10.4F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, false);
		wing.texOffs(45, 16).addBox(-2.5F, 11.28F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, false);
		wing.texOffs(46, 15).addBox(-2.5F, 11.28F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, false);
		wing.texOffs(46, 17).addBox(-3.32F, 12.15F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, false);
		wing.texOffs(47, 16).addBox(-3.32F, 12.15F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, false);
		wing.texOffs(47, 19).addBox(-4.13F, 13.9F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, false);
		wing.texOffs(48, 18).addBox(-4.13F, 13.9F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, false);
		wing.texOffs(48, 21).addBox(-4.92F, 15.65F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, false);
		wing.texOffs(49, 20).addBox(-4.92F, 15.65F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, false);
		wing.texOffs(49, 24).addBox(-5.75F, 18.25F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, false);
		wing.texOffs(50, 23).addBox(-5.75F, 18.25F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, false);
		wing.texOffs(50, 26).addBox(-6.55F, 20.0F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, false);
		wing.texOffs(51, 25).addBox(-6.55F, 20.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, false);
		wing.texOffs(51, 28).addBox(-7.35F, 21.7F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, false);
		wing.texOffs(52, 27).addBox(-7.35F, 21.7F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, false);
		wing.texOffs(52, 30).addBox(-8.18F, 23.45F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, false);
		wing.texOffs(53, 29).addBox(-8.15F, 23.45F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, false);
		wing.texOffs(53, 32).addBox(-8.98F, 25.2F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, false);
		wing.texOffs(54, 31).addBox(-8.95F, 25.2F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, false);
		wing.texOffs(53, 36).addBox(-10.0F, 29.5F, -0.5F, 2.0F, 0.0F, 1.0F, 0.0F, true);
		wing.texOffs(53, 35).addBox(-8.16F, 28.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, true);
		wing.texOffs(47, 35).addBox(-8.73F, 28.65F, -0.5F, 3.0F, 0.0F, 1.0F, 0.0F, true);
		wing.texOffs(50, 34).addBox(-5.73F, 27.65F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, true);
		wing.texOffs(48, 34).addBox(-5.94F, 27.78F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, true);
		wing.texOffs(49, 33).addBox(-4.94F, 26.78F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, true);
		wing.texOffs(43, 33).addBox(-5.52F, 26.9F, -0.5F, 3.0F, 0.0F, 1.0F, 0.0F, true);
		wing.texOffs(46, 32).addBox(-2.52F, 25.9F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, true);
		wing.texOffs(44, 32).addBox(-2.7F, 26.05F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, true);
		wing.texOffs(45, 31).addBox(-1.7F, 25.05F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, true);
		wing.texOffs(43, 31).addBox(-1.9F, 25.18F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, true);
		wing.texOffs(40, 30).addBox(-1.29F, 24.3F, -0.5F, 2.0F, 0.0F, 1.0F, 0.0F, true);
		wing.texOffs(42, 29).addBox(0.71F, 23.3F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, true);
		wing.texOffs(40, 29).addBox(0.51F, 23.44F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, true);
		wing.texOffs(41, 28).addBox(1.51F, 22.44F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, true);
		wing.texOffs(39, 28).addBox(1.33F, 22.57F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, true);
		wing.texOffs(40, 27).addBox(2.33F, 21.57F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, true);
		wing.texOffs(38, 27).addBox(2.15F, 21.7F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, true);
		wing.texOffs(39, 25).addBox(3.15F, 19.7F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, true);
		wing.texOffs(37, 25).addBox(2.93F, 19.95F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, true);
		wing.texOffs(38, 23).addBox(3.93F, 17.95F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, true);
		wing.texOffs(36, 23).addBox(3.76F, 18.23F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, true);
		wing.texOffs(37, 20).addBox(4.76F, 15.23F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, true);
		wing.texOffs(35, 20).addBox(4.57F, 15.6F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, true);
		wing.texOffs(36, 17).addBox(5.54F, 12.6F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, true);
		wing.texOffs(34, 17).addBox(5.35F, 13.0F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, true);
		wing.texOffs(35, 15).addBox(6.35F, 11.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, true);
		wing.texOffs(33, 15).addBox(6.17F, 11.25F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, true);
		wing.texOffs(34, 11).addBox(7.17F, 7.25F, -0.5F, 0.0F, 4.0F, 1.0F, 0.0F, true);
		wing.texOffs(32, 11).addBox(6.96F, 7.8F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, true);
		wing.texOffs(33, 5).addBox(7.96F, 1.8F, -0.5F, 0.0F, 6.0F, 1.0F, 0.0F, true);
		wing.texOffs(31, 5).addBox(7.76F, 2.58F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, true);
		wing.texOffs(31, 5).addBox(7.76F, 1.72F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, true);
		wing.texOffs(32, 4).addBox(6.96F, 0.86F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, true);
		wing.texOffs(34, 2).addBox(7.15F, 0.0F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, true);
		wing.texOffs(35, 3).addBox(-0.5F, 0.0F, -0.5F, 8.0F, 0.0F, 1.0F, 0.0F, true);
		wing.texOffs(33, 3).addBox(7.96F, 0.86F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, true);
		wing.texOffs(32, 4).addBox(8.76F, 1.65F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, true);
		wing.texOffs(44, 30).addBox(-0.9F, 24.18F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, true);
		wing.texOffs(54, 34).addBox(-9.75F, 26.95F, -0.5F, 1.0F, 0.0F, 1.0F, 0.0F, false);
		wing.texOffs(55, 33).addBox(-9.75F, 26.7F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, false);
		wing.texOffs(45, 14).addBox(-1.7F, 10.4F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, false);
		wing.texOffs(44, 11).addBox(-0.9F, 7.8F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, false);
	}

	protected Iterable<ModelPart> headParts() {
		return ImmutableList.of();
	}

	protected Iterable<ModelPart> bodyParts() {
		return ImmutableList.of(this.wingPack, this.leftWing, this.rightWing);
	}

	public void setupAnim(T living, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
		this.wingPack.y = 0.0F;
		this.wingPack.xRot = 0.0F;

		float leftWingXRot = (float) -Math.PI;
		float leftWingYRot = 0.0F;
		float leftWingZRot = (float)Math.PI / 2;
		float leftWingYPos = 0.75F; // default Y pos
		//this.leftWing.x = 4.25F;
		//this.rightWing.x = -4.25F;
		//this.rightWing.z = 5.0F;

		if (living.isFallFlying()) {
			float fallFactor = 1.0F;
			Vec3 deltaMove = living.getDeltaMovement();
			if (deltaMove.y < 0.0D) {
				Vec3 normalDeltaMove = deltaMove.normalize();
				fallFactor = (1.0F - (float)Math.pow(-normalDeltaMove.y, 1.5D)) * -1;
			}
			leftWingZRot = fallFactor * ((float)Math.PI / 2F) + (1.0F - fallFactor) * -leftWingZRot;

		} else if (living.isCrouching()) {
			this.wingPack.y += 3.0F;
			this.wingPack.xRot += 0.5F;

			//this.leftWing.x += 1.0F;
			//this.rightWing.x += 1.0F;
			//this.rightWing.z -= 1.0F;

			leftWingXRot *= (8.0F / 3.0F) / 8.0F;
			leftWingYRot = (float) -Math.PI * (8.0F / 3.0F) / 3.0F;
			leftWingZRot *= 3.0F;
			//leftWingYPos += 3.0F;
		}
		this.leftWing.y = leftWingYPos;
		if (living instanceof AbstractClientPlayer) {
			AbstractClientPlayer abstractclientplayerentity = (AbstractClientPlayer)living;
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
		this.rightWing.z = this.leftWing.z;

		this.rightWing.xRot = -this.leftWing.xRot;
		this.rightWing.yRot = -this.leftWing.yRot + (float)Math.PI; // multiply by -1 to properly mirror
		this.rightWing.zRot = -this.leftWing.zRot;
	}

	public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}