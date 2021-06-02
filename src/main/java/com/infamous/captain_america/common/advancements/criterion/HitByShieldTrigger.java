package com.infamous.captain_america.common.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;

import java.util.*;

import com.infamous.captain_america.CaptainAmerica;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;

public class HitByShieldTrigger extends AbstractCriterionTrigger<HitByShieldTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation(CaptainAmerica.MODID,"hit_by_shield");

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   @Override
   public HitByShieldTrigger.Instance createInstance(JsonObject jsonObject, EntityPredicate.AndPredicate andPredicate, ConditionArrayParser conditionArrayParser) {
      EntityPredicate.AndPredicate[] aentitypredicate$andpredicate = EntityPredicate.AndPredicate.fromJsonArray(jsonObject, "victims", conditionArrayParser);
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(jsonObject.get("unique_entity_types"));
      return new HitByShieldTrigger.Instance(andPredicate, aentitypredicate$andpredicate, minmaxbounds$intbound);
   }

   public void trigger(ServerPlayerEntity serverPlayer, Collection<Entity> entityCollection) {
      List<LootContext> list = Lists.newArrayList();
      Set<EntityType<?>> set = Sets.newHashSet();

      for(Entity entity : entityCollection) {
         set.add(entity.getType());
         list.add(EntityPredicate.createContext(serverPlayer, entity));
      }

      this.trigger(serverPlayer, (instance) -> instance.matches(list, set.size()));
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate.AndPredicate[] victims;
      private final MinMaxBounds.IntBound uniqueEntityTypes;

      public Instance(EntityPredicate.AndPredicate andPredicate, EntityPredicate.AndPredicate[] andPredicates, MinMaxBounds.IntBound minMaxBounds) {
         super(HitByShieldTrigger.ID, andPredicate);
         this.victims = andPredicates;
         this.uniqueEntityTypes = minMaxBounds;
      }

      public static HitByShieldTrigger.Instance shieldHit(EntityPredicate.Builder... builders) {
         EntityPredicate.AndPredicate[] aentitypredicate$andpredicate = new EntityPredicate.AndPredicate[builders.length];

         for(int i = 0; i < builders.length; ++i) {
            EntityPredicate.Builder entitypredicate$builder = builders[i];
            aentitypredicate$andpredicate[i] = EntityPredicate.AndPredicate.wrap(entitypredicate$builder.build());
         }

         return new HitByShieldTrigger.Instance(EntityPredicate.AndPredicate.ANY, aentitypredicate$andpredicate, MinMaxBounds.IntBound.ANY);
      }

      public static HitByShieldTrigger.Instance shieldHit(MinMaxBounds.IntBound minMaxBounds) {
         EntityPredicate.AndPredicate[] aentitypredicate$andpredicate = new EntityPredicate.AndPredicate[0];
         return new HitByShieldTrigger.Instance(EntityPredicate.AndPredicate.ANY, aentitypredicate$andpredicate, minMaxBounds);
      }

      public boolean matches(Collection<LootContext> lootContexts, int value) {
         if (this.victims.length > 0) {
            List<LootContext> list = Lists.newArrayList(lootContexts);

            for(EntityPredicate.AndPredicate entitypredicate$andpredicate : this.victims) {
               boolean flag = false;
               Iterator<LootContext> iterator = list.iterator();

               while(iterator.hasNext()) {
                  LootContext lootcontext = iterator.next();
                  if (entitypredicate$andpredicate.matches(lootcontext)) {
                     iterator.remove();
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  return false;
               }
            }
         }

         return this.uniqueEntityTypes.matches(value);
      }

      @Override
      public JsonObject serializeToJson(ConditionArraySerializer serializer) {
         JsonObject jsonobject = super.serializeToJson(serializer);
         jsonobject.add("victims", EntityPredicate.AndPredicate.toJson(this.victims, serializer));
         jsonobject.add("unique_entity_types", this.uniqueEntityTypes.serializeToJson());
         return jsonobject;
      }
   }
}