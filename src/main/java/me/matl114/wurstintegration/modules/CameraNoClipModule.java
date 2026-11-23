package me.matl114.wurstintegration.modules;

import me.matl114.managers.config.Config;

public final class CameraNoClipModule extends WurstRenderModule {
    public static volatile CameraNoClipModule INSTANCE;

    public CameraNoClipModule(Config config) {
        super(config, "CameraNoClip", "camera-no-clip");
        INSTANCE = this;
    }

    @Override protected boolean hasRenderPass() { return false; }
}
