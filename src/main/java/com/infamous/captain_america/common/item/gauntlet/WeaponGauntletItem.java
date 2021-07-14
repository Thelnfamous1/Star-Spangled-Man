package com.infamous.captain_america.common.item.gauntlet;

import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.util.CALogicHelper;
import com.infamous.captain_america.common.util.FalconAbilityKey;
import com.infamous.captain_america.common.util.FalconAbilityValue;
import com.infamous.captain_america.common.util.KeyBindAction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class WeaponGauntletItem extends AbstractGauntletItem {

    public WeaponGauntletItem(Properties properties) {
        super(properties);
    }

    @Override
    protected ActionResult<ItemStack> useOnServer(World world, PlayerEntity player, Hand hand, ItemStack heldItem) {
        IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(player);
        boolean canUseWeapon = canUseWeapon(player, hand);
        if(falconAbilityCap != null && canUseWeapon){
            FalconAbilityValue abilityValue = falconAbilityCap.get(FalconAbilityKey.COMBAT);
            abilityValue.getHandlerForKeyBindAction(KeyBindAction.INITIAL_PRESS).accept((ServerPlayerEntity)player);
            player.startUsingItem(hand);
            return ActionResult.consume(heldItem);
        } else{
            return ActionResult.pass(heldItem);
        }
    }

    @Override
    protected ActionResult<ItemStack> useOnClient(World world, PlayerEntity player, Hand hand, ItemStack heldItem) {
        boolean canUseWeapon = canUseWeapon(player, hand);
        if(canUseWeapon){
            player.startUsingItem(hand);
        }
        return canUseWeapon ? ActionResult.consume(heldItem) : ActionResult.pass(heldItem);
    }

    private boolean canUseWeapon(PlayerEntity player, Hand hand) {
        return !player.isSecondaryUseActive() || (hand == Hand.MAIN_HAND && !ControlGauntletItem.hasThisInOppositeHand(player, hand));
    }

    @Override
    protected void usingOnClient(World useWorld, LivingEntity useEntity, ItemStack useItem, int useTicksLeft) {

    }

    @Override
    protected void usingOnServer(World useWorld, LivingEntity useEntity, ItemStack useItem, int useTicksLeft) {
        IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(useEntity);
        if(falconAbilityCap != null && useEntity instanceof ServerPlayerEntity){
            FalconAbilityValue abilityValue = falconAbilityCap.get(FalconAbilityKey.COMBAT);
            abilityValue.getHandlerForKeyBindAction(KeyBindAction.HELD).accept((ServerPlayerEntity)useEntity);
        }
    }

    @Override
    protected void releaseOnClient(ItemStack usedItem, World usedWorld, LivingEntity usedEntity, int useTicksLeft) {
    }

    @Override
    protected void releaseOnServer(ItemStack usedItem, World usedWorld, LivingEntity usedEntity, int useTicksLeft) {
        IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(usedEntity);
        if(falconAbilityCap != null && usedEntity instanceof ServerPlayerEntity){
            FalconAbilityValue abilityValue = falconAbilityCap.get(FalconAbilityKey.COMBAT);
            abilityValue.getHandlerForKeyBindAction(KeyBindAction.RELEASE).accept((ServerPlayerEntity)usedEntity);
        }
    }

    @Override
    public int getUseDuration(ItemStack useItem) {
        return 72000;
    }

    public static boolean isHoldingThis(LivingEntity livingEntity){
        return livingEntity.isHolding(item -> item instanceof WeaponGauntletItem);
    }

    public static boolean hasThisInOppositeHand(LivingEntity livingEntity, Hand handIn){
        Hand oppositeHand = CALogicHelper.getOppositeHand(handIn);
        Item itemInOppositeHand = livingEntity.getItemInHand(oppositeHand).getItem();
        return itemInOppositeHand instanceof WeaponGauntletItem;
    }
}
