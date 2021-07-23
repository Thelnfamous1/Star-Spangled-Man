package com.infamous.captain_america.client.screen;

import com.infamous.captain_america.CaptainAmerica;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;

import net.minecraft.client.gui.components.Button.OnPress;

public class FalconButton extends Button  {
    protected Button.OnTooltip tool;
    public static final ResourceLocation WIDGETS_SOLARFORGE = new ResourceLocation(CaptainAmerica.MODID,"textures/gui/widgets_solarforge.png");

    public FalconButton(int p_i232255_1_, int p_i232255_2_, int p_i232255_3_, int p_i232255_4_, Component p_i232255_5_, OnPress pressable) {
      super(p_i232255_1_, p_i232255_2_, p_i232255_3_, p_i232255_4_, p_i232255_5_, pressable);
    }

    public FalconButton(int p_i232256_1_, int p_i232256_2_, int p_i232256_3_, int p_i232256_4_, Component p_i232256_5_, OnPress pressable, Button.OnTooltip onTooltip) {
        super(p_i232256_1_, p_i232256_2_, p_i232256_3_, p_i232256_4_, p_i232256_5_, pressable,onTooltip);
        this.tool = onTooltip;
    }

    @Override
    public void playDownSound(SoundManager soundHandler) {
        soundHandler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1,1));
    }

    @Override
    public void renderButton(PoseStack matrixStack, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
        if (this.isHovered() && tool != null) {
            this.renderToolTip(matrixStack, p_230431_2_, p_230431_3_);
        }

        Minecraft minecraft = Minecraft.getInstance();
        Font fontrenderer = minecraft.font;
        RenderSystem.setShaderTexture(0, WIDGETS_SOLARFORGE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(matrixStack, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
        this.blit(matrixStack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        this.renderBg(matrixStack, minecraft, p_230431_2_, p_230431_3_);
        int j = getFGColor();
        drawCenteredString(matrixStack, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    @Override
    public void renderToolTip(PoseStack matrixStack, int p_230443_2_, int p_230443_3_) {
        if (tool != null) {
            this.tool.onTooltip(this, matrixStack, p_230443_2_, p_230443_3_);
        }
    }

}