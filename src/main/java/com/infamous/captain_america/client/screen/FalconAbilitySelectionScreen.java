package com.infamous.captain_america.client.screen;

import com.infamous.captain_america.CaptainAmerica;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class FalconAbilitySelectionScreen extends Screen {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 88;

    private ResourceLocation GUI = new ResourceLocation(CaptainAmerica.MODID,"textures/gui/solar_config_gui.png");

    public FalconAbilitySelectionScreen() {
        super(new TranslationTextComponent("test.screen.thing"));
    }

    @Override
    public void init(){
        super.init();
        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        this.addButton(new FalconButton(relX +7 , relY + 10-3, 65, 15, new TranslationTextComponent("ability.falcon.flight"), button -> Minecraft.getInstance().setScreen(new FalconFlightScreen())));
        this.addButton(new FalconButton(relX +7, relY + 30-3, 65, 15, new TranslationTextComponent("ability.falcon.combat"), button -> Minecraft.getInstance().setScreen(new FalconCombatScreen())));
        this.addButton(new FalconButton(relX +7, relY + 50-3, 65, 15, new TranslationTextComponent("ability.falcon.drone"), button -> Minecraft.getInstance().setScreen(new FalconDroneScreen())));
        this.addButton(new FalconButton(relX +7, relY + 70-3, 65, 15, new TranslationTextComponent("ability.falcon.hud"), button -> Minecraft.getInstance().setScreen(new FalconHUDScreen())));
    }

    @Override
    public void render(MatrixStack stack, int rouseX, int rouseY, float partialTicks){
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(GUI);
        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        this.blit(stack, relX, relY, 77, 0, 100, HEIGHT);
        //this.renderBackground(stack);
        super.render(stack,rouseX,rouseY,partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}