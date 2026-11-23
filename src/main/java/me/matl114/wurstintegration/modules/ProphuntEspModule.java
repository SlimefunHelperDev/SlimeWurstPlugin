package me.matl114.wurstintegration.modules;

import me.matl114.managers.config.Config;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public final class ProphuntEspModule extends WurstRenderModule {
    public ProphuntEspModule(Config config) { super(config, "ProphuntESP", "prophunt-esp"); }
    @Override protected void collect() {
        float alpha = 0.5F + 0.25F * MathHelper.sin((System.currentTimeMillis() % 1000) / 500F * MathHelper.PI);
        int color = ((int) (alpha * 255) << 24) | 0x00FF0000;
        renderEntityEsp(entity -> entity instanceof LivingEntity living
                        && (living.isInvisible() || living.hasCustomName()),
                WurstEspStyle.BOXES, WurstBoxSize.ACCURATE, color, -1);
    }
}
