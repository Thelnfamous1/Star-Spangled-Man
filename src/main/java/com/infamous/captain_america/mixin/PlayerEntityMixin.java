package com.infamous.captain_america.mixin;

import com.infamous.captain_america.common.item.VibraniumShieldItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "disableShield", cancellable = true)
    private void disableShieldHook(boolean guaranteeDisable, CallbackInfo callbackInfo){
        if(VibraniumShieldItem.SHIELD_PREDICATE.test(this.useItem.getItem())){
            callbackInfo.cancel();
        }
    }
}
