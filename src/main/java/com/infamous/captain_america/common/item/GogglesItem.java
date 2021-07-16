package com.infamous.captain_america.common.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.Optional;
import java.util.function.Predicate;

public class GogglesItem extends CAArmorItem {

    public static final Predicate<Item> GOGGLES_PREDICATE = item -> item instanceof GogglesItem;
    public static final EquipmentSlotType SLOT = EquipmentSlotType.HEAD;
    public GogglesItem(IArmorMaterial armorMaterial, Properties properties) {
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
        CompoundNBT compoundnbt = gogglesStack.getTag();
        return compoundnbt != null && compoundnbt.getBoolean("HUDEnabled");
    }

    public static void setHUDEnabled(ItemStack gogglesStack, boolean enabled) {
        CompoundNBT compoundnbt = gogglesStack.getOrCreateTag();
        compoundnbt.putBoolean("HUDEnabled", enabled);
    }
}
