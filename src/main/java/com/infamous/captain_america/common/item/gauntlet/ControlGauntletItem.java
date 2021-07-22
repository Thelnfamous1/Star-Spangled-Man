package com.infamous.captain_america.common.item.gauntlet;

import com.infamous.captain_america.client.screen.FalconAbilitySelectionScreen;
import com.infamous.captain_america.common.util.CALogicHelper;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ControlGauntletItem extends AbstractGauntletItem {

    public ControlGauntletItem(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResultHolder<ItemStack> useOnServer(Level world, Player player, InteractionHand hand, ItemStack heldItem) {
        boolean canUseControl = canUseControl(player, hand);
        return canUseControl ? InteractionResultHolder.consume(heldItem) : InteractionResultHolder.pass(heldItem);
    }

    @Override
    protected InteractionResultHolder<ItemStack> useOnClient(Level world, Player player, InteractionHand hand, ItemStack heldItem) {
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        boolean canUseControl = canUseControl(player, hand);
        if(minecraft.screen == null
                && minecraft.level != null
                && !player.isDeadOrDying()
                && FalconFlightHelper.hasEXO7Falcon(player)
                && canUseControl){
            //CaptainAmerica.LOGGER.info("Opening falcon screen for client player {}!", player.getDisplayName().getString());
            minecraft.setScreen(new FalconAbilitySelectionScreen());
            return InteractionResultHolder.consume(heldItem);
        } else{
            return InteractionResultHolder.pass(heldItem);
        }
    }

    private boolean canUseControl(Player player, InteractionHand hand) {
        return !player.isSecondaryUseActive()
                || hand == InteractionHand.OFF_HAND
                || !WeaponGauntletItem.hasThisInOppositeHand(player, hand);
    }

    @Override
    protected void usingOnClient(Level useWorld, LivingEntity useEntity, ItemStack useItem, int useTicksLeft) {

    }

    @Override
    protected void usingOnServer(Level useWorld, LivingEntity useEntity, ItemStack useItem, int useTicksLeft) {

    }

    @Override
    protected void releaseOnClient(ItemStack usedItem, Level usedWorld, LivingEntity usedEntity, int useTicksLeft) {
    }

    @Override
    protected void releaseOnServer(ItemStack usedItem, Level usedWorld, LivingEntity usedEntity, int useTicksLeft) {
    }

    public static boolean hasThisInOppositeHand(LivingEntity livingEntity, InteractionHand handIn){
        InteractionHand oppositeHand = CALogicHelper.getOppositeHand(handIn);
        Item itemInOppositeHand = livingEntity.getItemInHand(oppositeHand).getItem();
        return itemInOppositeHand instanceof ControlGauntletItem;
    }

    public static boolean isHoldingThis(LivingEntity livingEntity){
        return livingEntity.isHolding(itemStack -> itemStack.getItem() instanceof ControlGauntletItem);
    }

    public static boolean isStackOfThis(ItemStack stack){
        return stack.getItem() instanceof ControlGauntletItem;
    }
}
