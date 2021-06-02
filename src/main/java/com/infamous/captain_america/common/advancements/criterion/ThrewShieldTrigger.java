package com.infamous.captain_america.common.advancements.criterion;

import com.google.gson.JsonObject;
import com.infamous.captain_america.CaptainAmerica;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class ThrewShieldTrigger extends AbstractCriterionTrigger<ThrewShieldTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation(CaptainAmerica.MODID,"threw_shield");

   public ResourceLocation getId() {
      return ID;
   }

   public ThrewShieldTrigger.Instance createInstance(JsonObject jsonObject, EntityPredicate.AndPredicate andPredicate, ConditionArrayParser conditionArrayParser) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(jsonObject.get("item"));
      return new ThrewShieldTrigger.Instance(andPredicate, itempredicate);
   }

   public void trigger(ServerPlayerEntity serverPlayer, ItemStack itemStack) {
      this.trigger(serverPlayer, (instance) -> instance.matches(itemStack));
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate item;

      public Instance(EntityPredicate.AndPredicate andPredicate, ItemPredicate itemPredicate) {
         super(ThrewShieldTrigger.ID, andPredicate);
         this.item = itemPredicate;
      }

      public static ThrewShieldTrigger.Instance threwShield(IItemProvider itemProvider) {
         return new ThrewShieldTrigger.Instance(EntityPredicate.AndPredicate.ANY, ItemPredicate.Builder.item().of(itemProvider).build());
      }

      public boolean matches(ItemStack stack) {
         return this.item.matches(stack);
      }

      public JsonObject serializeToJson(ConditionArraySerializer serializer) {
         JsonObject jsonobject = super.serializeToJson(serializer);
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}