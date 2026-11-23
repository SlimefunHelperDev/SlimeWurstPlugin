package me.matl114.wurstintegration.modules;

import me.matl114.managers.config.Config;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;

public final class OverlayModule extends WurstRenderModule {
    public OverlayModule(Config config) { super(config, "Overlay", "overlay"); }
    @Override protected void collect() {
        if (mc.options.attackKey.isPressed() && mc.crosshairTarget instanceof BlockHitResult hit) {
            overlayCollector.submit(new Box(hit.getBlockPos()), 0x4000AAFF);
        }
    }
}
