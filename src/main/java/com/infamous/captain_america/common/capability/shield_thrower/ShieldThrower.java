package com.infamous.captain_america.common.capability.shield_thrower;

public class ShieldThrower implements IShieldThrower{
    private int shieldChargingTicks;
    private float shieldChargingScale;
    private boolean isShieldRunning;

    public ShieldThrower(){
        this.shieldChargingScale = 0;
        this.shieldChargingScale = 0.0F;
        this.isShieldRunning = false;
    }

    @Override
    public boolean isShieldRunning() {
        return this.isShieldRunning;
    }

    @Override
    public void setShieldRunning(boolean shieldRunning) {
        this.isShieldRunning = shieldRunning;
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
