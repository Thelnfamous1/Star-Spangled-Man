package com.infamous.captain_america.common.registry;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.item.CAArmorMaterial;
import com.infamous.captain_america.common.item.EXO7FalconItem;
import com.infamous.captain_america.common.item.VibraniumShieldItem;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegistry{

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CaptainAmerica.MODID);

    public static final RegistryObject<Item> FALCON_GOGGLES = ITEMS.register(
            "falcon_goggles", () ->
                    new ArmorItem(CAArmorMaterial.FALCON_WINGSUIT, EquipmentSlotType.HEAD,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );

    public static final RegistryObject<Item> FALCON_WINGSUIT = ITEMS.register(
            "falcon_wingsuit", () ->
                    new EXO7FalconItem(CAArmorMaterial.FALCON_WINGSUIT, EquipmentSlotType.CHEST,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
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

    public static final RegistryObject<Item> CAPTAIN_AMERICA_WINGSUIT = ITEMS.register(
            "captain_america_wingsuit", () ->
                    new EXO7FalconItem(CAArmorMaterial.CAPTAIN_AMERICA_WINGSUIT, EquipmentSlotType.CHEST,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
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

    public static final RegistryObject<Item> CAPTAIN_AMERICA_SHIELD = ITEMS.register(
            "captain_america_shield", () ->
                    new VibraniumShieldItem(
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );

    public static final RegistryObject<Item> VIBRANIUM_SHIELD = ITEMS.register(
            "vibranium_shield", () ->
                    new VibraniumShieldItem(
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );

    public static void register(IEventBus modBusEvent) {
        CaptainAmerica.LOGGER.info("Registering items!");
        ITEMS.register(modBusEvent);
        CaptainAmerica.LOGGER.info("Finished registering items!");
    }
}
