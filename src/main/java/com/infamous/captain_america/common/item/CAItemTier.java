package com.infamous.captain_america.common.item;

import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum CAItemTier implements INamedItemTier {
   VIBRANIUM("vibranium", 3, 1561, 8.0F, 3.0F, 10, () -> {
      return Ingredient.of(Items.DIAMOND);
   });

   private final String name;
   private final int level;
   private final int uses;
   private final float speed;
   private final float damage;
   private final int enchantmentValue;
   private final LazyLoadedValue<Ingredient> repairIngredient;

   CAItemTier(String nameIn, int levelIn, int usesIn, float speedIn, float damageIn, int enchantmentValueIn, Supplier<Ingredient> repairIngredientIn) {
      this.name = nameIn;
      this.level = levelIn;
      this.uses = usesIn;
      this.speed = speedIn;
      this.damage = damageIn;
      this.enchantmentValue = enchantmentValueIn;
      this.repairIngredient = new LazyLoadedValue<>(repairIngredientIn);
   }

   public int getUses() {
      return this.uses;
   }

   public float getSpeed() {
      return this.speed;
   }

   public float getAttackDamageBonus() {
      return this.damage;
   }

   public int getLevel() {
      return this.level;
   }

   public int getEnchantmentValue() {
      return this.enchantmentValue;
   }

   public Ingredient getRepairIngredient() {
      return this.repairIngredient.get();
   }

   @Override
   public String getName() {
      return this.name;
   }
}