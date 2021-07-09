package com.infamous.captain_america.common.registry;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.renderer.model.CARenderMaterial;
import com.infamous.captain_america.common.item.*;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegistry implements IRegistryManager<Item> {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CaptainAmerica.MODID);

    public static final RegistryObject<Item> CAPTAIN_AMERICA_HELMET = ITEMS.register(
            "captain_america_helmet", () ->
                    new CAArmorItem(CAArmorMaterial.CAPTAIN_AMERICA, EquipmentSlotType.HEAD,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );

    public static final RegistryObject<Item> CAPTAIN_AMERICA_CHESTPLATE = ITEMS.register(
            "captain_america_chestplate", () ->
                    new CAArmorItem(CAArmorMaterial.CAPTAIN_AMERICA, EquipmentSlotType.CHEST,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );

    public static final RegistryObject<Item> CAPTAIN_AMERICA_PANTS = ITEMS.register(
            "captain_america_pants", () ->
                    new CAArmorItem(CAArmorMaterial.CAPTAIN_AMERICA, EquipmentSlotType.LEGS,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );

    public static final RegistryObject<Item> CAPTAIN_AMERICA_BOOTS = ITEMS.register(
            "captain_america_boots", () ->
                    new CAArmorItem(CAArmorMaterial.CAPTAIN_AMERICA, EquipmentSlotType.FEET,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );

    public static final RegistryObject<Item> FALCON_GOGGLES = ITEMS.register(
            "falcon_goggles", () ->
                    new GogglesItem(CAArmorMaterial.FALCON_WINGSUIT,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );

    public static final RegistryObject<Item> FALCON_JACKET = ITEMS.register(
            "falcon_jacket", () ->
                    new CAArmorItem(CAArmorMaterial.FALCON_WINGSUIT, EquipmentSlotType.CHEST,
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
                    new CAArmorItem(CAArmorMaterial.FALCON_WINGSUIT, EquipmentSlotType.LEGS,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );

    public static final RegistryObject<Item> FALCON_GREAVES = ITEMS.register(
            "falcon_greaves", () ->
                    new CAArmorItem(CAArmorMaterial.FALCON_WINGSUIT, EquipmentSlotType.FEET,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );
    public static final RegistryObject<Item> CAPTAIN_AMERICA_GOGGLES = ITEMS.register(
            "captain_america_goggles", () ->
                    new GogglesItem(CAArmorMaterial.CAPTAIN_AMERICA_WINGSUIT,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );

    public static final RegistryObject<Item> CAPTAIN_AMERICA_JACKET = ITEMS.register(
            "captain_america_jacket", () ->
                    new CAArmorItem(CAArmorMaterial.CAPTAIN_AMERICA_WINGSUIT, EquipmentSlotType.CHEST,
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
                    new CAArmorItem(CAArmorMaterial.CAPTAIN_AMERICA_WINGSUIT, EquipmentSlotType.LEGS,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT))
    );

    public static final RegistryObject<Item> CAPTAIN_AMERICA_GREAVES = ITEMS.register(
            "captain_america_greaves", () ->
                    new CAArmorItem(CAArmorMaterial.CAPTAIN_AMERICA_WINGSUIT, EquipmentSlotType.FEET,
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
                                    .set(() -> CARenderMaterial::getCaptainAmericaShield))
    );

    public static final RegistryObject<Item> VIBRANIUM_SHIELD = ITEMS.register(
            "vibranium_shield", () ->
                    new VibraniumShieldItem(EntityTypeRegistry.VIBRANIUM_SHIELD,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT),
                            new CARenderMaterial()
                                    .set(() -> CARenderMaterial::getVibraniumShield))
    );

    public static final RegistryObject<Item> VIBRANIUM_ARM = ITEMS.register(
            "vibranium_arm", () ->
                    new MetalArmItem(
                            3.0F,
                            1.0F,
                            CAItemTier.VIBRANIUM,
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT)
                                    .stacksTo(1))
    );

    public static final RegistryObject<BulletItem> PISTOL_BULLET = ITEMS.register(
            "pistol_bullet", () ->
                    new BulletItem(
                            (new Item.Properties())
                                    .tab(ItemGroup.TAB_COMBAT)
                                    .stacksTo(1), 6)
    );

    @Override
    public String getRegistryTypeForLogger() {
        return "items";
    }

    @Override
    public DeferredRegister<Item> getDeferredRegister() {
        return ITEMS;
    }
}
