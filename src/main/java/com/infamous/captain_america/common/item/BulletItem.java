package com.infamous.captain_america.common.item;

import java.util.List;

import javax.annotation.Nullable;

import com.infamous.captain_america.common.entity.projectile.BulletEntity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.world.item.Item.Properties;

public class BulletItem extends Item {
	private final int damage;

	public BulletItem(Properties properties, int damage) {
		super(properties);
		this.damage = damage;
	}

	public BulletEntity createBullet(Level world, ItemStack stack, LivingEntity shooter) {
		BulletEntity entity = new BulletEntity(world, shooter);
		entity.setItem(stack);
		entity.setDamage(this.damage);
		return entity;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		tooltip.add(new TranslatableComponent("tooltip.captain_america.bullet.damage", this.damage).withStyle(ChatFormatting.DARK_GREEN));
	}

}