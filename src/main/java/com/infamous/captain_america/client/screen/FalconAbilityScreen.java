package com.infamous.captain_america.client.screen;

import com.infamous.captain_america.CaptainAmerica;
import com.infamous.captain_america.client.network.packet.CSetFalconAbilityPacket;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.network.NetworkHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public abstract class FalconAbilityScreen extends Screen {

    private static final int WIDTH = 77;
    private static final int HEIGHT = 180;
    private final IFalconAbility.Key key;
    private final ResourceLocation GUI = new ResourceLocation(CaptainAmerica.MODID,"textures/gui/solar_config_gui.png");
    public int page;
    private int maxPages;
    private int previousPage;


    public FalconAbilityScreen(IFalconAbility.Key key) {
        super(new TranslationTextComponent("test.screen.thing"));
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

    protected abstract void addButtons(int relX, int relY);

    protected void addFalconButton(int relX, int relY, int offset, IFalconAbility.Value value){
        this.addButton(new FalconButton(relX + 6, relY-4+offset, 65, 15, new TranslationTextComponent(value.getTranslationKey()), p -> {
            NetworkHandler.INSTANCE.sendToServer(new CSetFalconAbilityPacket(this.key, value));
            Minecraft.getInstance().setScreen(new FalconAbilitySelectionScreen());
        }));
    }

    @Override
    public void tick() {
        if (this.page != this.previousPage){
            List<Widget> buttonList = this.buttons;
            if (this.page > this.previousPage) {
                System.out.println(buttonList.get(0).y);
                for (int i = 0; i < buttonList.size(); i++) {
                    buttonList.get(i).y += this.height;
                }

                System.out.println(buttonList.get(0).y);
                this.previousPage = this.page;
            } else {
                System.out.println(buttonList.get(0).y);
                for (int i = 0; i < buttonList.size(); i++) {
                    buttonList.get(i).y -= this.height;
                }
                System.out.println(buttonList.get(0).y);
                this.previousPage = page;
            }
        }
    }

    @Override
    public void render(MatrixStack stack, int rouseX, int rouseY, float partialTicks){

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(GUI);
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
