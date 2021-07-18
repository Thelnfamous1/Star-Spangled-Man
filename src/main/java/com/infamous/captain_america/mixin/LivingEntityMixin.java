package com.infamous.captain_america.mixin;

import com.infamous.captain_america.common.util.FalconFlightHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
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
        }
    }

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
