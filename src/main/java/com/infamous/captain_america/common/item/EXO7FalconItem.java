package com.infamous.captain_america.common.item;

import com.infamous.captain_america.common.entity.drone.RedwingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleOptions;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.world.item.Item.Properties;

public class EXO7FalconItem extends CAArmorItem {
    public static final Predicate<Item> FALCON_PREDICATE =
            item -> item instanceof EXO7FalconItem;
    public static final EquipmentSlot SLOT = EquipmentSlot.CHEST;
    private final Supplier<EntityType<? extends RedwingEntity>> redwingTypeSupplier;
    private final ParticleOptions propulsionParticle;

    public EXO7FalconItem(Supplier<EntityType<? extends RedwingEntity>> redwingTypeSupplier,
                          ArmorMaterial armorMaterial,
                          Properties properties,
                          ParticleOptions propulsionParticle) {
        super(armorMaterial, SLOT, properties);
        this.redwingTypeSupplier = redwingTypeSupplier;
        this.propulsionParticle = propulsionParticle;
    }

    public static Optional<EntityType<? extends RedwingEntity>> getRedwingType(ItemStack itemStack){
        Item item = itemStack.getItem();
        if(item instanceof EXO7FalconItem exo7FalconItem){
            return Optional.of(exo7FalconItem.getRedwingType());
        }
        else return Optional.empty();
    }

    public ParticleOptions getPropulsionParticle() {
        return this.propulsionParticle;
    }

    public EntityType<? extends RedwingEntity> getRedwingType() {
        return redwingTypeSupplier.get();
    }

    public static boolean isBroken(ItemStack stack){
        return stack.getDamageValue() >= stack.getMaxDamage() - 1;
    }

    public static boolean isFlightEnabled(ItemStack exo7FalconStack) {
        if(isBroken(exo7FalconStack)){
            return false;
        }
        CompoundTag compoundnbt = exo7FalconStack.getTag();
        return compoundnbt != null && compoundnbt.getBoolean("FlightEnabled");
    }

    public static void setFlightEnabled(ItemStack exo7FalconStack, boolean flightEnabled) {
        CompoundTag compoundnbt = exo7FalconStack.getOrCreateTag();
        compoundnbt.putBoolean("FlightEnabled", flightEnabled);
    }

    public static Optional<ItemStack> getEXO7FalconStack(LivingEntity living){
        ItemStack exo7FalconStack = living.getItemBySlot(SLOT);
        if(isEXO7FalconStack(exo7FalconStack)){
            return Optional.of(exo7FalconStack);
        } else{
            return Optional.empty();
        }
    }

    public static Optional<EXO7FalconItem> getEXO7FalconItem(LivingEntity livingEntity){
        Optional<ItemStack> exo7FalconStack = getEXO7FalconStack(livingEntity);
        if(exo7FalconStack.isPresent()) {
            Item item = exo7FalconStack.get().getItem();
            // Directly downcasting since this should be an EXO7FalconItem given the checks above
            // If this fails, we definitely want to know about it
            return Optional.of((EXO7FalconItem) item);
        }
        return Optional.empty();
    }

    public static boolean isEXO7FalconStack(ItemStack stack){
        return FALCON_PREDICATE.test(stack.getItem());
    }


    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return isEXO7FalconStack(stack);
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
