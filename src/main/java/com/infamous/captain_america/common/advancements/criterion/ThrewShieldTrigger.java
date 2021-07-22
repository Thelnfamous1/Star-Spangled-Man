package com.infamous.captain_america.common.advancements.criterion;

import com.google.gson.JsonObject;
import com.infamous.captain_america.CaptainAmerica;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.resources.ResourceLocation;

public class ThrewShieldTrigger extends SimpleCriterionTrigger<ThrewShieldTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation(CaptainAmerica.MODID,"threw_shield");

   public ResourceLocation getId() {
      return ID;
   }

   public ThrewShieldTrigger.Instance createInstance(JsonObject jsonObject, EntityPredicate.Composite andPredicate, DeserializationContext conditionArrayParser) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(jsonObject.get("item"));
      return new ThrewShieldTrigger.Instance(andPredicate, itempredicate);
   }

   public void trigger(ServerPlayer serverPlayer, ItemStack itemStack) {
      this.trigger(serverPlayer, (instance) -> instance.matches(itemStack));
   }

   public static class Instance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;

      public Instance(EntityPredicate.Composite andPredicate, ItemPredicate itemPredicate) {
         super(ThrewShieldTrigger.ID, andPredicate);
         this.item = itemPredicate;
      }

      public static ThrewShieldTrigger.Instance threwShield(ItemLike itemProvider) {
         return new ThrewShieldTrigger.Instance(EntityPredicate.Composite.ANY, ItemPredicate.Builder.item().of(itemProvider).build());
      }

      public boolean matches(ItemStack stack) {
         return this.item.matches(stack);
      }

      public JsonObject serializeToJson(SerializationContext serializer) {
         JsonObject jsonobject = super.serializeToJson(serializer);
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}