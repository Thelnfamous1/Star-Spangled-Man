package com.infamous.captain_america.common.item.gauntlet;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.screen.FalconAbilitySelectionScreen;
import com.infamous.captain_america.common.util.CALogicHelper;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ControlGauntletItem extends AbstractGauntletItem {

    public ControlGauntletItem(Properties properties) {
        super(properties);
    }

    @Override
    protected ActionResult<ItemStack> useOnServer(World world, PlayerEntity player, Hand hand, ItemStack heldItem) {
        boolean canUseControl = canUseControl(player, hand);
        return canUseControl ? ActionResult.consume(heldItem) : ActionResult.pass(heldItem);
    }

    @Override
    protected ActionResult<ItemStack> useOnClient(World world, PlayerEntity player, Hand hand, ItemStack heldItem) {
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        boolean canUseControl = canUseControl(player, hand);
        if(minecraft.screen == null
                && minecraft.level != null
                && !player.isDeadOrDying()
                && FalconFlightHelper.hasEXO7Falcon(player)
                && canUseControl){
            //CaptainAmerica.LOGGER.info("Opening falcon screen for client player {}!", player.getDisplayName().getString());
            minecraft.setScreen(new FalconAbilitySelectionScreen());
            return ActionResult.consume(heldItem);
        } else{
            return ActionResult.pass(heldItem);
        }
    }

    private boolean canUseControl(PlayerEntity player, Hand hand) {
        return !player.isSecondaryUseActive()
                || hand == Hand.OFF_HAND
                || !WeaponGauntletItem.hasThisInOppositeHand(player, hand);
    }

    @Override
    protected void usingOnClient(World useWorld, LivingEntity useEntity, ItemStack useItem, int useTicksLeft) {

    }

    @Override
    protected void usingOnServer(World useWorld, LivingEntity useEntity, ItemStack useItem, int useTicksLeft) {

    }

    @Override
    protected void releaseOnClient(ItemStack usedItem, World usedWorld, LivingEntity usedEntity, int useTicksLeft) {
    }

    @Override
    protected void releaseOnServer(ItemStack usedItem, World usedWorld, LivingEntity usedEntity, int useTicksLeft) {
    }

    public static boolean hasThisInOppositeHand(LivingEntity livingEntity, Hand handIn){
        Hand oppositeHand = CALogicHelper.getOppositeHand(handIn);
        Item itemInOppositeHand = livingEntity.getItemInHand(oppositeHand).getItem();
        return itemInOppositeHand instanceof ControlGauntletItem;
    }

    public static boolean isHoldingThis(LivingEntity livingEntity){
        return livingEntity.isHolding(item -> item instanceof ControlGauntletItem);
    }

    public static boolean isStackOfThis(ItemStack stack){
        return stack.getItem() instanceof ControlGauntletItem;
    }
}
