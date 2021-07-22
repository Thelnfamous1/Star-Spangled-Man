package com.infamous.captain_america.client.renderer.model;// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.15 - 1.16
// Paste this class into your mod and generate all required imports


import com.google.common.collect.ImmutableList;
import com.infamous.captain_america.common.entity.drone.RedwingEntity;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;

@SuppressWarnings("FieldCanBeLocal")
public class RedwingModel<T extends RedwingEntity> extends ListModel<T> {
	private final ModelPart body;
	private final ModelPart tail;
	private final ModelPart rightTailWing;
	private final ModelPart leftTailWing;
	private final ModelPart camera;
	private final ModelPart leftWing;
	private final ModelPart rightWing;
	private final ModelPart head;

	public RedwingModel() {
		texWidth = 64;
		texHeight = 64;

		// BODY
		body = new ModelPart(this);
		body.setPos(0.0F, 19.35F, -0.7F);

		// TAIL
		tail = new ModelPart(this);
		tail.setPos(0.0F, 0.15F, -0.55F);
		body.addChild(tail);
		tail.texOffs(4, 18).addBox(-2.0F, -1.0F, 0.0F, 4.0F, 2.0F, 7.0F, 0.0F, false);

		rightTailWing = new ModelPart(this);
		rightTailWing.setPos(-3.5F, -1.0F, 6.0F);
		tail.addChild(rightTailWing);

		ModelPart rightTailWingCube = new ModelPart(this);
		rightTailWingCube.setPos(1.142F, -0.358F, 0.7302F);
		rightTailWing.addChild(rightTailWingCube);
		setRotationAngle(rightTailWingCube, 3.1416F, -2.3562F, 0.7854F);
		rightTailWingCube.texOffs(41, 7).addBox(-2.0F, -0.5F, -1.0F, 4.0F, 1.0F, 2.0F, 0.0F, false);

		leftTailWing = new ModelPart(this);
		leftTailWing.setPos(1.25F, -0.25F, 5.5F);
		tail.addChild(leftTailWing);

		ModelPart leftTailWingCube = new ModelPart(this);
		leftTailWingCube.setPos(0.75F, -0.75F, -0.5F);
		leftTailWing.addChild(leftTailWingCube);
		setRotationAngle(leftTailWingCube, 0.0F, -0.6981F, -0.7854F);
		leftTailWingCube.texOffs(41, 18).addBox(-0.5F, -0.5F, 0.0F, 4.0F, 1.0F, 2.0F, 0.0F, false);

		// HEAD

		head = new ModelPart(this);
		head.setPos(0.0F, 4.65F, 0.7F);
		body.addChild(head);

		ModelPart headCube = new ModelPart(this);
		headCube.setPos(0.0F, -4.5F, -3.5F);
		head.addChild(headCube);
		setRotationAngle(headCube, 0.0F, 0.7854F, 0.0F);
		headCube.texOffs(3, 3).addBox(-3.0F, -1.5F, -3.0F, 6.0F, 3.0F, 6.0F, 0.0F, false);

		// WINGS

		leftWing = new ModelPart(this);
		leftWing.setPos(2.6161F, 0.15F, -1.3F);
		body.addChild(leftWing);

		ModelPart leftWingCube = new ModelPart(this);
		leftWingCube.setPos(0.1339F, -1.0F, 0.0F);
		leftWing.addChild(leftWingCube);
		setRotationAngle(leftWingCube, -0.0873F, 0.7854F, 0.0F);
		leftWingCube.texOffs(5, 45).addBox(-2.1339F, 0.0F, -0.0052F, 4.0F, 2.0F, 6.0F, 0.0F, false);

		rightWing = new ModelPart(this);
		rightWing.setPos(-2.9497F, 0.15F, -1.5145F);
		body.addChild(rightWing);

		ModelPart rightWingCube = new ModelPart(this);
		rightWingCube.setPos(0.0F, -1.0F, 0.0F);
		rightWing.addChild(rightWingCube);
		setRotationAngle(rightWingCube, -0.0873F, 0.7854F, -0.0873F);
		rightWingCube.texOffs(5, 33).addBox(-6.0054F, 0.0109F, -1.5734F, 6.0F, 2.0F, 4.0F, 0.0F, false);


		// CAMERA

		camera = new ModelPart(this);
		camera.setPos(0.0F, 1.65F, -4.3F);
		body.addChild(camera);

		ModelPart cameraCube = new ModelPart(this);
		cameraCube.setPos(0.0F, 0.0F, 0.0F);
		camera.addChild(cameraCube);
		setRotationAngle(cameraCube, -0.9599F, 0.0F, 0.0F);
		cameraCube.texOffs(45, 29).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

	}

	@Override
	public void setupAnim(T redwing, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {

	}

	@Override
	public Iterable<ModelPart> parts() {
		return ImmutableList.of(this.body);
	}


	public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}