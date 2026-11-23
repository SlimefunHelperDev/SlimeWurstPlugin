package me.matl114.wurstintegration.modules;

import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.managers.config.Config;
import me.matl114.managers.config.FlagRef;

public final class AutoRespawnModule extends BaseModule {
    private final FlagRef enable;
    private Object lastDeathPlayer;

    public AutoRespawnModule(Config config) {
        super("AutoRespawn");
        var root = makePath(config, "wurst.auto-respawn");
        enable = flagBuilder(root.addEnable()).build();
        bindFlag(enable);
    }

    @Override
    public void registerAll() {
        super.registerAll();
        registerListener(Listener.getPostGameTick(), this::onPostGameTick);
    }

    @Override
    public void onDisableModule() {
        super.onDisableModule();
        lastDeathPlayer = null;
    }

    private void onPostGameTick() {
        if (!enable.get() || mc.player == null || mc.getNetworkHandler() == null) {
            lastDeathPlayer = null;
            return;
        }

        if (!mc.player.isDead()) {
            lastDeathPlayer = null;
            return;
        }

        if (lastDeathPlayer == mc.player) {
            return;
        }

        mc.player.requestRespawn();
        mc.setScreen(null);
        lastDeathPlayer = mc.player;
    }
}
