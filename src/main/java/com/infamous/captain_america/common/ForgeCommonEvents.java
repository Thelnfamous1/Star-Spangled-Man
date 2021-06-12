package com.infamous.captain_america.common;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.drone_controller.DroneControllerProvider;
import com.infamous.captain_america.common.capability.drone_controller.IDroneController;
import com.infamous.captain_america.common.capability.shield_thrower.IShieldThrower;
import com.infamous.captain_america.common.capability.shield_thrower.ShieldThrowerProvider;
import com.infamous.captain_america.common.item.VibraniumShieldItem;
import com.infamous.captain_america.common.registry.EffectRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
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

@Mod.EventBusSubscriber(modid = CaptainAmerica.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeCommonEvents {

    public static final String HURT_CURRENTLY_USED_SHIELD_SRG_NAME = "func_184590_k";
    public static final int SHIELD_BLOCK_ID = 29;
    private static Method hurtCurrentlyUsedShield;

    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(new ResourceLocation(CaptainAmerica.MODID, "drone_controller"), new DroneControllerProvider());
            event.addCapability(new ResourceLocation(CaptainAmerica.MODID, "shield_thrower"), new ShieldThrowerProvider());
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

}
