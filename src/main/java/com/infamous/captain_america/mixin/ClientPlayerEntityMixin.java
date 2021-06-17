package com.infamous.captain_america.mixin;

import com.infamous.captain_america.common.item.VibraniumShieldItem;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Shadow public abstract boolean isUsingItem();

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/ClientPlayerEntity;isUsingItem()Z"), method = "aiStep")
    private boolean isUsingItemOrUseItemIsVibraniumShield(ClientPlayerEntity clientPlayerEntity){
        return this.isUsingItem() && !VibraniumShieldItem.isShieldStack(clientPlayerEntity.getUseItem());
    }
}
