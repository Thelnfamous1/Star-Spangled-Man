package com.infamous.captain_america.common.registry;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.entity.drone.RedwingEntity;
import com.infamous.captain_america.common.entity.projectile.VibraniumShieldEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityTypeRegistry implements IRegistryManager<EntityType<?>> {

    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, CaptainAmerica.MODID);

    public static final RegistryObject<EntityType<? extends RedwingEntity>> FALCON_REDWING = ENTITY_TYPES.register("falcon_redwing",
            () -> EntityType.Builder.<RedwingEntity>of(RedwingEntity::new, EntityClassification.CREATURE)
                    .sized(0.7F, 0.6F)
                    .fireImmune()
                    .clientTrackingRange(8)
                    .build("falcon_redwing")
    );

    public static final RegistryObject<EntityType<? extends RedwingEntity>> CAPTAIN_AMERICA_REDWING = ENTITY_TYPES.register("captain_america_redwing",
            () -> EntityType.Builder.<RedwingEntity>of(RedwingEntity::new, EntityClassification.CREATURE)
                    .sized(0.7F, 0.6F)
                    .fireImmune()
                    .clientTrackingRange(8)
                    .build("captain_america_redwing")
    );

    public static final RegistryObject<EntityType<? extends VibraniumShieldEntity>> CAPTAIN_AMERICA_SHIELD = ENTITY_TYPES.register("captain_america_shield",
            () -> EntityType.Builder.<VibraniumShieldEntity>of(VibraniumShieldEntity::new, EntityClassification.MISC)
                    .sized(1.0F, 0.25F)
                    .fireImmune()
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("captain_america_shield")
    );

    public static final RegistryObject<EntityType<? extends VibraniumShieldEntity>> VIBRANIUM_SHIELD = ENTITY_TYPES.register("vibranium_shield",
            () -> EntityType.Builder.<VibraniumShieldEntity>of(VibraniumShieldEntity::new, EntityClassification.MISC)
                    .sized(1.0F, 0.25F)
                    .fireImmune()
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("vibranium_shield")
    );

    @Override
    public String getRegistryTypeForLogger() {
        return "entity types";
    }

    @Override
    public DeferredRegister<EntityType<?>> getDeferredRegister() {
        return ENTITY_TYPES;
    }
}
