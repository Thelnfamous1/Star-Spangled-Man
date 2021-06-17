package com.infamous.captain_america.common.capability.shield_thrower;

public interface IShieldThrower {

    boolean isShieldRunning();

    void setShieldRunning(boolean shieldRunning);

    int getShieldChargingTicks();

    void setShieldChargingTicks(int shieldChargingTicks);

    float getShieldChargingScale();

    void setShieldChargingScale(float shieldChargingScale);

    default void addShieldChargingTicks(int additional){
        this.setShieldChargingTicks(this.getShieldChargingTicks() + additional);
    }
}
