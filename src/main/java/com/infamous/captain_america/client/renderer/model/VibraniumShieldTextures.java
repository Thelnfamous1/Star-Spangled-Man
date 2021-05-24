package com.infamous.captain_america.client.renderer.model;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.item.VibraniumShieldItem;
import com.infamous.captain_america.common.registry.ItemRegistry;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;

public class VibraniumShieldTextures {
    public static final RenderMaterial VIBRANIUM_SHIELD = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation(CaptainAmerica.MODID, "entity/shield/vibranium_shield"));
    public static final RenderMaterial CAPTAIN_AMERICA_SHIELD = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation(CaptainAmerica.MODID,"entity/shield/captain_america_shield"));

    public static RenderMaterial getShieldTexture(VibraniumShieldItem item) {
        if(item == ItemRegistry.CAPTAIN_AMERICA_SHIELD.get()){
            return CAPTAIN_AMERICA_SHIELD;
        } else{
            return VIBRANIUM_SHIELD;
        }
    }
}
