package com.infamous.captain_america.common.item;

import com.infamous.captain_america.common.entity.drone.RedwingEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class EXO7FalconItem extends CAArmorItem {
    public static final Predicate<Item> FALCON_PREDICATE =
            item -> item instanceof EXO7FalconItem;
    public static final EquipmentSlotType SLOT = EquipmentSlotType.CHEST;
    private final Supplier<EntityType<? extends RedwingEntity>> redwingTypeSupplier;

    public EXO7FalconItem(Supplier<EntityType<? extends RedwingEntity>> redwingTypeSupplier,
                          IArmorMaterial armorMaterial,
                          Properties properties) {
        super(armorMaterial, SLOT, properties);
        this.redwingTypeSupplier = redwingTypeSupplier;
    }

    public static Optional<EntityType<? extends RedwingEntity>> getRedwingType(ItemStack itemStack){
        Item item = itemStack.getItem();
        if(item instanceof EXO7FalconItem){
            EXO7FalconItem exo7FalconItem = (EXO7FalconItem) item;
            return Optional.of(exo7FalconItem.getRedwingType());
        }
        else return Optional.empty();
    }

    public EntityType<? extends RedwingEntity> getRedwingType() {
        return redwingTypeSupplier.get();
    }

    public static boolean isBroken(ItemStack stack){
        return stack.getDamageValue() >= stack.getMaxDamage() - 1;
    }

    public static boolean isFlightEnabled(ItemStack stack) {
        if(isBroken(stack)){
            return false;
        }
        CompoundNBT compoundnbt = stack.getTag();
        return compoundnbt != null && compoundnbt.getBoolean("FlightEnabled");
    }

    public static void setFlightEnabled(ItemStack stack, boolean flightEnabled) {
        CompoundNBT compoundnbt = stack.getOrCreateTag();
        compoundnbt.putBoolean("FlightEnabled", flightEnabled);
    }

    public static boolean isEXO7FalconStack(ItemStack stack){
        return FALCON_PREDICATE.test(stack.getItem());
    }


    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return FALCON_PREDICATE.test(stack.getItem());
    }

    @Override
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        /*
        if (!entity.level.isClientSide && (flightTicks + 1) % 20 == 0) {
            stack.hurtAndBreak(1, entity, e -> e.broadcastBreakEvent(EquipmentSlotType.CHEST));
        }
         */
        return true;
    }
}
