package com.infamous.captain_america.common.registry;

import com.infamous.captain_america.CaptainAmerica;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class PotionRegistry implements IRegistryManager<Potion> {
    private static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTION_TYPES, CaptainAmerica.MODID);

    public static final RegistryObject<Potion> SUPER_SOLDIER_SERUM = POTIONS.register(
            "super_soldier_serum", () -> {
                int duration = Integer.MAX_VALUE;
                int amplifier = 0;
                boolean ambient = false;
                boolean visible = false;
                boolean showIcon = true;
                return new Potion(
                        "super_soldier",
                        new EffectInstance(EffectRegistry.SUPER_SOLDIER.get(), duration, amplifier, ambient, visible, showIcon));
            }
    );

    @Override
    public String getRegistryTypeForLogger() {
        return "potions";
    }

    @Override
    public DeferredRegister<Potion> getDeferredRegister() {
        return POTIONS;
    }
}
