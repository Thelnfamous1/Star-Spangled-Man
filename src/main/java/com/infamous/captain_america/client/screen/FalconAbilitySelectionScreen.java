package com.infamous.captain_america.client.screen;

import com.infamous.captain_america.CaptainAmerica;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TranslatableComponent;

public class FalconAbilitySelectionScreen extends Screen {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 88;

    private ResourceLocation GUI = new ResourceLocation(CaptainAmerica.MODID,"textures/gui/solar_config_gui.png");

    public FalconAbilitySelectionScreen() {
        super(new TranslatableComponent("test.screen.thing"));
    }

    @Override
    public void init(){
        super.init();
        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        this.addRenderableWidget(new FalconButton(relX +7 , relY + 10-3, 65, 15, new TranslatableComponent("ability.falcon.flight"), button -> Minecraft.getInstance().setScreen(new FalconFlightScreen())));
        this.addRenderableWidget(new FalconButton(relX +7, relY + 30-3, 65, 15, new TranslatableComponent("ability.falcon.combat"), button -> Minecraft.getInstance().setScreen(new FalconCombatScreen())));
        this.addRenderableWidget(new FalconButton(relX +7, relY + 50-3, 65, 15, new TranslatableComponent("ability.falcon.drone"), button -> Minecraft.getInstance().setScreen(new FalconDroneScreen())));
        this.addRenderableWidget(new FalconButton(relX +7, relY + 70-3, 65, 15, new TranslatableComponent("ability.falcon.hud"), button -> Minecraft.getInstance().setScreen(new FalconHUDScreen())));
    }

    @Override
    public void render(PoseStack stack, int rouseX, int rouseY, float partialTicks){
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
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