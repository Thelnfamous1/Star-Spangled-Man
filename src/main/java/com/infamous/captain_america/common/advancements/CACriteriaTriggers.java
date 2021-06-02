package com.infamous.captain_america.common.advancements;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.advancements.criterion.HitByShieldTrigger;
import com.infamous.captain_america.common.advancements.criterion.ThrewShieldTrigger;
import net.minecraft.advancements.CriteriaTriggers;

public class CACriteriaTriggers {

    public static final ThrewShieldTrigger THREW_SHIELD = new ThrewShieldTrigger();
    public static final HitByShieldTrigger HIT_BY_SHIELD = new HitByShieldTrigger();

    public static void init(){
        CaptainAmerica.LOGGER.info("Registering criteria triggers!");
        CriteriaTriggers.register(THREW_SHIELD);
        CriteriaTriggers.register(HIT_BY_SHIELD);
        CaptainAmerica.LOGGER.info("Finished registering criteria triggers!");
    }

}
