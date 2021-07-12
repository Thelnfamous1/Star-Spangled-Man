package com.infamous.captain_america.common.registry;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.capability.falcon_ability.FalconAbility;
import com.infamous.captain_america.common.entity.drone.RedwingEntity;
import com.infamous.captain_america.common.entity.projectile.BulletEntity;
import com.infamous.captain_america.common.entity.projectile.MissileEntity;
import com.infamous.captain_america.common.entity.projectile.TimedGrenadeEntity;
import com.infamous.captain_america.common.entity.projectile.VibraniumShieldEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
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
                    .build(new ResourceLocation(CaptainAmerica.MODID, "falcon_redwing").toString())
    );

    public static final RegistryObject<EntityType<? extends RedwingEntity>> CAPTAIN_AMERICA_REDWING = ENTITY_TYPES.register("captain_america_redwing",
            () -> EntityType.Builder.<RedwingEntity>of(RedwingEntity::new, EntityClassification.CREATURE)
                    .sized(0.7F, 0.6F)
                    .fireImmune()
                    .clientTrackingRange(8)
                    .build(new ResourceLocation(CaptainAmerica.MODID, "captain_america_redwing").toString())
    );

    public static final RegistryObject<EntityType<? extends VibraniumShieldEntity>> CAPTAIN_AMERICA_SHIELD = ENTITY_TYPES.register("captain_america_shield",
            () -> EntityType.Builder.<VibraniumShieldEntity>of(VibraniumShieldEntity::new, EntityClassification.MISC)
                    .sized(1.0F, 0.25F)
                    .fireImmune()
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(new ResourceLocation(CaptainAmerica.MODID, "captain_america_shield").toString())
    );

    public static final RegistryObject<EntityType<? extends VibraniumShieldEntity>> VIBRANIUM_SHIELD = ENTITY_TYPES.register("vibranium_shield",
            () -> EntityType.Builder.<VibraniumShieldEntity>of(VibraniumShieldEntity::new, EntityClassification.MISC)
                    .sized(1.0F, 0.25F)
                    .fireImmune()
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(new ResourceLocation(CaptainAmerica.MODID, "vibranium_shield").toString())
    );

    public static final RegistryObject<EntityType<BulletEntity>> BULLET = ENTITY_TYPES.register("bullet",
            () -> EntityType.Builder.<BulletEntity>of(BulletEntity::new, EntityClassification.MISC)
            .sized(0.3125f, 0.3125f)
            .setUpdateInterval(10)
            .setTrackingRange(64)
            .setShouldReceiveVelocityUpdates(true)
            .build(new ResourceLocation(CaptainAmerica.MODID, "bullet").toString()));

    public static final RegistryObject<EntityType<MissileEntity>> MISSILE = ENTITY_TYPES.register("missile",
            () -> EntityType.Builder.<MissileEntity>of(MissileEntity::new, EntityClassification.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build(new ResourceLocation(CaptainAmerica.MODID, "missile").toString()));

    public static final RegistryObject<EntityType<TimedGrenadeEntity>> TIMED_GRENADE = ENTITY_TYPES.register("timed_grenade",
            () -> EntityType.Builder.<TimedGrenadeEntity>of(TimedGrenadeEntity::new, EntityClassification.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build(new ResourceLocation(CaptainAmerica.MODID, "timed_grenade").toString()));


    @Override
    public String getRegistryTypeForLogger() {
        return "entity types";
    }

    @Override
    public DeferredRegister<EntityType<?>> getDeferredRegister() {
        return ENTITY_TYPES;
    }
}
