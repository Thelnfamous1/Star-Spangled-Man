package com.infamous.captain_america.client.util;

import com.infamous.captain_america.CaptainAmerica;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class CARenderType extends RenderType {
    private final static ResourceLocation LASER_BEAM_CORE = new ResourceLocation(CaptainAmerica.MODID,"textures/misc/laser_beam_core.png");
    private final static ResourceLocation LASER_BEAM_MAIN = new ResourceLocation(CaptainAmerica.MODID,"textures/misc/laser_beam_main.png");
    private final static ResourceLocation LASER_BEAM_GLOW = new ResourceLocation(CaptainAmerica.MODID,"textures/misc/laser_beam_glow.png");

    // Dummy
    public CARenderType(String name, VertexFormat format, VertexFormat.Mode p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable runnablePre, Runnable runnablePost) {
        super(name, format, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, runnablePre, runnablePost);
    }

    public static final RenderType BEACON_BEAM_MAIN = create("LaserBeamMain",
            DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256,
            true, false, // idk
            RenderType.CompositeState.builder()
                    .setTextureState(new TextureStateShard(LASER_BEAM_MAIN, false, false))
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setCullState(NO_CULL)
                    .setLightmapState(NO_LIGHTMAP)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false));

    public static final RenderType BEACON_BEAM_GLOW = create("LaserBeamGlow",
            DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256,
            true, false, // idk
            RenderType.CompositeState.builder()
                    .setTextureState(new TextureStateShard(LASER_BEAM_GLOW, false, false))
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setCullState(NO_CULL)
                    .setLightmapState(NO_LIGHTMAP)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false));

    public static final RenderType BEACON_BEAM_CORE = create("LaserBeamCore",
            DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256,
            true, false, // idk
            RenderType.CompositeState.builder()
                    .setTextureState(new TextureStateShard(LASER_BEAM_CORE, false, false))
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setCullState(NO_CULL)
                    .setLightmapState(NO_LIGHTMAP)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false));
}