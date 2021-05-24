package com.infamous.captain_america.common.util;

import com.infamous.captain_america.common.entity.VibraniumShieldEntity;
import com.infamous.captain_america.common.item.VibraniumShieldItem;
import com.infamous.captain_america.common.registry.EntityTypeRegistry;
import com.infamous.captain_america.common.registry.ItemRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.function.Predicate;

public class VibraniumShieldHelper {
    public static final Predicate<Item> SHIELD_PREDICATE = item -> item instanceof VibraniumShieldItem;

    public static Hand getShieldHoldingHand(LivingEntity living) {
        return SHIELD_PREDICATE.test(living.getMainHandItem().getItem()) ? Hand.MAIN_HAND : Hand.OFF_HAND;
    }

    public static EntityType<VibraniumShieldEntity> getShieldType(VibraniumShieldItem item){
        if(item == ItemRegistry.CAPTAIN_AMERICA_SHIELD.get()){
            return EntityTypeRegistry.CAPTAIN_AMERICA_SHIELD.get();
        }
        return EntityTypeRegistry.VIBRANIUM_SHIELD.get();
    }

    public static boolean throwShield(LivingEntity thrower){
        Hand shieldHoldingHand = getShieldHoldingHand(thrower);
        ItemStack stack = thrower.getItemInHand(shieldHoldingHand);
        Item item = stack.getItem();
        if(!thrower.level.isClientSide && item instanceof VibraniumShieldItem){
            EntityType<VibraniumShieldEntity> shieldEntityType = getShieldType(((VibraniumShieldItem)item));
            VibraniumShieldEntity vibraniumShield = new VibraniumShieldEntity(shieldEntityType, thrower, thrower.level);

            Vector3d vector3d1 = thrower.getUpVector(1.0F);
            float offset = 0.0F;
            Quaternion quaternion = new Quaternion(new Vector3f(vector3d1), offset, true);
            Vector3d vector3d = thrower.getViewVector(1.0F);
            Vector3f vector3f = new Vector3f(vector3d);
            vector3f.transform(quaternion);
            vibraniumShield.shoot((double)vector3f.x(), (double)vector3f.y(), (double)vector3f.z(), 1.6F, offset);

            boolean thrown = thrower.level.addFreshEntity(vibraniumShield);
            if(thrown){
                ItemStack singletonShield = stack.split(1);
                vibraniumShield.setShieldStack(singletonShield);
                return true;
            }
        }
        return false;
    }

    public static boolean hasVibraniumShield(LivingEntity living) {
        return living.isHolding(SHIELD_PREDICATE);
    }
}
