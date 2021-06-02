package com.infamous.captain_america.common.capability.shield_thrower;

public class ShieldThrower implements IShieldThrower{
    private int shieldChargingTicks;
    private float shieldChargingScale;

    public ShieldThrower(){
        this.shieldChargingScale = 0;
        this.shieldChargingScale = 0.0F;
    }

    @Override
    public int getShieldChargingTicks() {
        return this.shieldChargingTicks;
    }

    @Override
    public void setShieldChargingTicks(int shieldChargingTicks) {
        this.shieldChargingTicks = shieldChargingTicks;
    }

    @Override
    public float getShieldChargingScale() {
        return this.shieldChargingScale;
    }

    @Override
    public void setShieldChargingScale(float shieldChargingScale) {
        this.shieldChargingScale = shieldChargingScale;
    }
}
