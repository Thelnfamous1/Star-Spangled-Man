package com.infamous.captain_america.common.item;

import com.infamous.captain_america.common.entity.RedwingEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;

import java.util.Optional;
import java.util.function.Supplier;

public class EXO7FalconItem extends ArmorItem {
    public static final EquipmentSlotType SLOT = EquipmentSlotType.CHEST;
    private final Supplier<EntityType<? extends RedwingEntity>> registeredRedwingType;

    public EXO7FalconItem(Supplier<EntityType<? extends RedwingEntity>> registeredRedwingType,
                          IArmorMaterial armorMaterial,
                          Properties properties) {
        super(armorMaterial, SLOT, properties);
        this.registeredRedwingType = registeredRedwingType;
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
        return registeredRedwingType.get();
    }

    public static boolean isFlightEnabled(ItemStack stack) {
        return stack.getDamageValue() < stack.getMaxDamage() - 1;
    }

    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return isFlightEnabled(stack);
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
