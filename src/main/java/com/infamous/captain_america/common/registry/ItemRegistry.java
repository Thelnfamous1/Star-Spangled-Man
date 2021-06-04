package com.infamous.captain_america.common.registry;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.renderer.model.CARenderMaterial;
import com.infamous.captain_america.common.item.CAArmorMaterial;
import com.infamous.captain_america.common.item.EXO7FalconItem;
import com.infamous.captain_america.common.item.VibraniumShieldItem;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegistry{

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CaptainAmerica.MODID);

    public static final RegistryObject<Item> CAPTAIN_AMERICA_HELMET = ITEMS.register(
            "captain_america_helmet", () ->
                    new ArmorItem(ArmorMaterial.DIAMOND, EquipmentSlotType.HEAD,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );

    public static final RegistryObject<Item> CAPTAIN_AMERICA_CHESTPLATE = ITEMS.register(
            "captain_america_chestplate", () ->
                    new ArmorItem(ArmorMaterial.DIAMOND, EquipmentSlotType.CHEST,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );

    public static final RegistryObject<Item> CAPTAIN_AMERICA_PANTS = ITEMS.register(
            "captain_america_pants", () ->
                    new ArmorItem(ArmorMaterial.DIAMOND, EquipmentSlotType.LEGS,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );

    public static final RegistryObject<Item> CAPTAIN_AMERICA_BOOTS = ITEMS.register(
            "captain_america_boots", () ->
                    new ArmorItem(ArmorMaterial.DIAMOND, EquipmentSlotType.FEET,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );

    public static final RegistryObject<Item> FALCON_GOGGLES = ITEMS.register(
            "falcon_goggles", () ->
                    new ArmorItem(CAArmorMaterial.FALCON_WINGSUIT, EquipmentSlotType.HEAD,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );

    public static final RegistryObject<Item> FALCON_JACKET = ITEMS.register(
            "falcon_jacket", () ->
                    new ArmorItem(CAArmorMaterial.FALCON_WINGSUIT, EquipmentSlotType.CHEST,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );

    public static final RegistryObject<Item> FALCON_WINGSUIT = ITEMS.register(
            "falcon_wingsuit", () ->
                    new EXO7FalconItem(EntityTypeRegistry.FALCON_REDWING,
                            CAArmorMaterial.FALCON_WINGSUIT,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_TRANSPORTATION))
    );

    public static final RegistryObject<Item> FALCON_LEGGINGS = ITEMS.register(
            "falcon_leggings", () ->
                    new ArmorItem(CAArmorMaterial.FALCON_WINGSUIT, EquipmentSlotType.LEGS,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );

    public static final RegistryObject<Item> FALCON_GREAVES = ITEMS.register(
            "falcon_greaves", () ->
                    new ArmorItem(CAArmorMaterial.FALCON_WINGSUIT, EquipmentSlotType.FEET,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );
    public static final RegistryObject<Item> CAPTAIN_AMERICA_GOGGLES = ITEMS.register(
            "captain_america_goggles", () ->
                    new ArmorItem(CAArmorMaterial.CAPTAIN_AMERICA_WINGSUIT, EquipmentSlotType.HEAD,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );

    public static final RegistryObject<Item> CAPTAIN_AMERICA_JACKET = ITEMS.register(
            "captain_america_jacket", () ->
                    new ArmorItem(CAArmorMaterial.CAPTAIN_AMERICA_WINGSUIT, EquipmentSlotType.CHEST,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );

    public static final RegistryObject<Item> CAPTAIN_AMERICA_WINGSUIT = ITEMS.register(
            "captain_america_wingsuit", () ->
                    new EXO7FalconItem(EntityTypeRegistry.FALCON_REDWING,
                            CAArmorMaterial.CAPTAIN_AMERICA_WINGSUIT,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_TRANSPORTATION))
    );

    public static final RegistryObject<Item> CAPTAIN_AMERICA_LEGGINGS = ITEMS.register(
            "captain_america_leggings", () ->
                    new ArmorItem(CAArmorMaterial.CAPTAIN_AMERICA_WINGSUIT, EquipmentSlotType.LEGS,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );

    public static final RegistryObject<Item> CAPTAIN_AMERICA_GREAVES = ITEMS.register(
            "captain_america_greaves", () ->
                    new ArmorItem(CAArmorMaterial.CAPTAIN_AMERICA_WINGSUIT, EquipmentSlotType.FEET,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );

    public static final RegistryObject<Item> WINGPACK = ITEMS.register(
            "wingpack", () ->
                    new Item(
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_TRANSPORTATION))
    );

    public static final RegistryObject<Item> VIBRANIUM_WINGPACK = ITEMS.register(
            "vibranium_wingpack", () ->
                    new Item(
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_TRANSPORTATION))
    );

    public static final RegistryObject<Item> CAPTAIN_AMERICA_SHIELD = ITEMS.register(
            "captain_america_shield", () ->
                    new VibraniumShieldItem(EntityTypeRegistry.CAPTAIN_AMERICA_SHIELD,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT),
                            new CARenderMaterial()
                                    .set(CARenderMaterial::getCaptainAmericaShieldCallable))
    );

    public static final RegistryObject<Item> VIBRANIUM_SHIELD = ITEMS.register(
            "vibranium_shield", () ->
                    new VibraniumShieldItem(EntityTypeRegistry.VIBRANIUM_SHIELD,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT),
                            new CARenderMaterial()
                                    .set(CARenderMaterial::getVibraniumShieldCallable))
    );

    public static void register(IEventBus modBusEvent) {
        CaptainAmerica.LOGGER.info("Registering items!");
        ITEMS.register(modBusEvent);
        CaptainAmerica.LOGGER.info("Finished registering items!");
    }
}
