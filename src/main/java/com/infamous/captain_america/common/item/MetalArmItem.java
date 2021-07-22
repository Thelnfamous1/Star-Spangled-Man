package com.infamous.captain_america.common.item;

import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.metal_arm.IMetalArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

import net.minecraft.world.item.Item.Properties;

public class MetalArmItem extends TieredItem {
    private static final Predicate<Item> METAL_ARM_PREDICATE = item -> item instanceof MetalArmItem;
    private final float attackDamageBaseline;
    private final float attackKnockback;

    public MetalArmItem(float attackDamageIn, float attackKnockback, INamedItemTier itemTier, Properties properties) {
        super(itemTier, properties);
        this.attackDamageBaseline = attackDamageIn + itemTier.getAttackDamageBonus();
        this.attackKnockback = attackKnockback;
    }

    @Override
    public INamedItemTier getTier() {
        return (INamedItemTier) super.getTier();
    }

    public float getAttackDamage() {
        return this.attackDamageBaseline;
    }

    public float getAttackKnockback(){
        return this.attackKnockback;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack metalArmStack = player.getItemInHand(hand);
        IMetalArm metalArmCap = CapabilityHelper.getMetalArmCap(player);
        if(metalArmCap != null){
            if(hand == InteractionHand.MAIN_HAND){
                player.setItemInHand(hand, metalArmCap.getMetalArmMainHand());
                metalArmCap.setMetalArmMainHand(metalArmStack);
            } else{
                player.setItemInHand(hand, metalArmCap.getMetalArmOffHand());
                metalArmCap.setMetalArmOffHand(metalArmStack);
            }
            return InteractionResultHolder.sidedSuccess(metalArmStack, world.isClientSide);
        } else{
            return super.use(world, player, hand);
        }
    }

    public static boolean isMetalArmStack(ItemStack stack){
        return METAL_ARM_PREDICATE.test(stack.getItem());
    }
}
