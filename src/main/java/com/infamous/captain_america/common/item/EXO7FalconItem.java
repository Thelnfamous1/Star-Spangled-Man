package com.infamous.captain_america.common.item;

import com.infamous.captain_america.common.entity.drone.RedwingEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class EXO7FalconItem extends CAArmorItem {
    public static final Predicate<Item> FALCON_PREDICATE =
            item -> item instanceof EXO7FalconItem;
    public static final EquipmentSlotType SLOT = EquipmentSlotType.CHEST;
    private final Supplier<EntityType<? extends RedwingEntity>> redwingTypeSupplier;
    private final IParticleData propulsionParticle;

    public EXO7FalconItem(Supplier<EntityType<? extends RedwingEntity>> redwingTypeSupplier,
                          IArmorMaterial armorMaterial,
                          Properties properties,
                          IParticleData propulsionParticle) {
        super(armorMaterial, SLOT, properties);
        this.redwingTypeSupplier = redwingTypeSupplier;
        this.propulsionParticle = propulsionParticle;
    }

    public static Optional<EntityType<? extends RedwingEntity>> getRedwingType(ItemStack itemStack){
        Item item = itemStack.getItem();
        if(item instanceof EXO7FalconItem){
            EXO7FalconItem exo7FalconItem = (EXO7FalconItem) item;
            return Optional.of(exo7FalconItem.getRedwingType());
        }
        else return Optional.empty();
    }

    public IParticleData getPropulsionParticle() {
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
        CompoundNBT compoundnbt = exo7FalconStack.getTag();
        return compoundnbt != null && compoundnbt.getBoolean("FlightEnabled");
    }

    public static void setFlightEnabled(ItemStack exo7FalconStack, boolean flightEnabled) {
        CompoundNBT compoundnbt = exo7FalconStack.getOrCreateTag();
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
