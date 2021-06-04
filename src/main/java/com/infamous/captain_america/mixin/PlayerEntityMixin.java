package com.infamous.captain_america.mixin;

import com.infamous.captain_america.common.item.VibraniumShieldItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("RETURN"), method = "createAttributes")
    private static void addKnockbackAttribute(CallbackInfoReturnable<AttributeModifierMap.MutableAttribute> cir){
        AttributeModifierMap.MutableAttribute mutableAttribute = cir.getReturnValue();
        mutableAttribute.add(Attributes.ATTACK_KNOCKBACK);
    }

    @ModifyVariable(at = @At("STORE"), method = "attack", ordinal = 0)
    private int getKnockbackAttributeValue(int knockbackValue, Entity target){
        return (int) this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
    }

    @Inject(at = @At("HEAD"), method = "disableShield", cancellable = true)
    private void disableShieldHook(boolean guaranteeDisable, CallbackInfo callbackInfo){
        if(VibraniumShieldItem.SHIELD_PREDICATE.test(this.useItem.getItem())){
            callbackInfo.cancel();
        }
    }
}
