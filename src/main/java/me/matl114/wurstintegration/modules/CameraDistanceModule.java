package me.matl114.wurstintegration.modules;

import me.matl114.managers.config.Config;
import me.matl114.managers.config.DoubleRef;

public final class CameraDistanceModule extends WurstRenderModule {
    public static volatile CameraDistanceModule INSTANCE;

    private final DoubleRef distance = addDouble("distance", 12, -0.5, 150);

    public CameraDistanceModule(Config config) {
        super(config, "CameraDistance", "camera-distance");
        INSTANCE = this;
    }

    public double getDistance() { return distance.get(); }

    @Override protected boolean hasRenderPass() { return false; }
}
