package com.infamous.captain_america.client.renderer.model;

import com.infamous.captain_america.CaptainAmerica;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class CARenderMaterial implements DistExecutor.SafeCallable<RenderMaterial> {
    public static final net.minecraft.client.renderer.model.RenderMaterial VIBRANIUM_SHIELD = new net.minecraft.client.renderer.model.RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation(CaptainAmerica.MODID, "entity/shield/vibranium_shield"));
    public static final net.minecraft.client.renderer.model.RenderMaterial CAPTAIN_AMERICA_SHIELD = new net.minecraft.client.renderer.model.RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation(CaptainAmerica.MODID,"entity/shield/captain_america_shield"));

    private Supplier<Callable<net.minecraft.client.renderer.model.RenderMaterial>> renderMaterial;

    public CARenderMaterial(){

    }

    @Override
    public RenderMaterial call() throws Exception {
        return this.renderMaterial.get().call();
    }

    public CARenderMaterial set(Supplier<Callable<net.minecraft.client.renderer.model.RenderMaterial>> callableSupplier) {
        this.renderMaterial = callableSupplier;
        return this;
    }

    public static net.minecraft.client.renderer.model.RenderMaterial getCaptainAmericaShield(){
        return CAPTAIN_AMERICA_SHIELD;
    }

    public static Callable<net.minecraft.client.renderer.model.RenderMaterial> getCaptainAmericaShieldCallable(){
        return CARenderMaterial::getCaptainAmericaShield;
    }

    public static net.minecraft.client.renderer.model.RenderMaterial getVibraniumShield(){
        return VIBRANIUM_SHIELD;
    }

    public static Callable<net.minecraft.client.renderer.model.RenderMaterial> getVibraniumShieldCallable(){
        return CARenderMaterial::getVibraniumShield;
    }
}
