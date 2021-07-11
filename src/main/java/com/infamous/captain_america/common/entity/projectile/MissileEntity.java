package com.infamous.captain_america.common.entity.projectile;

import com.infamous.captain_america.common.registry.EntityTypeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class MissileEntity extends AbstractArrowEntity {
    private float explosionStrength = 1.0F;
    private int explosionRadius = 3;
    private Explosion.Mode explosion$mode = Explosion.Mode.BREAK;
    private boolean dud;

    public MissileEntity(EntityType<? extends MissileEntity> entityType, World world) {
        super(entityType, world);
    }

    public MissileEntity(LivingEntity shooter, World p_i48548_3_) {
        super(EntityTypeRegistry.MISSILE.get(), shooter, p_i48548_3_);
    }

    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);

        compoundNBT.putByte("ExplosionRadius", (byte)this.explosionRadius);
        compoundNBT.putFloat("ExplosionStrength", this.explosionStrength);
        compoundNBT.putByte("ExplosionMode", (byte)this.explosion$mode.ordinal());
        compoundNBT.putBoolean("Dud", this.dud);
    }

    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
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
    protected void onHitBlock(BlockRayTraceResult blockRTR) {
        super.onHitBlock(blockRTR);
        this.explode();
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult entityRTR) {
        Entity target = entityRTR.getEntity();
        float deltaMoveLength = (float)this.getDeltaMovement().length();
        int actualDamage = MathHelper.ceil(MathHelper.clamp((double)deltaMoveLength * this.getBaseDamage(), 0.0D, 2.147483647E9D));

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
        this.explode();
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    private void explode() {
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

    public static Explosion.Mode explosionModeByOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal > Explosion.Mode.values().length) {
            ordinal = 0;
        }

        return Explosion.Mode.values()[ordinal];
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldRender(double p_145770_1_, double p_145770_3_, double p_145770_5_) {
        return true;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
