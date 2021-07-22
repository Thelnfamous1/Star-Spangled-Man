package com.infamous.captain_america.common.entity.projectile;

import com.infamous.captain_america.common.registry.EntityTypeRegistry;
import com.infamous.captain_america.common.registry.ItemRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class BulletEntity extends Fireball {
	protected double damage = 1;
	protected boolean ignoreInvulnerability = false;
	protected double knockbackStrength = 0;
	protected int lifeTicks;

	public BulletEntity(EntityType<? extends BulletEntity> p_i50160_1_, Level p_i50160_2_) {
		super(p_i50160_1_, p_i50160_2_);
	}

	public BulletEntity(Level worldIn, LivingEntity shooter) {
		this(worldIn, shooter, 0, 0, 0);
		this.setPos(shooter.getX(), shooter.getY(0.5), shooter.getZ());
	}

	public BulletEntity(Level worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
		super(EntityTypeRegistry.BULLET.get(), shooter, accelX, accelY, accelZ, worldIn);
	}

	public BulletEntity(Level worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
		super(EntityTypeRegistry.BULLET.get(), x, y, z, accelX, accelY, accelZ, worldIn);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack getItem() {
		ItemStack itemstack = this.getItemRaw();
		return itemstack.isEmpty() ? new ItemStack(ItemRegistry.SMALL_CALIBER_BULLET.get()) : itemstack;
	}

	private static final double STOP_THRESHOLD = 0.01;

	@Override
	public void tick() {
		this.lifeTicks++;
		if (this.lifeTicks > 100 || this.getDeltaMovement().lengthSqr() < STOP_THRESHOLD) {
			this.discard();
		}
		super.tick();
	}

	@Override
	protected void onHitEntity(EntityHitResult entityRTR) {
		super.onHitEntity(entityRTR);
		if (!this.level.isClientSide) {
			Entity target = entityRTR.getEntity();
			Entity shooter = this.getOwner();

			if (this.isOnFire()) target.setSecondsOnFire(5);
			int lastInvulnerableTime = target.invulnerableTime;
			if (this.ignoreInvulnerability) target.invulnerableTime = 0;
			DamageSource bulletDamageSource = (new IndirectEntityDamageSource("bullet", this, shooter)).setProjectile();
			boolean damaged = target.hurt(bulletDamageSource, (float) this.damage);
			
			if (damaged && target instanceof LivingEntity) {
				LivingEntity livingTarget = (LivingEntity)target;
				if (this.knockbackStrength > 0) {
					double actualKnockback = this.knockbackStrength;
					Vec3 vec = this.getDeltaMovement().multiply(1, 0, 1).normalize().scale(actualKnockback);
					if (vec.lengthSqr() > 0) livingTarget.push(vec.x, 0.1, vec.z);
				}

				if (shooter instanceof LivingEntity) this.doEnchantDamageEffects((LivingEntity)shooter, target);
			}
			else if (!damaged && this.ignoreInvulnerability) target.invulnerableTime = lastInvulnerableTime;
		}
	}

	@Override
	protected void onHit(HitResult result) {
		super.onHit(result);
		//Don't disappear on blocks if we're set to noclipping
		if (!this.level.isClientSide && (!this.noPhysics || result.getType() != HitResult.Type.BLOCK)) discard();
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("lifeTicks", this.lifeTicks);
		compound.putDouble("damage", this.damage);
		if (this.ignoreInvulnerability) compound.putBoolean("ignoreinv", this.ignoreInvulnerability);
		if (this.knockbackStrength != 0) compound.putDouble("knockback", this.knockbackStrength);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.lifeTicks = compound.getInt("lifeTicks");
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
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}