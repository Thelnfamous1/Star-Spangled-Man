package com.infamous.captain_america.common.item;

import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.metal_arm.IMetalArm;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.function.Predicate;

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
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack metalArmStack = player.getItemInHand(hand);
        IMetalArm metalArmCap = CapabilityHelper.getMetalArmCap(player);
        if(metalArmCap != null){
            if(hand == Hand.MAIN_HAND){
                player.setItemInHand(hand, metalArmCap.getMetalArmMainHand());
                metalArmCap.setMetalArmMainHand(metalArmStack);
            } else{
                player.setItemInHand(hand, metalArmCap.getMetalArmOffHand());
                metalArmCap.setMetalArmOffHand(metalArmStack);
            }
            return ActionResult.sidedSuccess(metalArmStack, world.isClientSide);
        } else{
            return super.use(world, player, hand);
        }
    }

    public static boolean isMetalArmStack(ItemStack stack){
        return METAL_ARM_PREDICATE.test(stack.getItem());
    }
}
