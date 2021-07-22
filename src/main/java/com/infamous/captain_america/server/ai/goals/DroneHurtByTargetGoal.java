package com.infamous.captain_america.server.ai.goals;

import com.infamous.captain_america.common.entity.drone.IDrone;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.util.Iterator;
import java.util.List;

public class DroneHurtByTargetGoal<T extends PathfinderMob & IDrone> extends HurtByTargetGoal {
    private final T drone;

    public static final String TO_IGNORE_ALERT_FIELD_NAME = "field_220797_i";
    private static Class<?>[] reflectedToIgnoreAlert;

    public DroneHurtByTargetGoal(T droneMob, Class<?>... toIgnoreDamage) {
        super(droneMob, toIgnoreDamage);
        this.drone = droneMob;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !this.drone.isRecalled();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && !this.drone.isRecalled();
    }

    protected void alertOthers() {
        double followDistance = this.getFollowDistance();
        AABB followZone = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(followDistance, 10.0D, followDistance);
        List<? extends Mob> mobsInFollowZone = this.mob.level.getEntitiesOfClass(this.mob.getClass(), followZone, EntitySelector.NO_SPECTATORS);
        Iterator<? extends Mob> nearbyMobIterator = mobsInFollowZone.iterator();

        while(true) {
            Mob currentNearbyMob;
            while(true) {
                if (!nearbyMobIterator.hasNext()) {
                    return;
                }

                currentNearbyMob = nearbyMobIterator.next();
                if (this.mob != currentNearbyMob
                        && this.mob.getLastHurtByMob() != null
                        && currentNearbyMob.getTarget() == null
                        && this.neitherTameableOrHaveSameOwner(currentNearbyMob)
                        && !currentNearbyMob.isAlliedTo(this.mob.getLastHurtByMob())) {
                    if (this.getToIgnoreAlert() == null) {
                        break;
                    }

                    boolean flag = false;

                    for(Class<?> oclass : this.getToIgnoreAlert()) {
                        if (currentNearbyMob.getClass() == oclass) {
                            flag = true;
                            break;
                        }
                    }

                    if (!flag) {
                        break;
                    }
                }
            }

            this.alertOther(currentNearbyMob, this.mob.getLastHurtByMob());
        }
    }

    private boolean neitherTameableOrHaveSameOwner(Mob nearbyMob) {
        boolean goalOwnerNotTameable = !(this.mob instanceof TamableAnimal);
        boolean nearbyMobNotTameable = !(nearbyMob instanceof TamableAnimal);
        boolean bothTameable = !goalOwnerNotTameable && !nearbyMobNotTameable;
        boolean haveSameOwner = bothTameable && ((TamableAnimal) this.mob).getOwner() == ((TamableAnimal) nearbyMob).getOwner();
        return !bothTameable || haveSameOwner;
    }

    private Class<?>[] getToIgnoreAlert(){
        if(reflectedToIgnoreAlert == null){
            reflectedToIgnoreAlert = ObfuscationReflectionHelper.getPrivateValue(HurtByTargetGoal.class, this, TO_IGNORE_ALERT_FIELD_NAME);
        }
        return reflectedToIgnoreAlert;
    }
}
