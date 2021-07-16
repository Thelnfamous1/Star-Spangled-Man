package com.infamous.captain_america.common.item;

import java.util.List;

import javax.annotation.Nullable;

import com.infamous.captain_america.common.entity.projectile.BulletEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BulletItem extends Item {
	private final int damage;

	public BulletItem(Properties properties, int damage) {
		super(properties);
		this.damage = damage;
	}

	public BulletEntity createBullet(World world, ItemStack stack, LivingEntity shooter) {
		BulletEntity entity = new BulletEntity(world, shooter);
		entity.setItem(stack);
		entity.setDamage(this.damage);
		return entity;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new TranslationTextComponent("tooltip.captain_america.bullet.damage", this.damage).withStyle(TextFormatting.DARK_GREEN));
	}

}