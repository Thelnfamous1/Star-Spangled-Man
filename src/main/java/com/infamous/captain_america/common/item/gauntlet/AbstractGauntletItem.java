package com.infamous.captain_america.common.item.gauntlet;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public abstract class AbstractGauntletItem extends Item {

    public AbstractGauntletItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        if(world.isClientSide){
            return this.useOnClient(world, player, hand, heldItem);
        } else{
            return this.useOnServer(world, player, hand, heldItem);
        }
    }

    protected abstract ActionResult<ItemStack> useOnServer(World world, PlayerEntity player, Hand hand, ItemStack heldItem);

    protected abstract ActionResult<ItemStack> useOnClient(World world, PlayerEntity player, Hand hand, ItemStack heldItem);

    @Override
    public void onUseTick(World usingWorld, LivingEntity usingEntity, ItemStack usingItem, int useTicksLeft) {
        if(usingWorld.isClientSide){
            this.usingOnClient(usingWorld, usingEntity, usingItem, useTicksLeft);
        } else{
            this.usingOnServer(usingWorld, usingEntity, usingItem, useTicksLeft);
        }
    }

    protected abstract void usingOnClient(World useWorld, LivingEntity useEntity, ItemStack useItem, int useTicksLeft);

    protected abstract void usingOnServer(World useWorld, LivingEntity useEntity, ItemStack useItem, int useTicksLeft);

    @Override
    public void releaseUsing(ItemStack usedItem, World usedWorld, LivingEntity usedEntity, int useTicksLeft) {
        if(usedWorld.isClientSide){
            this.releaseOnClient(usedItem, usedWorld, usedEntity, useTicksLeft);
        } else{
            this.releaseOnServer(usedItem, usedWorld, usedEntity, useTicksLeft);
        }
    }

    protected abstract void releaseOnClient(ItemStack usedItem, World usedWorld, LivingEntity usedEntity, int useTicksLeft);

    protected abstract void releaseOnServer(ItemStack usedItem, World usedWorld, LivingEntity usedEntity, int useTicksLeft);

}
