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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CaptainAmerica.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeCommonEvents {

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

    private static boolean isKineticDamage(DamageSource damageSource){
        return damageSource == DamageSource.ANVIL
                || damageSource == DamageSource.FALL
                || damageSource == DamageSource.FALLING_BLOCK
                || damageSource == DamageSource.FLY_INTO_WALL;
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
