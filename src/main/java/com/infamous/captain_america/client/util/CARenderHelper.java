package com.infamous.captain_america.client.util;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.common.capability.shield_thrower.IShieldThrower;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class CARenderHelper {
    public static final ResourceLocation SHIELD_THROW_METER_LOCATION = new ResourceLocation(CaptainAmerica.MODID, "textures/gui/shield_throw_meter.png");
    public static final String GUI_FIELD_NAME = "field_71456_v";
    private static Gui ingameGui;
    private static Minecraft minecraft;

    public static void renderShieldThrowMeter(IShieldThrower shieldThrowerCap, PoseStack mStack)
    {
        boolean minecraftChanged = minecraft != Minecraft.getInstance();
        if(minecraftChanged){
            CaptainAmerica.LOGGER.info("RenderHelper.minecraft was updated to the current Minecraft instance!");
        }
        minecraft = minecraftChanged ? Minecraft.getInstance() : minecraft;
        minecraft.getTextureManager().bindForSetup(SHIELD_THROW_METER_LOCATION);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();

        minecraft.getProfiler().push("shieldThrowBar");
        minecraft.getTextureManager().bindForSetup(SHIELD_THROW_METER_LOCATION);

        float shieldChargingScale = shieldThrowerCap.getShieldChargingScale();
        int barLength = 182;
        int barHeight = 5;
        int scaledToleranceProgress = (int)(shieldChargingScale * 183.0F);

        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int barXPos = screenWidth / 2 - 91;
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        int barYPos = screenHeight - 32 + 3;

        int emptyBarTexOffsX = 0;
        int emptyBarTexOffsY = 84;
        int fullBarTexOffsX = 0;
        int fullTexOffsY = 89;

        Gui ingameGui = getIngameGui(minecraftChanged);
        if(ingameGui != null){
            ingameGui.blit(mStack, barXPos, barYPos, emptyBarTexOffsX, emptyBarTexOffsY, barLength, barHeight);
            if (scaledToleranceProgress > 0) {
                ingameGui.blit(mStack, barXPos, barYPos, fullBarTexOffsX, fullTexOffsY, scaledToleranceProgress, barHeight);
            }
        } else{
            CaptainAmerica.LOGGER.error("Unable to render shield throw meter!");
        }

        minecraft.getProfiler().pop();

        RenderSystem.enableBlend();
        minecraft.getProfiler().pop();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Nullable
    private static Gui getIngameGui(boolean minecraftChanged){
        if(ingameGui == null || minecraftChanged){
            Field gui = ObfuscationReflectionHelper.findField(Minecraft.class, GUI_FIELD_NAME);
            try {
                ingameGui = (Gui) gui.get(Minecraft.getInstance());
                return ingameGui;
            } catch (IllegalAccessException e) {
                CaptainAmerica.LOGGER.error("Couldn't get the value of the Minecraft.gui field! Used field name {}", GUI_FIELD_NAME);
                return null;
            }
        }
        return ingameGui;
    }
}