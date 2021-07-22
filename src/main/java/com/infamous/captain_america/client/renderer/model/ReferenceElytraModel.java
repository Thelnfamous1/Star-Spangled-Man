package com.infamous.captain_america.client.renderer.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ReferenceElytraModel<T extends LivingEntity> extends AgeableListModel<T> {
   private final ModelPart rightWing;
   private final ModelPart leftWing;

   public ReferenceElytraModel() {
      this.leftWing = new ModelPart(this, 22, 0);
      this.leftWing.addBox(-10.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, 1.0F);

      this.rightWing = new ModelPart(this, 22, 0);
      this.rightWing.mirror = true;
      this.rightWing.addBox(0.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, 1.0F);
   }

   protected Iterable<ModelPart> headParts() {
      return ImmutableList.of();
   }

   protected Iterable<ModelPart> bodyParts() {
      return ImmutableList.of(this.leftWing, this.rightWing);
   }

   public void setupAnim(T living, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      float leftWingXRot = (float) (Math.PI / 12);
      float leftWingYRot = 0.0F;
      float leftWingZRot = (float) -(Math.PI / 12);
      float leftWingYPos = 0.0F;

      if (living.isFallFlying()) {
         float fallFactor = 1.0F;
         Vec3 deltaMove = living.getDeltaMovement();
         if (deltaMove.y < 0.0D) {
            Vec3 normalDeltaMove = deltaMove.normalize();
            fallFactor = 1.0F - (float)Math.pow(-normalDeltaMove.y, 1.5D);
         }

         leftWingXRot = fallFactor * ((float)Math.PI / 9F) + (1.0F - fallFactor) * leftWingXRot;
         leftWingZRot = fallFactor * ((float)-Math.PI / 2F) + (1.0F - fallFactor) * leftWingZRot;
      } else if (living.isCrouching()) {
         leftWingXRot *= (8.0F / 3.0F);
         leftWingYRot = leftWingXRot / 3;
         leftWingZRot *= 3.0F;
         leftWingYPos = 3.0F;
      }

      this.leftWing.x = 5.0F;
      this.leftWing.y = leftWingYPos;
      if (living instanceof AbstractClientPlayer abstractclientplayerentity) {
         abstractclientplayerentity.elytraRotX = (float)((double)abstractclientplayerentity.elytraRotX + (double)(leftWingXRot - abstractclientplayerentity.elytraRotX) * 0.1D);
         abstractclientplayerentity.elytraRotY = (float)((double)abstractclientplayerentity.elytraRotY + (double)(leftWingYRot - abstractclientplayerentity.elytraRotY) * 0.1D);
         abstractclientplayerentity.elytraRotZ = (float)((double)abstractclientplayerentity.elytraRotZ + (double)(leftWingZRot - abstractclientplayerentity.elytraRotZ) * 0.1D);
         this.leftWing.xRot = abstractclientplayerentity.elytraRotX;
         this.leftWing.yRot = abstractclientplayerentity.elytraRotY;
         this.leftWing.zRot = abstractclientplayerentity.elytraRotZ;
      } else {
         this.leftWing.xRot = leftWingXRot;
         this.leftWing.zRot = leftWingZRot;
         this.leftWing.yRot = leftWingYRot;
      }

      this.rightWing.x = -this.leftWing.x;
      this.rightWing.yRot = -this.leftWing.yRot;
      this.rightWing.y = this.leftWing.y;
      this.rightWing.xRot = this.leftWing.xRot;
      this.rightWing.zRot = -this.leftWing.zRot;
   }
}