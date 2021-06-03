package com.infamous.captain_america.server.ai.goals;

import com.infamous.captain_america.common.entity.drone.IDrone;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.Iterator;
import java.util.List;

public class DroneHurtByTargetGoal<T extends CreatureEntity & IDrone> extends HurtByTargetGoal {
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
        AxisAlignedBB followZone = AxisAlignedBB.unitCubeFromLowerCorner(this.mob.position()).inflate(followDistance, 10.0D, followDistance);
        List<MobEntity> mobsInFollowZone = this.mob.level.getLoadedEntitiesOfClass(this.mob.getClass(), followZone);
        Iterator<MobEntity> nearbyMobIterator = mobsInFollowZone.iterator();

        while(true) {
            MobEntity currentNearbyMob;
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

    private boolean neitherTameableOrHaveSameOwner(MobEntity nearbyMob) {
        boolean goalOwnerNotTameable = !(this.mob instanceof TameableEntity);
        boolean nearbyMobNotTameable = !(nearbyMob instanceof TameableEntity);
        boolean bothTameable = !goalOwnerNotTameable && !nearbyMobNotTameable;
        boolean haveSameOwner = bothTameable && ((TameableEntity) this.mob).getOwner() == ((TameableEntity) nearbyMob).getOwner();
        return !bothTameable || haveSameOwner;
    }

    private Class<?>[] getToIgnoreAlert(){
        if(reflectedToIgnoreAlert == null){
            reflectedToIgnoreAlert = ObfuscationReflectionHelper.getPrivateValue(HurtByTargetGoal.class, this, TO_IGNORE_ALERT_FIELD_NAME);
        }
        return reflectedToIgnoreAlert;
    }
}
