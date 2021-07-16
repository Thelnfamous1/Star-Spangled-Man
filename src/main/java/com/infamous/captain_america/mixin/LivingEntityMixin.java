package com.infamous.captain_america.mixin;

import com.infamous.captain_america.common.util.FalconFlightHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Final
    @Shadow
    private static AttributeModifier SLOW_FALLING;

    @Shadow public abstract boolean isFallFlying();

    public LivingEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isFallFlying()Z"),
            method = "travel",
            cancellable = true)
    private void isDiving(Vector3d travelVec, CallbackInfo ci){
        if(FalconFlightHelper.isDiving((LivingEntity)(Object)this)){
            ci.cancel();
            this.dive(travelVec);
            this.calculateEntityAnimation((LivingEntity)(Object)this, this instanceof IFlyingAnimal);
        } else if(FalconFlightHelper.isBarrelRolling((LivingEntity)(Object)this)){
            ci.cancel();
            this.barrelRoll(travelVec);
            this.calculateEntityAnimation((LivingEntity)(Object)this, this instanceof IFlyingAnimal);
        }
    }

    private void barrelRoll(Vector3d travelVec) {
        Vector3d initialDeltaMove = this.getDeltaMovement();
        if (initialDeltaMove.y > -0.5D) {
            this.fallDistance = 1.0F;
        }

        Vector3d lookAngle = this.getLookAngle();
        float rotationPitch = this.xRot * ((float)Math.PI / 180F);
        double lookHorizDist = Math.sqrt(lookAngle.x * lookAngle.x + lookAngle.z * lookAngle.z);
        double initialMoveHorizDist = Math.sqrt(getHorizontalDistanceSqr(initialDeltaMove));
        double lookLength = lookAngle.length();
        float cosRotationPitch = MathHelper.cos(rotationPitch);
        cosRotationPitch = (float)((double)cosRotationPitch * (double)cosRotationPitch * Math.min(1.0D, lookLength / 0.4D));
        initialDeltaMove = this.getDeltaMovement().add(0.0D, this.getGravity() * (-1.0D + (double)cosRotationPitch * 0.75D), 0.0D);
        if (initialDeltaMove.y < 0.0D && lookHorizDist > 0.0D) {
            double d5 = initialDeltaMove.y * -0.1D * (double)cosRotationPitch;
            initialDeltaMove = initialDeltaMove.add(lookAngle.x * d5 / lookHorizDist, d5, lookAngle.z * d5 / lookHorizDist);
        }

        if (rotationPitch < 0.0F && lookHorizDist > 0.0D) {
            double d9 = initialMoveHorizDist * (double)(-MathHelper.sin(rotationPitch)) * 0.04D;
            initialDeltaMove = initialDeltaMove.add(-lookAngle.x * d9 / lookHorizDist, d9 * 3.2D, -lookAngle.z * d9 / lookHorizDist);
        }

        if (lookHorizDist > 0.0D) {
            initialDeltaMove = initialDeltaMove.add((lookAngle.x / lookHorizDist * initialMoveHorizDist - initialDeltaMove.x) * 0.1D, 0.0D, (lookAngle.z / lookHorizDist * initialMoveHorizDist - initialDeltaMove.z) * 0.1D);
        }

        this.setDeltaMovement(initialDeltaMove.multiply((double)0.99F, (double)0.98F, (double)0.99F));
        float barrelRollScale = 10;
        this.moveRelative(0.02F * barrelRollScale, new Vector3d(travelVec.x, 0.0D, 0.0D)); // so we can move accordingly
        this.move(MoverType.SELF, this.getDeltaMovement());
        if (this.horizontalCollision && !this.level.isClientSide) {
            double currentMoveHorizDist = Math.sqrt(getHorizontalDistanceSqr(this.getDeltaMovement()));
            double moveHorizDistDiff = initialMoveHorizDist - currentMoveHorizDist;
            float kineticDamage = (float)(moveHorizDistDiff * 10.0D - 3.0D);
            if (kineticDamage > 0.0F) {
                this.playSound(this.getFallDamageSound((int)kineticDamage), 1.0F, 1.0F);
                this.hurt(DamageSource.FLY_INTO_WALL, kineticDamage);
            }
        }

        if (this.onGround && !this.level.isClientSide) {
            this.setSharedFlag(7, false);
        }
    }

    private double getGravity(){
        double d0 = 0.08D;
        ModifiableAttributeInstance gravity = this.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
        boolean flag = this.getDeltaMovement().y <= 0.0D;
        if (flag && this.hasEffect(Effects.SLOW_FALLING)) {
            if (!gravity.hasModifier(SLOW_FALLING)) gravity.addTransientModifier(SLOW_FALLING);
            this.fallDistance = 0.0F;
        } else if (gravity.hasModifier(SLOW_FALLING)) {
            gravity.removeModifier(SLOW_FALLING);
        }
        return gravity.getValue();
    }

    @Shadow
    protected abstract SoundEvent getFallDamageSound(int f2);

    private void dive(Vector3d travelVec){
        double gravityValue = 0.08D;
        ModifiableAttributeInstance gravity = this.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
        gravityValue = gravity.getValue();

        BlockPos posBelowThatAffectsMyMovement = this.getBlockPosBelowThatAffectsMyMovement();
        float slipperiness = this.level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getSlipperiness(level, this.getBlockPosBelowThatAffectsMyMovement(), this);
        float horizMoveFactor = this.onGround ? slipperiness * 0.91F : 0.91F;
        Vector3d frictionMovement = this.handleRelativeFrictionAndCalculateMovement(travelVec, slipperiness);
        double vertMovement = frictionMovement.y;
        if (this.hasEffect(Effects.LEVITATION)) {
            vertMovement += (0.05D * (double)(this.getEffect(Effects.LEVITATION).getAmplifier() + 1) - frictionMovement.y) * 0.2D;
            this.fallDistance = 0.0F;
        } else if (this.level.isClientSide && !this.level.hasChunkAt(posBelowThatAffectsMyMovement)) {
            if (this.getY() > 0.0D) {
                vertMovement = -0.1D;
            } else {
                vertMovement = 0.0D;
            }
        } else if (!this.isNoGravity()) {
            vertMovement -= gravityValue;
        }

        this.setDeltaMovement(frictionMovement.x * (double)horizMoveFactor, vertMovement * (double)0.98F, frictionMovement.z * (double)horizMoveFactor);
    }

    @Shadow
    public abstract EffectInstance getEffect(Effect effect);

    @Shadow
    public abstract Vector3d handleRelativeFrictionAndCalculateMovement(Vector3d travelVec, float f3);

    @Shadow
    public abstract boolean hasEffect(Effect effect);

    @Shadow
    public abstract void calculateEntityAnimation(LivingEntity livingEntity, boolean b);

    @Shadow
    public abstract ModifiableAttributeInstance getAttribute(Attribute attribute);

}
