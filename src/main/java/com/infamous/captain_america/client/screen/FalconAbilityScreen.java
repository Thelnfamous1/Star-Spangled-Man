package com.infamous.captain_america.client.screen;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.network.packet.CSetFalconAbilityPacket;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.infamous.captain_america.common.util.FalconAbilityKey;
import com.infamous.captain_america.common.util.FalconAbilityValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;
import java.util.function.Supplier;

public abstract class FalconAbilityScreen extends Screen {

    private static final int WIDTH = 77;
    private static final int HEIGHT = 180;
    private final FalconAbilityKey key;
    private final ResourceLocation GUI = new ResourceLocation(CaptainAmerica.MODID,"textures/gui/solar_config_gui.png");
    public int page;
    private int maxPages;
    private int previousPage;

    public FalconAbilityScreen(FalconAbilityKey key) {
        super(new TranslatableComponent("test.screen.thing"));
        this.key = key;
    }

    @Override
    public boolean mouseScrolled(double mousePosX, double mousePosY, double delta) {
        if (delta > 0 && this.page != this.maxPages){
            this.page += 1;
        }
        if (delta < 0 && this.page != 1){
            this.page -= 1;
        }
        return super.mouseScrolled(mousePosX, mousePosY, delta);
    }
    @Override
    public void init(){
        super.init();
        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        this.page = 1;
        this.maxPages = 1;
        this.previousPage = 1;
        this.addButtons(relX, relY);
    }

    protected void addButtons(int relX, int relY){
        int start = -10;
        for(Supplier<FalconAbilityValue> abilityValue : this.key.getChildrenSuppliers()){
            // actually starts at 10, and then adds 20 for each button after the first
            addFalconButton(relX, relY, start += 20, abilityValue.get());
        }
    }

    protected void addFalconButton(int relX, int relY, int offset, FalconAbilityValue value){
        this.addRenderableWidget(new FalconButton(relX + 6, relY-4+offset, 65, 15, new TranslatableComponent(this.key.getTranslationKey(value)), p -> {
            NetworkHandler.INSTANCE.sendToServer(new CSetFalconAbilityPacket(this.key, value));
            Minecraft.getInstance().setScreen(new FalconAbilitySelectionScreen());
        }));
    }

    @Override
    public void tick() {
        if (this.page != this.previousPage){
            List<Widget> buttonList = this.renderables;
            if (this.page > this.previousPage) {
                for (Widget currentWidget : buttonList) {
                    if (currentWidget instanceof AbstractWidget abstractWidget) {
                        abstractWidget.y += this.height;
                    }
                }

            } else {
                for (Widget currentWidget : buttonList) {
                    if (currentWidget instanceof AbstractWidget abstractWidget) {
                        abstractWidget.y -= this.height;
                    }
                }
            }
            this.previousPage = this.page;
        }
    }

    @Override
    public void render(PoseStack stack, int rouseX, int rouseY, float partialTicks){

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;


        this.blit(stack, relX, relY, 0, 0, WIDTH, HEIGHT);
        drawCenteredString(stack, this.minecraft.font, "Page "+page+"/"+maxPages, relX, relY, 0xffffff);

        // GL11.glScissor((int)(relX*scale), (int)(relY*scale), scissorsWidth, scissorsHeight-120);

        super.render(stack,rouseX,rouseY,partialTicks);

    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
