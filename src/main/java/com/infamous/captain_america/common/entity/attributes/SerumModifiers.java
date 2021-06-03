package com.infamous.captain_america.common.entity.attributes;

import net.minecraft.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class SerumModifiers {

    public static final UUID ATTACK_DAMAGE_MODIFIER_SERUM_UUID = UUID.fromString("68999399-0494-498b-aa49-07a855f71607");
    public static final UUID ATTACK_SPEED_MODIFIER_SERUM_UUID = UUID.fromString("b115ddfd-7c9d-4b9d-b611-f257f46341ed");
    public static final UUID MOVE_SPEED_MODIFIER_SERUM_UUID = UUID.fromString("e1a9a2b7-d5e2-4697-93b9-fd6da2f345ea");
    public static final UUID SWIM_SPEED_MODIFIER_SERUM_UUID = UUID.fromString("ec695eb6-30ac-44ad-ab65-f5caaf2a7cff");
    public static final UUID MAX_HEALTH_MODIFIER_SERUM_UUID = UUID.fromString("43d1e5fd-e729-42e4-9d7a-cf1dbc87cbdb");

    public static final AttributeModifier ATTACK_DAMAGE_MODIFIER_SERUM = new AttributeModifier(ATTACK_DAMAGE_MODIFIER_SERUM_UUID, "Super soldier attack damage boost", (double)5.0F, AttributeModifier.Operation.ADDITION);
    public static final AttributeModifier ATTACK_SPEED_MODIFIER_SERUM = new AttributeModifier(ATTACK_SPEED_MODIFIER_SERUM_UUID, "Super soldier attack speed boost", (double)(1.6F - 4.0F), AttributeModifier.Operation.ADDITION);
    public static final int SPEED_LEVEL = 1;
    public static final AttributeModifier MOVE_SPEED_MODIFIER_SERUM = new AttributeModifier(MOVE_SPEED_MODIFIER_SERUM_UUID, "Super soldier move speed boost", (double)(0.03F * (1.0F + (float) SPEED_LEVEL * 0.35F)), AttributeModifier.Operation.ADDITION);
    public static final AttributeModifier SWIM_SPEED_MODIFIER_SERUM = new AttributeModifier(SWIM_SPEED_MODIFIER_SERUM_UUID, "Super soldier swim speed boost", (double)(0.03F * (1.0F + (float) SPEED_LEVEL * 0.35F)), AttributeModifier.Operation.ADDITION);
    public static final AttributeModifier MAX_HEALTH_MODIFIER_SERUM = new AttributeModifier(MAX_HEALTH_MODIFIER_SERUM_UUID, "Super soldier max health boost", (double)20.0F, AttributeModifier.Operation.ADDITION);


}
