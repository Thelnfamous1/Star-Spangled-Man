package com.infamous.captain_america.common;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.drone_controller.DroneControllerProvider;
import com.infamous.captain_america.common.capability.drone_controller.IDroneController;
import com.infamous.captain_america.common.capability.metal_arm.IMetalArm;
import com.infamous.captain_america.common.capability.metal_arm.MetalArmProvider;
import com.infamous.captain_america.common.capability.shield_thrower.IShieldThrower;
import com.infamous.captain_america.common.capability.shield_thrower.ShieldThrowerProvider;
import com.infamous.captain_america.common.item.MetalArmItem;
import com.infamous.captain_america.common.item.VibraniumShieldItem;
import com.infamous.captain_america.common.registry.EffectRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = CaptainAmerica.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeCommonEvents {

    public static final String HURT_CURRENTLY_USED_SHIELD_SRG_NAME = "func_184590_k";
    public static final int SHIELD_BLOCK_ID = 29;
    private static final UUID METAL_ARM_ATTACK_DAMAGE_UUID = UUID.fromString("b9f748ce-38a8-4ea0-bab7-7cbaa345ade3");
    private static final UUID METAL_ARM_ATTACK_KNOCKBACK_UUID = UUID.fromString("11bca4d6-e7b9-40ca-b876-68136bcebe28");
    private static Method hurtCurrentlyUsedShield;

    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(new ResourceLocation(CaptainAmerica.MODID, "drone_controller"), new DroneControllerProvider());
            event.addCapability(new ResourceLocation(CaptainAmerica.MODID, "shield_thrower"), new ShieldThrowerProvider());
            event.addCapability(new ResourceLocation(CaptainAmerica.MODID, "metal_arm"), new MetalArmProvider());
        }
    }

    @SubscribeEvent
    public static void onSprinting(LivingEvent.LivingUpdateEvent event){
        LivingEntity living = event.getEntityLiving();
        IShieldThrower shieldThrowerCap = CapabilityHelper.getShieldThrowerCap(living);
        if(shieldThrowerCap != null){
            boolean hasAcceleratedMovement = living.isSprinting() || living.isFallFlying();
            shieldThrowerCap.setShieldRunning(hasAcceleratedMovement && living.isBlocking());
            if(shieldThrowerCap.isShieldRunning() && !living.level.isClientSide){
                chargingStar(living);
            }
        }
    }

    /*
    Borrowed and reworked from tallestred's Big Brain mod
     */
    public static void chargingStar(LivingEntity shieldRunner) {
        List<Entity> hitEntities = shieldRunner.level.getEntities(shieldRunner, shieldRunner.getBoundingBox().expandTowards(shieldRunner.getDeltaMovement()), EntityPredicates.pushableBy(shieldRunner));
        if (!hitEntities.isEmpty() && shieldRunner.level instanceof ServerWorld) {
            for (Entity hitEntity : hitEntities) {
                final Vector3d preHitDeltaMove = shieldRunner.getDeltaMovement();
                if (hitEntity.distanceTo(shieldRunner) <= shieldRunner.distanceTo(hitEntity)) {
                    hitEntity.push(shieldRunner);
                    double initialChargeDamage = 10.0D;
                    double initialChargeKnockback = 10.0D;
                    // normal sprinting results in about 1.0 damage
                    // boosted elytra flight results in about 15.0 damage
                    float scaledChargeDamage = (float) (initialChargeDamage * preHitDeltaMove.length());
                    CaptainAmerica.LOGGER.debug("Initial damage: {}, Delta movement length: {}, Scaled damage: {}", initialChargeDamage, preHitDeltaMove.length(), scaledChargeDamage);
                    // normal sprinting results in about 1 block pushed back
                    // boosted elytra flight results in about 15.0 blocks pushed back
                    float scaledChargeKnockback = (float) (initialChargeKnockback * preHitDeltaMove.length());
                    CaptainAmerica.LOGGER.debug("Initial knockback: {}, Delta movement length: {}, Scaled knockback: {}", initialChargeKnockback, preHitDeltaMove.length(), scaledChargeKnockback);
                    for (int duration = 0; duration < 10; duration++) {
                        double xSpeed = shieldRunner.getRandom().nextGaussian() * 0.02D;
                        double ySpeed = shieldRunner.getRandom().nextGaussian() * 0.02D;
                        double zSpeed = shieldRunner.getRandom().nextGaussian() * 0.02D;
                        BasicParticleType particleType = hitEntity instanceof WitherEntity || hitEntity instanceof WitherSkeletonEntity ? ParticleTypes.SMOKE : ParticleTypes.CLOUD;
                        // Collision is done on the server side, so a server side method must be used.
                        ((ServerWorld) shieldRunner.level).sendParticles(particleType, shieldRunner.getRandomX(1.0D), shieldRunner.getRandomY() + 1.0D, shieldRunner.getRandomZ(1.0D), 1, xSpeed, ySpeed, zSpeed, 1.0D);
                    }
                    DamageSource chargeDamageSource = shieldRunner instanceof PlayerEntity ? DamageSource.playerAttack((PlayerEntity) shieldRunner) : DamageSource.mobAttack(shieldRunner);
                    if (hitEntity.hurt(chargeDamageSource, scaledChargeDamage)) {
                        if (hitEntity instanceof LivingEntity) {
                            ((LivingEntity) hitEntity).knockback(scaledChargeKnockback, (double) MathHelper.sin(shieldRunner.yRot * ((float) Math.PI / 180F)), (double) (-MathHelper.cos(shieldRunner.yRot * ((float) Math.PI / 180F))));
                            shieldRunner.setDeltaMovement(shieldRunner.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                        }
                        if (!shieldRunner.isSilent())
                            shieldRunner.level.playSound((PlayerEntity) null, shieldRunner.getX(), shieldRunner.getY(), shieldRunner.getZ(), SoundEvents.SHIELD_BLOCK, shieldRunner.getSoundSource(), 0.5F, 0.8F + shieldRunner.getRandom().nextFloat() * 0.4F);
                        if (hitEntity instanceof PlayerEntity && ((PlayerEntity) hitEntity).getUseItem().isShield(((PlayerEntity) hitEntity)))
                            ((PlayerEntity) hitEntity).disableShield(true);
                    }
                    shieldRunner.setLastHurtMob(hitEntity);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event){
        PlayerEntity oldPlayer = event.getOriginal();
        PlayerEntity newPlayer = event.getPlayer();
        IDroneController oldDroneControllerCap = CapabilityHelper.getDroneControllerCap(oldPlayer);
        IDroneController newDroneControllerCap = CapabilityHelper.getDroneControllerCap(newPlayer);
        if(oldDroneControllerCap != null && newDroneControllerCap != null){
            newDroneControllerCap.copyValuesFrom(oldDroneControllerCap);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if(event.phase == TickEvent.Phase.START && event.side == LogicalSide.CLIENT){
            IShieldThrower shieldThrowerCap = CapabilityHelper.getShieldThrowerCap(event.player);
            boolean hasShield = VibraniumShieldItem.hasVibraniumShield(event.player);
            if(shieldThrowerCap != null){
                if(hasShield){
                    if (shieldThrowerCap.getShieldChargingTicks() < 0) {
                        shieldThrowerCap.addShieldChargingTicks(1);
                        if (shieldThrowerCap.getShieldChargingTicks() == 0) {
                            shieldThrowerCap.setShieldChargingScale(0.0F);
                        }
                    }
                } else{
                    shieldThrowerCap.setShieldChargingScale(0.0F);
                }
            }
        }
    }

    @SubscribeEvent
    public static void postJump(LivingEvent.LivingJumpEvent event){
        LivingEntity entityLiving = event.getEntityLiving();
        if(entityLiving.getEffect(EffectRegistry.SUPER_SOLDIER.get()) != null){
            float superSoldierJumpFactor = 0.1F * 2;
            Vector3d deltaMovement = entityLiving.getDeltaMovement();
            double adjustedYDelta = deltaMovement.y + superSoldierJumpFactor;
            entityLiving.setDeltaMovement(deltaMovement.x, adjustedYDelta, deltaMovement.z);
        }
    }

    @SubscribeEvent
    public static void onKineticAttack(LivingAttackEvent event){
        LivingEntity entityLiving = event.getEntityLiving();
        DamageSource damageSource = event.getSource();
        if(!VibraniumShieldItem.hasVibraniumShield(entityLiving)){
            return;
        }
        if(entityLiving.level.isClientSide){
            return;
        }
        if(canBlockKineticDamage(entityLiving, damageSource)){
            hurtCurrentlyUsedShield(entityLiving, event.getAmount());
            entityLiving.level.broadcastEntityEvent(entityLiving, (byte) SHIELD_BLOCK_ID);
            event.setCanceled(true);
        }
    }

    private static void hurtCurrentlyUsedShield(LivingEntity entityLiving, float damageAmount) {
        if(hurtCurrentlyUsedShield == null){
            hurtCurrentlyUsedShield = ObfuscationReflectionHelper.findMethod(LivingEntity.class, HURT_CURRENTLY_USED_SHIELD_SRG_NAME, float.class);
        }
        try {
            hurtCurrentlyUsedShield.invoke(entityLiving, damageAmount);
        } catch (IllegalAccessException | InvocationTargetException e) {
            CaptainAmerica.LOGGER.error("Reflection error for LivingEntity#hurtCurrentlyUsedShield!");
        }
    }

    private static boolean canBlockKineticDamage(LivingEntity living, DamageSource damageSource) {
        boolean kineticDamage = isKineticDamage(damageSource);
        boolean blocking = living.isBlocking();
        boolean shieldStack = VibraniumShieldItem.isShieldStack(living.getUseItem());
        if (kineticDamage
                && blocking
                && shieldStack) {
            Direction[] directions = Direction.orderedByNearest(living);
            if(damageSource == DamageSource.FLY_INTO_WALL){
                return true;
            }
            if(damageSource == DamageSource.FALL){
                return directions[0] == Direction.DOWN;
            }
            if(damageSource == DamageSource.ANVIL || damageSource == DamageSource.FALLING_BLOCK){
                return directions[0] == Direction.UP;
            }
        }

        return false;
    }

    @SubscribeEvent
    public static void onKineticDamage(LivingDamageEvent event){
        LivingEntity entityLiving = event.getEntityLiving();
        DamageSource damageSource = event.getSource();
        if(entityLiving.getEffect(EffectRegistry.SUPER_SOLDIER.get()) != null
                && isKineticDamage(damageSource)){
            float superSoldierKineticResistanceFactor = 0.5F;
            float damage = event.getAmount();
            float adjustedDamage = damage * superSoldierKineticResistanceFactor;
            event.setAmount(adjustedDamage);
        }
    }

    private static boolean isUnblockableKineticDamage(DamageSource damageSource){
        return damageSource == DamageSource.FALL
                || damageSource == DamageSource.FLY_INTO_WALL;
    }

    private static boolean isKineticDamage(DamageSource damageSource){
        return damageSource == DamageSource.ANVIL
                || damageSource == DamageSource.FALLING_BLOCK
                || isUnblockableKineticDamage(damageSource);
    }

    @SubscribeEvent
    public static void onPotionApplicable(PotionEvent.PotionApplicableEvent event){
        LivingEntity entityLiving = event.getEntityLiving();
        EffectInstance effectInstance = event.getPotionEffect();
        Effect effect = effectInstance.getEffect();
        if(entityLiving.getEffect(EffectRegistry.SUPER_SOLDIER.get()) != null
            && isImmuneTo(effect)){
            event.setResult(Event.Result.DENY);
        }
    }

    private static boolean isImmuneTo(Effect effect){
        return effect == Effects.POISON
                || effect == Effects.CONFUSION
                || effect == Effects.HUNGER;
    }

    @SubscribeEvent
    public static void onMetalArmTick(LivingEvent.LivingUpdateEvent event){
        LivingEntity living = event.getEntityLiving();
        if(living.level.isClientSide) return;

        ModifiableAttributeInstance attackDamage = living.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) {
            if (attackDamage.getModifier(METAL_ARM_ATTACK_DAMAGE_UUID) != null) {
                attackDamage.removeModifier(METAL_ARM_ATTACK_DAMAGE_UUID);
            }
        }

        ModifiableAttributeInstance attackKnockback = living.getAttribute(Attributes.ATTACK_KNOCKBACK);
        if (attackKnockback != null) {
            if (attackKnockback.getModifier(METAL_ARM_ATTACK_KNOCKBACK_UUID) != null) {
                attackKnockback.removeModifier(METAL_ARM_ATTACK_KNOCKBACK_UUID);
            }
        }

        IMetalArm metalArmCap = CapabilityHelper.getMetalArmCap(living);
        if(metalArmCap != null){
            ItemStack metalArmMainHand = metalArmCap.getMetalArmMainHand();
            if(MetalArmItem.isMetalArmStack(metalArmMainHand)){
                MetalArmItem metalArmItem = (MetalArmItem) metalArmMainHand.getItem();
                if(attackDamage != null){
                    attackDamage.addTransientModifier(new AttributeModifier(METAL_ARM_ATTACK_DAMAGE_UUID, "Metal arm attack damage", metalArmItem.getAttackDamage(), AttributeModifier.Operation.ADDITION));
                }
                if(attackKnockback != null){
                    attackKnockback.addTransientModifier(new AttributeModifier(METAL_ARM_ATTACK_KNOCKBACK_UUID, "Metal arm attack knockback", metalArmItem.getAttackKnockback(), AttributeModifier.Operation.ADDITION));
                }
            }
        }
    }


}
