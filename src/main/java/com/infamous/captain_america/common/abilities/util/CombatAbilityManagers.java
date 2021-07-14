package com.infamous.captain_america.common.abilities.util;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.abilities.Ability;
import com.infamous.captain_america.common.abilities.AbilityManager;
import com.infamous.captain_america.common.abilities.InputManager;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.entity.projectile.CAProjectileEntity;
import com.infamous.captain_america.common.entity.projectile.MissileEntity;
import com.infamous.captain_america.common.entity.projectile.TimedGrenadeEntity;
import com.infamous.captain_america.common.item.EXO7FalconItem;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.common.util.CALogicHelper;
import com.infamous.captain_america.server.network.packet.SCombatPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.network.PacketDistributor;

public class CombatAbilityManagers {

    public static final AbilityManager MISSILE = AbilityManager.createOrReplace(Ability.MISSILE,
            new InputManager()
                    .onInitialPress(serverPlayer -> {
                        if(EXO7FalconItem.getEXO7FalconStack(serverPlayer).isPresent()){
                            MissileEntity missile = new MissileEntity(serverPlayer, serverPlayer.level);
                            if (serverPlayer.abilities.instabuild) {
                                missile.pickup = CAProjectileEntity.PickupStatus.CREATIVE_ONLY;
                            }

                            Vector3d upVector = serverPlayer.getUpVector(1.0F);
                            Quaternion quaternion = new Quaternion(new Vector3f(upVector), 0, true);
                            Vector3d viewVector = serverPlayer.getViewVector(1.0F);
                            Vector3f viewVectorF = new Vector3f(viewVector);
                            viewVectorF.transform(quaternion);
                            missile.shoot((double)viewVectorF.x(), (double)viewVectorF.y(), (double)viewVectorF.z(), 3.2F, 0);

                            serverPlayer.level.addFreshEntity(missile);
                            serverPlayer.level.playSound((PlayerEntity)null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.FIREWORK_ROCKET_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                            CaptainAmerica.LOGGER.info("Server player {} has fired a missile!", serverPlayer.getDisplayName().getString());
                        }
                    })
    );
    public static final AbilityManager GRENADE = AbilityManager.createOrReplace(Ability.GRENADE,
            new InputManager()
                    .onInitialPress(serverPlayer -> {
                        if(EXO7FalconItem.getEXO7FalconStack(serverPlayer).isPresent()){
                            TimedGrenadeEntity timedGrenade = new TimedGrenadeEntity(serverPlayer, serverPlayer.level);
                            if (serverPlayer.abilities.instabuild) {
                                timedGrenade.pickup = CAProjectileEntity.PickupStatus.CREATIVE_ONLY;
                            }

                            Vector3d upVector = serverPlayer.getUpVector(1.0F);
                            Quaternion quaternion = new Quaternion(new Vector3f(upVector), 0, true);
                            Vector3d viewVector = serverPlayer.getViewVector(1.0F);
                            Vector3f viewVectorF = new Vector3f(viewVector);
                            viewVectorF.transform(quaternion);
                            timedGrenade.shoot((double)viewVectorF.x(), (double)viewVectorF.y(), (double)viewVectorF.z(), 3.2F, 0);

                            serverPlayer.level.addFreshEntity(timedGrenade);
                            serverPlayer.level.playSound((PlayerEntity)null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.FIREWORK_ROCKET_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                            CaptainAmerica.LOGGER.info("Server player {} has fired a timed grenade!", serverPlayer.getDisplayName().getString());
                        }
                    })
    );
    public static final AbilityManager LASER = AbilityManager.createOrReplace(Ability.LASER,
            new InputManager()
                    .onInitialPress(serverPlayer -> {
                        if(EXO7FalconItem.getEXO7FalconStack(serverPlayer).isPresent()
                                && serverPlayer.getMainHandItem().isEmpty()){
                            IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(serverPlayer);
                            if (falconAbilityCap == null) return;

                            falconAbilityCap.setShootingLaser(true);
                            CaptainAmerica.LOGGER.debug("Server player {} has started firing their laser!", serverPlayer.getDisplayName().getString());
                            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SCombatPacket(SCombatPacket.Action.START_LASER));
                        }
                    })
                    .onHeld(serverPlayer -> {
                        IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(serverPlayer);
                        if (falconAbilityCap == null) return;

                        boolean hasCorrectEquipment = EXO7FalconItem.getEXO7FalconStack(serverPlayer).isPresent()
                                && serverPlayer.getMainHandItem().isEmpty();
                        if(falconAbilityCap.isShootingLaser()
                                && hasCorrectEquipment){
                            RayTraceResult rayTraceResult = CALogicHelper.getLaserRayTrace(serverPlayer);
                            if(rayTraceResult instanceof EntityRayTraceResult){
                                Entity target = ((EntityRayTraceResult) rayTraceResult).getEntity();
                                DamageSource laserDamageSource = DamageSource.playerAttack(serverPlayer).setIsFire();
                                boolean didHurt = target.hurt(laserDamageSource, 2.0F / 20);
                                if(didHurt){
                                    target.invulnerableTime = 0;
                                }
                            } else{
                                CaptainAmerica.LOGGER.debug("Server player {} is attempting to break a block!", serverPlayer.getDisplayName().getString());
                                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SCombatPacket(SCombatPacket.Action.CONTINUE_LASER));
                            }
                        } else if(falconAbilityCap.isShootingLaser() && !hasCorrectEquipment){
                            falconAbilityCap.setShootingLaser(false);
                            CaptainAmerica.LOGGER.debug("Server player {} has stopped firing their laser!", serverPlayer.getDisplayName().getString());
                            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SCombatPacket(SCombatPacket.Action.STOP_LASER));

                        }
                    })
                    .onRelease(serverPlayer -> {
                        IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(serverPlayer);
                        if (falconAbilityCap == null) return;

                        falconAbilityCap.setShootingLaser(false);
                        CaptainAmerica.LOGGER.debug("Server player {} has stopped firing their laser!", serverPlayer.getDisplayName().getString());
                        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SCombatPacket(SCombatPacket.Action.STOP_LASER));
                    })
    );

    private CombatAbilityManagers(){

    }
}
