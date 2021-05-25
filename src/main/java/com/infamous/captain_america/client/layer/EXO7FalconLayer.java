package com.infamous.captain_america.client.layer;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.item.EXO7FalconItem;
import com.infamous.captain_america.common.registry.ItemRegistry;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EXO7FalconLayer<T extends LivingEntity, M extends EntityModel<T>> extends ElytraLayer<T, M> {
    private static final ResourceLocation FALCON_WINGSUIT_TEXTURE = new ResourceLocation(CaptainAmerica.MODID, "textures/entity/falcon_wingsuit.png");
    private static final ResourceLocation CAPTAIN_AMERICA_WINGSUIT_TEXTURE = new ResourceLocation(CaptainAmerica.MODID, "textures/entity/captain_america_wingsuit.png");

    public EXO7FalconLayer(IEntityRenderer<T, M> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public boolean shouldRender(ItemStack stack, T entity) {
        return stack.getItem() instanceof EXO7FalconItem && EXO7FalconItem.isFlightEnabled(stack);
    }

    @Override
    public ResourceLocation getElytraTexture(ItemStack stack, T entity) {
        if(stack.getItem() == ItemRegistry.FALCON_WINGSUIT.get()){
            return FALCON_WINGSUIT_TEXTURE;
        } else if(stack.getItem() == ItemRegistry.CAPTAIN_AMERICA_WINGSUIT.get()){
            return CAPTAIN_AMERICA_WINGSUIT_TEXTURE;
        }
        else return super.getElytraTexture(stack, entity);
    }
}
