package com.infamous.captain_america.mixin;

import com.infamous.captain_america.common.registry.EffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LightTexture.class)
public class LightTextureMixin {

    @Final
    @Shadow
    private Minecraft minecraft;

    @ModifyVariable(at = @At(value = "STORE"), method = "updateLightTexture", ordinal = 4)
    private float checkHUDNightVision(float original){
        if(this.minecraft.player != null && this.minecraft.player.hasEffect(EffectRegistry.HUD_NIGHT_VISION.get())){
            return 1.0F; // Pretend we have an active NIGHT_VISION effect with a duration greater than 10 seconds
        }
        return original;
    }
}