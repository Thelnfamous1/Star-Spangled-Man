package com.infamous.captain_america.client.renderer.model;

import com.infamous.captain_america.CaptainAmerica;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class CARenderMaterial implements DistExecutor.SafeCallable<net.minecraft.client.resources.model.Material> {
    public static final net.minecraft.client.resources.model.Material VIBRANIUM_SHIELD =
            new net.minecraft.client.resources.model.Material(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(CaptainAmerica.MODID, "entity/shield/vibranium_shield"));
    public static final net.minecraft.client.resources.model.Material CAPTAIN_AMERICA_SHIELD =
            new net.minecraft.client.resources.model.Material(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(CaptainAmerica.MODID,"entity/shield/captain_america_shield"));
    public static final net.minecraft.client.resources.model.Material US_AGENT_SHIELD =
            new net.minecraft.client.resources.model.Material(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(CaptainAmerica.MODID,"entity/shield/us_agent_shield"));

    private Supplier<Callable<net.minecraft.client.resources.model.Material>> renderMaterial;

    public CARenderMaterial(){

    }

    @Override
    public net.minecraft.client.resources.model.Material call() throws Exception {
        return this.renderMaterial.get().call();
    }

    public CARenderMaterial set(Supplier<Callable<net.minecraft.client.resources.model.Material>> callableSupplier) {
        this.renderMaterial = callableSupplier;
        return this;
    }

    public static net.minecraft.client.resources.model.Material getCaptainAmericaShield(){
        return CAPTAIN_AMERICA_SHIELD;
    }

    public static net.minecraft.client.resources.model.Material getVibraniumShield(){
        return VIBRANIUM_SHIELD;
    }

    public static net.minecraft.client.resources.model.Material getUSAgentShield(){
        return US_AGENT_SHIELD;
    }

}
