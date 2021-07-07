package com.infamous.captain_america.common.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class CAArmorItem extends ArmorItem {

    public CAArmorItem(IArmorMaterial p_i48534_1_, EquipmentSlotType p_i48534_2_, Properties p_i48534_3_) {
        super(p_i48534_1_, p_i48534_2_, p_i48534_3_);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <A extends net.minecraft.client.renderer.entity.model.BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
        if(armorSlot != EquipmentSlotType.LEGS){
            return (A) new net.minecraft.client.renderer.entity.model.BipedModel<>(0.6F); // no need to have bulky armor at 1.0F inflation
        } else{
            return (A) new net.minecraft.client.renderer.entity.model.BipedModel<>(0.45F);
        }
    }
}
