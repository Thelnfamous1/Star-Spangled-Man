package com.infamous.captain_america.common.advancements.criterion;

import com.google.gson.JsonObject;
import com.infamous.captain_america.CaptainAmerica;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class HitByShieldTrigger extends SimpleCriterionTrigger<HitByShieldTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation(CaptainAmerica.MODID,"hit_by_shield");

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   @Override
   public HitByShieldTrigger.Instance createInstance(JsonObject jsonObject, EntityPredicate.Composite andPredicate, DeserializationContext conditionArrayParser) {
      EntityPredicate.Composite[] aentitypredicate$andpredicate = EntityPredicate.Composite.fromJsonArray(jsonObject, "victims", conditionArrayParser);
      MinMaxBounds.Ints minmaxbounds$intbound = MinMaxBounds.Ints.fromJson(jsonObject.get("unique_entities"));
      return new HitByShieldTrigger.Instance(andPredicate, aentitypredicate$andpredicate, minmaxbounds$intbound);
   }

   /*
    Non-javadoc comment: Original vanilla implementation of KilledByCrossbow Trigger
    requires that all the hit entities be of different types, rather than just simply unique
    mobs differentiated by their network ids. I found that to be an unnecessary requirement
    for this trigger, so I changed it below.
    */
   public void trigger(ServerPlayer serverPlayer, Collection<Entity> entityCollection) {
      List<LootContext> lootContexts = new ArrayList<>();
      //Set<EntityType<?>> set = Sets.newHashSet();

      for(Entity entity : entityCollection) {
         //set.add(entity.getType());
         lootContexts.add(EntityPredicate.createContext(serverPlayer, entity));
      }

      this.trigger(serverPlayer, (instance) -> {
         //int hitEntityCount = set.size();
         int hitEntityCount = lootContexts.size();
         return instance.matches(lootContexts, hitEntityCount);
      });
   }

   public static class Instance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate.Composite[] victims;
      private final MinMaxBounds.Ints uniqueEntities;

      public Instance(EntityPredicate.Composite andPredicate, EntityPredicate.Composite[] andPredicates, MinMaxBounds.Ints minMaxBounds) {
         super(HitByShieldTrigger.ID, andPredicate);
         this.victims = andPredicates;
         this.uniqueEntities = minMaxBounds;
      }

      public static HitByShieldTrigger.Instance shieldHit(EntityPredicate.Builder... builders) {
         EntityPredicate.Composite[] aentitypredicate$andpredicate = new EntityPredicate.Composite[builders.length];

         for(int i = 0; i < builders.length; ++i) {
            EntityPredicate.Builder entitypredicate$builder = builders[i];
            aentitypredicate$andpredicate[i] = EntityPredicate.Composite.wrap(entitypredicate$builder.build());
         }

         return new HitByShieldTrigger.Instance(EntityPredicate.Composite.ANY, aentitypredicate$andpredicate, MinMaxBounds.Ints.ANY);
      }

      public static HitByShieldTrigger.Instance shieldHit(MinMaxBounds.Ints minMaxBounds) {
         EntityPredicate.Composite[] aentitypredicate$andpredicate = new EntityPredicate.Composite[0];
         return new HitByShieldTrigger.Instance(EntityPredicate.Composite.ANY, aentitypredicate$andpredicate, minMaxBounds);
      }

      public boolean matches(Collection<LootContext> lootContexts, int value) {
         if (this.victims.length > 0) {
            List<LootContext> list = new ArrayList<>(lootContexts);

            for(EntityPredicate.Composite entitypredicate$andpredicate : this.victims) {
               boolean foundMatch = false;
               Iterator<LootContext> iterator = list.iterator();

               while(iterator.hasNext()) {
                  LootContext lootcontext = iterator.next();
                  if (entitypredicate$andpredicate.matches(lootcontext)) {
                     iterator.remove();
                     foundMatch = true;
                     break;
                  }
               }

               if (!foundMatch) {
                  return false;
               }
            }
         }

         return this.uniqueEntities.matches(value);
      }

      @Override
      public JsonObject serializeToJson(SerializationContext serializer) {
         JsonObject jsonobject = super.serializeToJson(serializer);
         jsonobject.add("victims", EntityPredicate.Composite.toJson(this.victims, serializer));
         jsonobject.add("unique_entities", this.uniqueEntities.serializeToJson());
         return jsonobject;
      }
   }
}