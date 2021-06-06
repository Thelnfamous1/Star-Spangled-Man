package com.infamous.captain_america.common.item;

import java.util.function.Supplier;

import com.infamous.captain_america.CaptainAmerica;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.LazyValue;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
   private final LazyValue<Ingredient> repairIngredient;

   CAItemTier(String nameIn, int levelIn, int usesIn, float speedIn, float damageIn, int enchantmentValueIn, Supplier<Ingredient> repairIngredientIn) {
      this.name = nameIn;
      this.level = levelIn;
      this.uses = usesIn;
      this.speed = speedIn;
      this.damage = damageIn;
      this.enchantmentValue = enchantmentValueIn;
      this.repairIngredient = new LazyValue<>(repairIngredientIn);
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