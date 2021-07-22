package com.infamous.captain_america.common.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.client.IItemRenderProperties;

public class CAArmorItem extends ArmorItem implements IItemRenderProperties {

    public CAArmorItem(ArmorMaterial p_i48534_1_, EquipmentSlot p_i48534_2_, Properties p_i48534_3_) {
        super(p_i48534_1_, p_i48534_2_, p_i48534_3_);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <A extends net.minecraft.client.model.HumanoidModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, A _default) {
        if(armorSlot != EquipmentSlot.LEGS){
            return (A) new net.minecraft.client.model.HumanoidModel<>(0.6F); // no need to have bulky armor at 1.0F inflation
        } else{
            return (A) new net.minecraft.client.model.HumanoidModel<>(0.45F);
        }
    }
}
