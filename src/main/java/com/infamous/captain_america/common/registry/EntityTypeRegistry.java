package com.infamous.captain_america.common.registry;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.entity.RedwingEntity;
import com.infamous.captain_america.common.entity.VibraniumShieldEntity2;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityTypeRegistry {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, CaptainAmerica.MODID);

    public static final RegistryObject<EntityType<? extends RedwingEntity>> FALCON_REDWING = ENTITY_TYPES.register("falcon_redwing",
            () -> EntityType.Builder.<RedwingEntity>of(RedwingEntity::new, EntityClassification.CREATURE)
                    .sized(0.7F, 0.6F)
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

    public static final RegistryObject<EntityType<? extends VibraniumShieldEntity2>> CAPTAIN_AMERICA_SHIELD = ENTITY_TYPES.register("captain_america_shield",
            () -> EntityType.Builder.<VibraniumShieldEntity2>of(VibraniumShieldEntity2::new, EntityClassification.MISC)
                    .sized(1.0F, 0.25F)
                    .fireImmune()
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("captain_america_shield")
    );

    public static final RegistryObject<EntityType<? extends VibraniumShieldEntity2>> VIBRANIUM_SHIELD = ENTITY_TYPES.register("vibranium_shield",
            () -> EntityType.Builder.<VibraniumShieldEntity2>of(VibraniumShieldEntity2::new, EntityClassification.MISC)
                    .sized(1.0F, 0.25F)
                    .fireImmune()
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("vibranium_shield")
    );

    public static void register(IEventBus modBusEvent) {
        CaptainAmerica.LOGGER.info("Registering entity types!");
        ENTITY_TYPES.register(modBusEvent);
        CaptainAmerica.LOGGER.info("Finished registering entity types!");
    }
}
