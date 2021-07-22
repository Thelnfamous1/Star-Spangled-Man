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
import com.infamous.captain_america.common.item.gauntlet.WeaponGauntletItem;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.common.util.CALogicHelper;
import com.infamous.captain_america.server.network.packet.SCombatPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import com.mojang.math.Quaternion;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;
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

                            Vec3 upVector = serverPlayer.getUpVector(1.0F);
                            Quaternion quaternion = new Quaternion(new Vector3f(upVector), 0, true);
                            Vec3 viewVector = serverPlayer.getViewVector(1.0F);
                            Vector3f viewVectorF = new Vector3f(viewVector);
                            viewVectorF.transform(quaternion);
                            missile.shoot((double)viewVectorF.x(), (double)viewVectorF.y(), (double)viewVectorF.z(), 3.2F, 0);

                            serverPlayer.level.addFreshEntity(missile);
                            serverPlayer.level.playSound((Player)null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.FIREWORK_ROCKET_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
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

                            Vec3 upVector = serverPlayer.getUpVector(1.0F);
                            Quaternion quaternion = new Quaternion(new Vector3f(upVector), 0, true);
                            Vec3 viewVector = serverPlayer.getViewVector(1.0F);
                            Vector3f viewVectorF = new Vector3f(viewVector);
                            viewVectorF.transform(quaternion);
                            timedGrenade.shoot((double)viewVectorF.x(), (double)viewVectorF.y(), (double)viewVectorF.z(), 3.2F, 0);

                            serverPlayer.level.addFreshEntity(timedGrenade);
                            serverPlayer.level.playSound((Player)null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.FIREWORK_ROCKET_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
                            CaptainAmerica.LOGGER.info("Server player {} has fired a timed grenade!", serverPlayer.getDisplayName().getString());
                        }
                    })
    );
    public static final AbilityManager LASER = AbilityManager.createOrReplace(Ability.LASER,
            new InputManager()
                    .onInitialPress(serverPlayer -> {
                        if(WeaponGauntletItem.isHoldingThis(serverPlayer)){
                            CaptainAmerica.LOGGER.debug("Server player {} has started firing their laser!", serverPlayer.getDisplayName().getString());
                            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SCombatPacket(SCombatPacket.Action.START_LASER));
                        }
                    })
                    .onHeld(serverPlayer -> {
                        if(WeaponGauntletItem.isHoldingThis(serverPlayer)){
                            HitResult rayTraceResult = CALogicHelper.getLaserRayTrace(serverPlayer);
                            if(rayTraceResult instanceof EntityHitResult){
                                Entity target = ((EntityHitResult) rayTraceResult).getEntity();
                                DamageSource laserDamageSource = DamageSource.playerAttack(serverPlayer).setIsFire();
                                boolean didHurt = target.hurt(laserDamageSource, 2.0F / 20);
                                if(didHurt){
                                    target.invulnerableTime = 0;
                                }
                            } else{
                                CaptainAmerica.LOGGER.debug("Server player {} is attempting to break a block!", serverPlayer.getDisplayName().getString());
                                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SCombatPacket(SCombatPacket.Action.CONTINUE_LASER));
                            }
                        } else {
                            CaptainAmerica.LOGGER.debug("Server player {} has stopped firing their laser!", serverPlayer.getDisplayName().getString());
                            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SCombatPacket(SCombatPacket.Action.STOP_LASER));

                        }
                    })
                    .onRelease(serverPlayer -> {
                        CaptainAmerica.LOGGER.debug("Server player {} has stopped firing their laser!", serverPlayer.getDisplayName().getString());
                        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SCombatPacket(SCombatPacket.Action.STOP_LASER));
                    })
    );

    private CombatAbilityManagers(){

    }
}
