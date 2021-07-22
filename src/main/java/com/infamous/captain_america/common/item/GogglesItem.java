package com.infamous.captain_america.common.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;
import java.util.function.Predicate;

import net.minecraft.world.item.Item.Properties;

public class GogglesItem extends CAArmorItem {

    public static final Predicate<Item> GOGGLES_PREDICATE = item -> item instanceof GogglesItem;
    public static final EquipmentSlot SLOT = EquipmentSlot.HEAD;
    public GogglesItem(ArmorMaterial armorMaterial, Properties properties) {
        super(armorMaterial, SLOT, properties);
    }

    public static Optional<ItemStack> getGoggles(LivingEntity living){
        ItemStack goggles = living.getItemBySlot(SLOT);
        if(GOGGLES_PREDICATE.test(goggles.getItem())){
            return Optional.of(goggles);
        } else{
            return Optional.empty();
        }
    }

    public static boolean toggleHUD(LivingEntity living) {
        Optional<ItemStack> optionalGoggles = getGoggles(living);
        if(optionalGoggles.isPresent()){
            ItemStack gogglesStack = optionalGoggles.get();
            boolean isEnabled = isHUDEnabled(gogglesStack);
            setHUDEnabled(gogglesStack, !isEnabled);
            return !isEnabled;
        } else{
            return false;
        }
    }

    public static void toggleHUDTo(LivingEntity livingEntity, boolean toggleTo){
        Optional<ItemStack> optionalGoggles = getGoggles(livingEntity);
        if(optionalGoggles.isPresent()){
            ItemStack gogglesStack = optionalGoggles.get();
            setHUDEnabled(gogglesStack, toggleTo);
        }
    }

    public static boolean isHUDEnabled(ItemStack gogglesStack) {
        CompoundTag compoundnbt = gogglesStack.getTag();
        return compoundnbt != null && compoundnbt.getBoolean("HUDEnabled");
    }

    public static void setHUDEnabled(ItemStack gogglesStack, boolean enabled) {
        CompoundTag compoundnbt = gogglesStack.getOrCreateTag();
        compoundnbt.putBoolean("HUDEnabled", enabled);
    }
}
