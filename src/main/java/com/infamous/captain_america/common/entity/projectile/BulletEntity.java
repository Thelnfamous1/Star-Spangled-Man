package com.infamous.captain_america.common.entity.projectile;

import com.infamous.captain_america.common.item.IBullet;
import com.infamous.captain_america.common.registry.EntityTypeRegistry;
import com.infamous.captain_america.common.registry.ItemRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class BulletEntity extends AbstractFireballEntity {
	protected double damage = 1;
	protected boolean ignoreInvulnerability = false;
	protected double knockbackStrength = 0;
	protected int ticksSinceFired;

	public BulletEntity(EntityType<? extends BulletEntity> p_i50160_1_, World p_i50160_2_) {
		super(p_i50160_1_, p_i50160_2_);
	}

	public BulletEntity(World worldIn, LivingEntity shooter) {
		this(worldIn, shooter, 0, 0, 0);
		setPos(shooter.getX(), shooter.getY(0.5), shooter.getZ());
	}

	public BulletEntity(World worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
		super(EntityTypeRegistry.BULLET.get(), shooter, accelX, accelY, accelZ, worldIn);
	}

	public BulletEntity(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
		super(EntityTypeRegistry.BULLET.get(), x, y, z, accelX, accelY, accelZ, worldIn);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack getItem() {
		ItemStack itemstack = this.getItemRaw();
		return itemstack.isEmpty() ? new ItemStack(ItemRegistry.PISTOL_BULLET.get()) : itemstack;
	}

	private static final double STOP_THRESHOLD = 0.01;

	@Override
	public void tick() {
		//Using a thing I save so that bullets don't get clogged up on chunk borders
		this.ticksSinceFired++;
		if (this.ticksSinceFired > 100 || this.getDeltaMovement().lengthSqr() < STOP_THRESHOLD) {
			remove();
		}
		super.tick();
	}

	@Override
	protected void onHitEntity(EntityRayTraceResult raytrace) {
		super.onHitEntity(raytrace);
		if (!this.level.isClientSide) {
			Entity target = raytrace.getEntity();
			Entity shooter = getOwner();
			IBullet bullet = (IBullet) getItem().getItem();
			
			if (this.isOnFire()) target.setSecondsOnFire(5);
			int lastInvulnerableTime = target.invulnerableTime;
			if (this.ignoreInvulnerability) target.invulnerableTime = 0;
			boolean damaged = target.hurt((new IndirectEntityDamageSource("arrow", this, shooter)).setProjectile(), (float) bullet.modifyDamage(damage, this, target, shooter, level));
			
			if (damaged && target instanceof LivingEntity) {
				LivingEntity livingTarget = (LivingEntity)target;
				if (this.knockbackStrength > 0) {
					double actualKnockback = this.knockbackStrength;
					Vector3d vec = this.getDeltaMovement().multiply(1, 0, 1).normalize().scale(actualKnockback);
					if (vec.lengthSqr() > 0) livingTarget.push(vec.x, 0.1, vec.z);
				}

				if (shooter instanceof LivingEntity) this.doEnchantDamageEffects((LivingEntity)shooter, target);
				
				bullet.onLivingEntityHit(this, livingTarget, shooter, this.level);
			}
			else if (!damaged && this.ignoreInvulnerability) target.invulnerableTime = lastInvulnerableTime;
		}
	}

	@Override
	protected void onHit(RayTraceResult result) {
		super.onHit(result);
		//Don't disappear on blocks if we're set to noclipping
		if (!this.level.isClientSide && (!this.noPhysics || result.getType() != RayTraceResult.Type.BLOCK)) remove();
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("tsf", this.ticksSinceFired);
		compound.putDouble("damage", this.damage);
		if (this.ignoreInvulnerability) compound.putBoolean("ignoreinv", this.ignoreInvulnerability);
		if (this.knockbackStrength != 0) compound.putDouble("knockback", this.knockbackStrength);
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		super.readAdditionalSaveData(compound);
		this.ticksSinceFired = compound.getInt("tsf");
		this.damage = compound.getDouble("damage");
		//The docs says if it's not here it's gonna be false/0 so it should be good
		this.ignoreInvulnerability = compound.getBoolean("ignoreinv");
		this.knockbackStrength = compound.getDouble("knockback");
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public double getDamage() {
		return this.damage;
	}

	public void setIgnoreInvulnerability(boolean ignoreInvulnerability) {
		this.ignoreInvulnerability = ignoreInvulnerability;
	}

	/**
	 * Knockback on impact, 0.6 is equivalent to Punch I.
	 */
	public void setKnockbackStrength(double knockbackStrength) {
		this.knockbackStrength = knockbackStrength;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return false;
	}

	@Override
	protected boolean shouldBurn() {
		return false;
	}

	@Override
	protected float getInertia() {
		return 1;
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}