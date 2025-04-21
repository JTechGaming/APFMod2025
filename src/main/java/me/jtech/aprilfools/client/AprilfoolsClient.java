package me.jtech.aprilfools.client;

import me.jtech.aprilfools.AprilFoolsBlocks;
import me.jtech.aprilfools.CrosstalkManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.render.RenderLayer;

import java.util.Random;

public class AprilfoolsClient implements ClientModInitializer {
    private static final Random random = new Random();

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), AprilFoolsBlocks.PINE_BUSH, AprilFoolsBlocks.SUN_THORN);
    }
}
