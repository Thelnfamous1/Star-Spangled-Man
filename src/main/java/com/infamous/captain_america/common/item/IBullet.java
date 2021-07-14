package com.infamous.captain_america.common.item;

import com.infamous.captain_america.common.entity.projectile.BulletEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IBullet {
	/**
	 * Creates a projectile and set its stats and stuff. The gun will give it velocity and spawn it in the world.
	 */
	BulletEntity createProjectile(World world, ItemStack stack, LivingEntity shooter);

}