package com.infamous.captain_america.client.renderer.model;// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.15 - 1.16
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;

public class VibraniumShieldModel1 extends Model {
	private final ModelPart shield;
	private final ModelPart rightHandle;
	private final ModelPart leftHandle;

	public VibraniumShieldModel1() {
		super(RenderType::entitySolid);
		texWidth = 64;
		texHeight = 64;

		shield = new ModelPart(this);
		shield.setPos(0.0F - 1.0F, 24.0F - 15.0F, 0.0F - 3.0F);
		shield.texOffs(0, 0).addBox(-7.0F, -17.0F, -0.75F, 16.0F, 16.0F, 1.0F, 0.0F, false);

		rightHandle = new ModelPart(this);
		rightHandle.setPos(8.5F, -10.0F, -0.25F);
		shield.addChild(rightHandle);
		rightHandle.texOffs(0, 17).addBox(-11.9019F, -2.0F, 2.0F, 2.0F, 6.0F, 6.0F, 0.0F, false);

		ModelPart rightAttachment = new ModelPart(this);
		rightAttachment.setPos(0.0F, 0.0F, 0.0F);
		rightHandle.addChild(rightAttachment);
		setRotationAngle(rightAttachment, 0.0F, -0.5236F, 0.0F);
		rightAttachment.texOffs(0, 17).addBox(-12.3074F, -2.0F, 7.682F, 3.0F, 6.0F, 0.0F, 0.0F, false);

		leftHandle = new ModelPart(this);
		leftHandle.setPos(1.4019F, 0.0F, 0.25F);
		shield.addChild(leftHandle);
		leftHandle.texOffs(16, 17).addBox(1.9995F, -12.0F, 1.4991F, 2.0F, 6.0F, 6.0F, 0.0F, false);

		ModelPart leftAttachment = new ModelPart(this);
		leftAttachment.setPos(-5.9019F, -10.0F, -1.0F);
		leftHandle.addChild(leftAttachment);
		setRotationAngle(leftAttachment, 0.0F, 0.5236F, 0.0F);
		leftAttachment.texOffs(10, 17).addBox(7.3253F, -2.0F, 7.114F, 3.0F, 6.0F, 0.0F, 0.0F, false);
	}

	public ModelPart shield() {
		return this.shield;
	}

	public void renderToBuffer(PoseStack matrixStack, VertexConsumer vertexBuilder, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
		this.shield.render(matrixStack, vertexBuilder, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
	}

	public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}