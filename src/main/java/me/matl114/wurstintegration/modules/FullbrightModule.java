package me.matl114.wurstintegration.modules;

import me.matl114.managers.config.Config;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.FlagRef;
import net.minecraft.client.option.SimpleOption;

public final class FullbrightModule extends WurstRenderModule {
    private final FlagRef fade = addFlag("fade", true);
    private final DoubleRef defaultGamma = addDouble("default-gamma", 0.5, 0, 1);
    private Double originalGamma;

    public FullbrightModule(Config config) {
        super(config, "Fullbright", "fullbright");
    }

    @Override
    protected boolean hasRenderPass() { return false; }

    @Override
    protected void onWurstEnable() {
        SimpleOption<Double> gamma = mc.options.getGamma();
        if (originalGamma == null) {
            originalGamma = gamma.getValue();
        }
        gamma.setValue(16.0);
    }

    @Override
    protected void onWurstDisable() {
        if (originalGamma != null) {
            mc.options.getGamma().setValue(defaultGamma.get());
            originalGamma = null;
        }
    }
}
