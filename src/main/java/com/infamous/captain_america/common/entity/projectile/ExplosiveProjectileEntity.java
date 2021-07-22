package com.infamous.captain_america.common.entity.projectile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class ExplosiveProjectileEntity extends CAProjectileEntity{
    protected float explosionStrength = 1.0F;
    protected int explosionRadius = 3;
    protected Explosion.BlockInteraction explosion$mode = Explosion.BlockInteraction.BREAK;
    protected boolean dud;

    protected ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> entityType, Level world){
        super(entityType, world);
    }

    protected ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> entityType, LivingEntity shooter, Level world) {
        super(entityType, shooter, world);
    }

    public void addAdditionalSaveData(CompoundTag compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);

        compoundNBT.putByte("ExplosionRadius", (byte)this.explosionRadius);
        compoundNBT.putFloat("ExplosionStrength", this.explosionStrength);
        compoundNBT.putByte("ExplosionMode", (byte)this.explosion$mode.ordinal());
        compoundNBT.putBoolean("Dud", this.dud);
    }

    public void readAdditionalSaveData(CompoundTag compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);

        if (compoundNBT.contains("ExplosionRadius", 99)) {
            this.explosionRadius = compoundNBT.getByte("ExplosionRadius");
        }
        if(compoundNBT.contains("ExplosionStrength", 99)){
            this.explosionStrength = compoundNBT.getByte("ExplosionStrength");
        }
        if (compoundNBT.contains("ExplosionMode", 99)) {
            this.explosion$mode = explosionModeByOrdinal(compoundNBT.getByte("ExplosionMode"));
        }
        this.dud = compoundNBT.getBoolean("Dud");
    }

    @Override
    protected void onHitBlock(BlockHitResult blockRTR) {
        super.onHitBlock(blockRTR);
        this.postHitBlock(blockRTR);
    }

    protected void postHitBlock(BlockHitResult blockRTR) {
        this.explode();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityRTR) {
        Entity target = entityRTR.getEntity();
        float deltaMoveLength = (float)this.getDeltaMovement().length();
        int actualDamage = Mth.ceil(Mth.clamp((double)deltaMoveLength * this.getBaseDamage(), 0.0D, 2.147483647E9D));

        Entity shooter = this.getOwner();
        DamageSource missileDamageSource;
        if (shooter == null) {
            missileDamageSource = (new IndirectEntityDamageSource("missile", this, this)).setProjectile();
        } else {
            missileDamageSource = (new IndirectEntityDamageSource("missile", this, shooter)).setProjectile();
            if (shooter instanceof LivingEntity) {
                ((LivingEntity)shooter).setLastHurtMob(target);
            }
        }

        int remainingFireTicks = target.getRemainingFireTicks();
        if (this.isOnFire()) {
            target.setSecondsOnFire(5);
        }

        if (target.hurt(missileDamageSource, (float)actualDamage)) {
            this.invulnerableTime = 0;
        } else {
            target.setRemainingFireTicks(remainingFireTicks);
        }
        this.postHitEntity(entityRTR);
    }

    protected void postHitEntity(EntityHitResult entityRTR) {
        this.explode();
    }

    protected void explode() {
        if(!this.level.isClientSide && !this.dud){
            this.remove();
            this.level.explode(
                    this,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    (float)this.explosionRadius * this.explosionStrength,
                    this.explosion$mode);
        }
    }

    public static Explosion.BlockInteraction explosionModeByOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal > Explosion.BlockInteraction.values().length) {
            ordinal = 0;
        }

        return Explosion.BlockInteraction.values()[ordinal];
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }
}
