package com.infamous.captain_america.common.entity;

import com.infamous.captain_america.common.util.VibraniumShieldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class VibraniumShieldEntity extends ThrowableEntity {

    private static final DataParameter<Boolean> ID_FOIL = EntityDataManager.defineId(VibraniumShieldEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<ItemStack> DATA_SHIELD_ITEM = EntityDataManager.defineId(VibraniumShieldEntity.class, DataSerializers.ITEM_STACK);
    private static final DataParameter<Byte> DATA_THROW_TYPE = EntityDataManager.defineId(VibraniumShieldEntity.class, DataSerializers.BYTE);

    private int life;
    private double baseDamage = 2.0D;
    public VibraniumShieldEntity.PickupStatus pickup = PickupStatus.ALLOWED;

    @Nullable
    private BlockState lastHitBlockState;
    @Nullable
    private BlockState inBlockState;
    private int lastHitEntityId = -1;

    protected boolean inGround;
    protected int inGroundTime;

    public VibraniumShieldEntity(EntityType<? extends VibraniumShieldEntity> entityType, World world){
        super(entityType, world);
    }

    public VibraniumShieldEntity(EntityType<? extends VibraniumShieldEntity> entityType, LivingEntity thrower, World world, ThrowType throwType) {
        super(entityType, thrower, world);
        if (thrower instanceof PlayerEntity) {
            this.pickup = VibraniumShieldEntity.PickupStatus.ALLOWED;
        }
        this.setThrowTypeData((byte) throwType.ordinal());
    }

    @Override
    public void tick() {
        super.tick();

        BlockPos shieldBlockPos = this.blockPosition();
        BlockState blockStateShieldAt = this.level.getBlockState(shieldBlockPos);

        /*
        if (!blockStateShieldAt.isAir(this.level, shieldBlockPos)) {
            VoxelShape collisionShape = blockStateShieldAt.getCollisionShape(this.level, shieldBlockPos);
            if (!collisionShape.isEmpty()) {
                Vector3d shieldPosVec = this.position();

                for(AxisAlignedBB boundingBox : collisionShape.toAabbs()) {
                    boolean verticalCollision = Direction.getNearest(shieldBlockPos.getX(), shieldBlockPos.getY(), shieldBlockPos.getZ()).getAxis() == Direction.Axis.Y;
                    if (boundingBox.move(shieldBlockPos).contains(shieldPosVec)
                            && verticalCollision) {
                        //this.inGround = true;
                        break;
                    }
                }
            }
        }
         */

        if (this.inGround) {
            if (this.inBlockState != blockStateShieldAt && this.shouldFall()) {
                this.startFalling();
            } else if (!this.level.isClientSide) {
                this.tickDespawn();
            }

            ++this.inGroundTime;
        } else{
            this.inGroundTime = 0;
        }
    }

    private boolean shouldFall() {
        return this.inGround && this.level.noCollision((new AxisAlignedBB(this.position(), this.position())).inflate(0.06D));
    }

    private void startFalling() {
        this.inGround = false;
        Vector3d vector3d = this.getDeltaMovement();
        this.setDeltaMovement(vector3d.multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F)));
        this.life = 0;
    }

    public void move(MoverType moverType, Vector3d vector3d) {
        super.move(moverType, vector3d);
        if (moverType != MoverType.SELF && this.shouldFall()) {
            this.startFalling();
        }

    }

    protected void tickDespawn() {
        ++this.life;
        if (this.life >= 1200) {
            this.spawnAtLocation(this.getShieldStack(), 0.1F);
            this.remove();
        }
    }

    @Override
    protected void onHit(RayTraceResult rayTraceResult) {
        boolean wasInGround = this.inGround;
        super.onHit(rayTraceResult);
        if(!wasInGround){
            this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 1.0F);
        }
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult blockRayTraceResult) {
        super.onHitBlock(blockRayTraceResult);
        this.handleThrowImpact(blockRayTraceResult);
        if(blockRayTraceResult.getDirection().getAxis() == Direction.Axis.Y){
            Vector3d posDiff = blockRayTraceResult.getLocation().subtract(this.getX(), this.getY(), this.getZ());
            this.setDeltaMovement(posDiff);
            Vector3d vector3d1 = posDiff.normalize().scale((double)0.05F);
            this.setPosRaw(this.getX() - vector3d1.x, this.getY() - vector3d1.y, this.getZ() - vector3d1.z);
            this.inGround = true;
            this.inBlockState = this.level.getBlockState(blockRayTraceResult.getBlockPos());
        }
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult entityRayTraceResult) {
        super.onHitEntity(entityRayTraceResult);
        Entity entity = entityRayTraceResult.getEntity();
        if(entity != this.getOwner()){
            float deltaMovementLength = (float)this.getDeltaMovement().length();
            int damage = MathHelper.ceil(MathHelper.clamp((double)deltaMovementLength * this.getBaseDamage(), 0.0D, 2.147483647E9D));
            entity.hurt(DamageSource.thrown(this, this.getOwner()), (float)damage);
            this.handleThrowImpact(entityRayTraceResult);
        }
        this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 1.0F);
    }

    private void handleThrowImpact(RayTraceResult rayTraceResult) {
        if(!this.inGround){
            switch (ThrowType.byOrdinal(this.getThrowTypeData())){
                case BOOMERANG_THROW:
                    this.boomerang(rayTraceResult);
                    break;
                case RICOCHET_THROW:
                    this.handleRicochet(rayTraceResult);
                    break;
            }
        }
        this.checkNotMoving();
    }
    
    private void checkNotMoving() {
        if (!this.level.isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
            if (this.pickup == PickupStatus.ALLOWED) {
                this.spawnAtLocation(this.getShieldStack(), 0.1F);
            }
            this.remove();
        }
    }

    private void boomerang(RayTraceResult rayTraceResult) {
        this.setDeltaMovement(this.getDeltaMovement().scale(-1.0D));
        this.yRot += 180.0F;
        this.yRotO += 180.0F;

        if(rayTraceResult instanceof EntityRayTraceResult){
            Entity entity = ((EntityRayTraceResult) rayTraceResult).getEntity();
            this.lastHitEntityId = entity.getId();
            this.lastHitBlockState = null;
        }
        else if(rayTraceResult instanceof BlockRayTraceResult){
            BlockPos blockPos = ((BlockRayTraceResult) rayTraceResult).getBlockPos();
            this.lastHitBlockState = this.level.getBlockState(blockPos);
            this.lastHitEntityId = -1;
        }
    }

    private void handleRicochet(RayTraceResult rayTraceResult) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            Entity entity = ((EntityRayTraceResult) rayTraceResult).getEntity();
            if(this.lastHitEntityId != entity.getId()){
                this.lastHitEntityId = entity.getId();
                //TODO:
                this.ricochetOffEntity(entity);
            }
            this.lastHitBlockState = null;
        } else if(rayTraceResult instanceof BlockRayTraceResult){
            BlockPos blockPos = ((BlockRayTraceResult) rayTraceResult).getBlockPos();
            if(this.lastHitBlockState != this.level.getBlockState(blockPos)){
                this.lastHitBlockState = this.level.getBlockState(blockPos);
                //TODO:
                this.ricochetOffBlock();
            }
            this.lastHitEntityId = -1;
        }
    }

    private void ricochetOffBlock() {
        Vector3d deltaMovement = this.getDeltaMovement();
        Vector3d xMovement = new Vector3d(deltaMovement.x, 0, 0);
        Vector3d yMovement = new Vector3d(0, deltaMovement.y, 0);
        Vector3d zMovement = new Vector3d(0, 0, deltaMovement.z);
        if(VibraniumShieldHelper.checkRicochetBlock(this, xMovement)){
            this.setDeltaMovement(-deltaMovement.x, deltaMovement.y, deltaMovement.z);
        }
        if(VibraniumShieldHelper.checkRicochetBlock(this, yMovement)){
            this.setDeltaMovement(deltaMovement.x, -deltaMovement.y, deltaMovement.z);
        }
        if(VibraniumShieldHelper.checkRicochetBlock(this, zMovement)){
            this.setDeltaMovement(deltaMovement.x, deltaMovement.y, -deltaMovement.z);
        }
    }

    private void ricochetOffEntity(Entity entity){
        Vector3d deltaMovement = this.getDeltaMovement();
        Vector3d xMovement = new Vector3d(deltaMovement.x, 0, 0);
        Vector3d yMovement = new Vector3d(0, deltaMovement.y, 0);
        Vector3d zMovement = new Vector3d(0, 0, deltaMovement.z);
        if(VibraniumShieldHelper.checkRicochetEntityWithBlockCheck(this, xMovement, entity)){
            this.setDeltaMovement(-deltaMovement.x, deltaMovement.y, deltaMovement.z);
        }
        if(VibraniumShieldHelper.checkRicochetEntityWithBlockCheck(this, yMovement, entity)){
            this.setDeltaMovement(deltaMovement.x, -deltaMovement.y, deltaMovement.z);
        }
        if(VibraniumShieldHelper.checkRicochetEntityWithBlockCheck(this, zMovement, entity)){
            this.setDeltaMovement(deltaMovement.x, deltaMovement.y, -deltaMovement.z);
        }
    }

    public void setBaseDamage(double baseDamage) {
        this.baseDamage = baseDamage;
    }

    public double getBaseDamage() {
        return this.baseDamage;
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity);
    }

    @Override
    public void playerTouch(PlayerEntity player) {
        boolean leftOwner = this.getLeftOwner();
        if (!this.level.isClientSide && (leftOwner || this.inGround)) {
            boolean canPickUp = this.pickup == PickupStatus.ALLOWED
                    || this.pickup == PickupStatus.CREATIVE_ONLY
                    && player.abilities.instabuild;
            if (this.pickup == PickupStatus.ALLOWED && !player.inventory.add(this.getShieldStack())) {
                canPickUp = false;
            }

            if (canPickUp) {
                this.remove();
            }
        }
    }

    private Boolean getLeftOwner() {
        return ObfuscationReflectionHelper.getPrivateValue(ProjectileEntity.class, this, "field_234611_d_");
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_SHIELD_ITEM, ItemStack.EMPTY);
        this.entityData.define(ID_FOIL, false);
        this.entityData.define(DATA_THROW_TYPE, (byte)0);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public ItemStack getShieldStack() {
        return this.entityData.get(DATA_SHIELD_ITEM);
    }

    public void setShieldStack(ItemStack shieldStack) {
        this.entityData.set(DATA_SHIELD_ITEM, shieldStack);
        this.entityData.set(ID_FOIL, shieldStack.hasFoil());
    }
    
    public Byte getThrowTypeData(){
        return this.entityData.get(DATA_THROW_TYPE);
    }
    
    public void setThrowTypeData(byte throwTypeOrdinal){
        this.entityData.set(DATA_THROW_TYPE, throwTypeOrdinal);
    }

    public boolean isFoil() {
        return this.entityData.get(ID_FOIL);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        ItemStack itemstack = ItemStack.of(compoundNBT.getCompound("ShieldItem"));
        if (!itemstack.isEmpty()) {
            this.entityData.set(DATA_SHIELD_ITEM, itemstack);
        }
        if (compoundNBT.contains("pickup", 99)) {
            this.pickup = VibraniumShieldEntity.PickupStatus.byOrdinal(compoundNBT.getByte("pickup"));
        } else if (compoundNBT.contains("player", 99)) {
            this.pickup = compoundNBT.getBoolean("player") ? VibraniumShieldEntity.PickupStatus.ALLOWED : VibraniumShieldEntity.PickupStatus.DISALLOWED;
        }
        
        if(compoundNBT.contains("ThrowType", 99)){
            this.setThrowTypeData(compoundNBT.getByte("ThrowType"));
        }
        
        this.life = compoundNBT.getShort("life");
        
        if (compoundNBT.contains("damage", 99)) {
            this.baseDamage = compoundNBT.getDouble("damage");
        }

        if (compoundNBT.contains("inBlockState", 10)) {
            this.inBlockState = NBTUtil.readBlockState(compoundNBT.getCompound("inBlockState"));
        }

        this.inGround = compoundNBT.getBoolean("inGround");
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        ItemStack itemstack = this.entityData.get(DATA_SHIELD_ITEM);
        if (!itemstack.isEmpty()) {
            compoundNBT.put("ShieldItem", itemstack.save(new CompoundNBT()));
        }
        compoundNBT.putByte("pickup", (byte)this.pickup.ordinal());
        compoundNBT.putByte("ThrowType", this.getThrowTypeData());
        compoundNBT.putShort("life", (short)this.life);
        compoundNBT.putDouble("damage", this.baseDamage);


        if (this.inBlockState != null) {
            compoundNBT.put("inBlockState", NBTUtil.writeBlockState(this.inBlockState));
        }
        compoundNBT.putBoolean("inGround", this.inGround);


    }

    @Override
    public void setOwner(@Nullable Entity owner) {
        super.setOwner(owner);
        if (owner instanceof PlayerEntity) {
            this.pickup = ((PlayerEntity)owner).abilities.instabuild ? VibraniumShieldEntity.PickupStatus.CREATIVE_ONLY : VibraniumShieldEntity.PickupStatus.ALLOWED;
        }
    }

    public enum PickupStatus {
        DISALLOWED,
        ALLOWED,
        CREATIVE_ONLY;

        public static VibraniumShieldEntity.PickupStatus byOrdinal(int ordinal) {
            if (ordinal < 0 || ordinal > values().length) {
                ordinal = 0;
            }

            return values()[ordinal];
        }
    }

    public enum ThrowType {
        BOOMERANG_THROW,
        RICOCHET_THROW;

        public static ThrowType byOrdinal(int ordinal) {
            if (ordinal < 0 || ordinal > values().length) {
                ordinal = 0;
            }

            return values()[ordinal];
        }
    }
}
