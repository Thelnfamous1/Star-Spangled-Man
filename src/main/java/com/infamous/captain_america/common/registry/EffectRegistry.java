package com.infamous.captain_america.common.registry;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.potion.SuperSoldierEffect;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EffectRegistry implements IRegistryManager<Effect> {

    private static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, CaptainAmerica.MODID);

    private static final double MOVE_SPEED_MODIFER_VALUE_SERUM = 1.0F;
    private static final double SWIM_SPEED_MODIFIER_VALUE_SERUM = 1.0F;
    private static final double FLY_SPEED_MODIFIER_VALUE_SERUM = 1.0F;
    private static final double HORSE_JUMP_STRENGTH_MODIFIER_VALUE_SERUM = 1.0F;
    private static final double ATTACK_DAMAGE_MODIFIER_VALUE_SERUM = 1.0F;
    private static final double ATTACK_SPEED_MODIFIER_VALUE_SERUM = 1.0F;
    private static final double ATTACK_KNOCKBACK_MODIFIER_VALUE_SERUM = 1.0F;
    private static final double MAX_HEALTH_MODIFIER_VALUE_SERUM = 1.0F;

    public static final RegistryObject<Effect> SUPER_SOLDIER = EFFECTS.register(
            "super_soldier", () ->
                    (new SuperSoldierEffect())
                            .addAttributeModifier(
                                    Attributes.MOVEMENT_SPEED,
                                    "e1a9a2b7-d5e2-4697-93b9-fd6da2f345ea",
                                    MOVE_SPEED_MODIFER_VALUE_SERUM,
                                    AttributeModifier.Operation.MULTIPLY_BASE)
                            .addAttributeModifier(
                                    ForgeMod.SWIM_SPEED.get(),
                                    "ec695eb6-30ac-44ad-ab65-f5caaf2a7cff",
                                    SWIM_SPEED_MODIFIER_VALUE_SERUM,
                                    AttributeModifier.Operation.MULTIPLY_BASE)
                            .addAttributeModifier(
                                    Attributes.FLYING_SPEED,
                                    "3424780b-3073-473c-a5e1-13a67446dfc8",
                                    FLY_SPEED_MODIFIER_VALUE_SERUM,
                                    AttributeModifier.Operation.MULTIPLY_BASE)
                            .addAttributeModifier(
                                    Attributes.JUMP_STRENGTH,
                                    "aef11618-e060-4f35-adac-b1c7ec39602a",
                                    HORSE_JUMP_STRENGTH_MODIFIER_VALUE_SERUM,
                                    AttributeModifier.Operation.MULTIPLY_BASE)
                            .addAttributeModifier(
                                    Attributes.ATTACK_DAMAGE,
                                    "68999399-0494-498b-aa49-07a855f71607",
                                    ATTACK_DAMAGE_MODIFIER_VALUE_SERUM,
                                    AttributeModifier.Operation.MULTIPLY_BASE)
                            .addAttributeModifier(
                                    Attributes.ATTACK_SPEED,
                                    "b115ddfd-7c9d-4b9d-b611-f257f46341ed",
                                    ATTACK_SPEED_MODIFIER_VALUE_SERUM,
                                    AttributeModifier.Operation.MULTIPLY_BASE)
                            .addAttributeModifier(
                                    Attributes.MAX_HEALTH,
                                    "43d1e5fd-e729-42e4-9d7a-cf1dbc87cbdb",
                                    MAX_HEALTH_MODIFIER_VALUE_SERUM,
                                    AttributeModifier.Operation.MULTIPLY_BASE)
                            .addAttributeModifier(
                                    Attributes.ATTACK_KNOCKBACK,
                                    "30efaa1a-6e18-48fb-b3e9-937d73ca04cc",
                                    ATTACK_KNOCKBACK_MODIFIER_VALUE_SERUM,
                                    AttributeModifier.Operation.ADDITION)
    );

    @Override
    public String getRegistryTypeForLogger() {
        return "effects";
    }

    @Override
    public DeferredRegister<Effect> getDeferredRegister() {
        return EFFECTS;
    }
}
