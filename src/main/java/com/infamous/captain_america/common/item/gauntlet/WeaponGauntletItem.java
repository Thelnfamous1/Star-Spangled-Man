package com.infamous.captain_america.common.item.gauntlet;

import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.util.CALogicHelper;
import com.infamous.captain_america.common.util.FalconAbilityKey;
import com.infamous.captain_america.common.util.FalconAbilityValue;
import com.infamous.captain_america.common.util.KeyBindAction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

import net.minecraft.world.item.Item.Properties;

public class WeaponGauntletItem extends AbstractGauntletItem {

    public WeaponGauntletItem(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResultHolder<ItemStack> useOnServer(Level world, Player player, InteractionHand hand, ItemStack heldItem) {
        IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(player);
        boolean canUseWeapon = canUseWeapon(player, hand);
        if(falconAbilityCap != null && canUseWeapon){
            FalconAbilityValue abilityValue = falconAbilityCap.get(FalconAbilityKey.COMBAT);
            abilityValue.getHandlerForKeyBindAction(KeyBindAction.INITIAL_PRESS).accept((ServerPlayer)player);
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(heldItem);
        } else{
            return InteractionResultHolder.pass(heldItem);
        }
    }

    @Override
    protected InteractionResultHolder<ItemStack> useOnClient(Level world, Player player, InteractionHand hand, ItemStack heldItem) {
        boolean canUseWeapon = canUseWeapon(player, hand);
        if(canUseWeapon){
            player.startUsingItem(hand);
        }
        return canUseWeapon ? InteractionResultHolder.consume(heldItem) : InteractionResultHolder.pass(heldItem);
    }

    private boolean canUseWeapon(Player player, InteractionHand hand) {
        return !player.isSecondaryUseActive()
                || hand == InteractionHand.OFF_HAND
                || !ControlGauntletItem.hasThisInOppositeHand(player, hand);
    }

    @Override
    protected void usingOnClient(Level useWorld, LivingEntity useEntity, ItemStack useItem, int useTicksLeft) {

    }

    @Override
    protected void usingOnServer(Level useWorld, LivingEntity useEntity, ItemStack useItem, int useTicksLeft) {
        IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(useEntity);
        if(falconAbilityCap != null && useEntity instanceof ServerPlayer){
            FalconAbilityValue abilityValue = falconAbilityCap.get(FalconAbilityKey.COMBAT);
            abilityValue.getHandlerForKeyBindAction(KeyBindAction.HELD).accept((ServerPlayer)useEntity);
        }
    }

    @Override
    protected void releaseOnClient(ItemStack usedItem, Level usedWorld, LivingEntity usedEntity, int useTicksLeft) {
    }

    @Override
    protected void releaseOnServer(ItemStack usedItem, Level usedWorld, LivingEntity usedEntity, int useTicksLeft) {
        IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(usedEntity);
        if(falconAbilityCap != null && usedEntity instanceof ServerPlayer){
            FalconAbilityValue abilityValue = falconAbilityCap.get(FalconAbilityKey.COMBAT);
            abilityValue.getHandlerForKeyBindAction(KeyBindAction.RELEASE).accept((ServerPlayer)usedEntity);
        }
    }

    @Override
    public int getUseDuration(ItemStack useItem) {
        return 72000;
    }

    public static boolean isHoldingThis(LivingEntity livingEntity){
        return livingEntity.isHolding(itemStack -> itemStack.getItem() instanceof WeaponGauntletItem);
    }

    public static boolean hasThisInOppositeHand(LivingEntity livingEntity, InteractionHand handIn){
        InteractionHand oppositeHand = CALogicHelper.getOppositeHand(handIn);
        Item itemInOppositeHand = livingEntity.getItemInHand(oppositeHand).getItem();
        return itemInOppositeHand instanceof WeaponGauntletItem;
    }

    public static boolean isStackOfThis(ItemStack stack){
        return stack.getItem() instanceof WeaponGauntletItem;
    }
}
