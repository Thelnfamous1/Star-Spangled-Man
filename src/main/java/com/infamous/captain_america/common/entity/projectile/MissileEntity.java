package com.infamous.captain_america.common.entity.projectile;

import com.infamous.captain_america.common.registry.EntityTypeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MissileEntity extends ExplosiveProjectileEntity {

    public MissileEntity(EntityType<? extends MissileEntity> entityType, Level world) {
        super(entityType, world);
    }

    public MissileEntity(LivingEntity shooter, Level world) {
        super(EntityTypeRegistry.MISSILE.get(), shooter, world);
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected float getInertia() {
        return 1;
    }

    @Override
    protected float getGravity() {
        return 0;
    }

    @Override
    protected float getWaterInertia() {
        return 1;
    }
}
