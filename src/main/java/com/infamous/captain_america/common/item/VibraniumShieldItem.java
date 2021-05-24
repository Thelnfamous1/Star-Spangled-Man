package com.infamous.captain_america.common.item;

import com.infamous.captain_america.client.renderer.VibraniumShieldISTER;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;

public class VibraniumShieldItem extends ShieldItem {

    public VibraniumShieldItem(Properties properties) {
        super(properties
                .stacksTo(1)
                .setISTER(VibraniumShieldItem::getISTER));
    }

    @Override
    public boolean isValidRepairItem(ItemStack p_82789_1_, ItemStack p_82789_2_) {
        return false;
    }

    private static Callable<ItemStackTileEntityRenderer> getISTER() {
        return VibraniumShieldISTER::new;
    }

    @Override
    public boolean isShield(ItemStack stack, @Nullable LivingEntity entity) {
        return stack.getItem() instanceof VibraniumShieldItem;
    }
}
