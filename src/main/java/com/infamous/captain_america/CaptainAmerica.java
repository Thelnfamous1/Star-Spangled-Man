package com.infamous.captain_america;

import com.infamous.captain_america.common.registry.*;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CaptainAmerica.MODID)
public class CaptainAmerica
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "captain_america";

    public CaptainAmerica() {
        IEventBus modBusEvent = FMLJavaModLoadingContext.get().getModEventBus();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        (new ItemRegistry()).register(modBusEvent);
        (new EntityTypeRegistry()).register(modBusEvent);
        (new SoundRegistry()).register(modBusEvent);
        (new EffectRegistry()).register(modBusEvent);
        (new PotionRegistry()).register(modBusEvent);
    }
}
