package com.infamous.captain_america.common.item.gauntlet;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

import net.minecraft.world.item.Item.Properties;

public abstract class AbstractGauntletItem extends Item {

    public AbstractGauntletItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        if(world.isClientSide){
            return this.useOnClient(world, player, hand, heldItem);
        } else{
            return this.useOnServer(world, player, hand, heldItem);
        }
    }

    protected abstract InteractionResultHolder<ItemStack> useOnServer(Level world, Player player, InteractionHand hand, ItemStack heldItem);

    protected abstract InteractionResultHolder<ItemStack> useOnClient(Level world, Player player, InteractionHand hand, ItemStack heldItem);

    @Override
    public void onUseTick(Level usingWorld, LivingEntity usingEntity, ItemStack usingItem, int useTicksLeft) {
        if(usingWorld.isClientSide){
            this.usingOnClient(usingWorld, usingEntity, usingItem, useTicksLeft);
        } else{
            this.usingOnServer(usingWorld, usingEntity, usingItem, useTicksLeft);
        }
    }

    protected abstract void usingOnClient(Level useWorld, LivingEntity useEntity, ItemStack useItem, int useTicksLeft);

    protected abstract void usingOnServer(Level useWorld, LivingEntity useEntity, ItemStack useItem, int useTicksLeft);

    @Override
    public void releaseUsing(ItemStack usedItem, Level usedWorld, LivingEntity usedEntity, int useTicksLeft) {
        if(usedWorld.isClientSide){
            this.releaseOnClient(usedItem, usedWorld, usedEntity, useTicksLeft);
        } else{
            this.releaseOnServer(usedItem, usedWorld, usedEntity, useTicksLeft);
        }
    }

    protected abstract void releaseOnClient(ItemStack usedItem, Level usedWorld, LivingEntity usedEntity, int useTicksLeft);

    protected abstract void releaseOnServer(ItemStack usedItem, Level usedWorld, LivingEntity usedEntity, int useTicksLeft);

    public static boolean isStackOfThis(ItemStack stack){
        return stack.getItem() instanceof AbstractGauntletItem;
    }
    public static boolean isHoldingThisInBothHands(LivingEntity living){
        return isStackOfThis(living.getMainHandItem()) && isStackOfThis(living.getOffhandItem());
    }
}
