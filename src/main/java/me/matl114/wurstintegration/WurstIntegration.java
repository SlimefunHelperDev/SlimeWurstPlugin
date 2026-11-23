package me.matl114.wurstintegration;

import me.matl114.hacks.api.ModuleGroup;
import me.matl114.managers.config.Config;

import me.matl114.plugins.SlimefunHelperPlugin;
import me.matl114.wurstintegration.modules.WurstRenderModules;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public final class WurstIntegration implements SlimefunHelperPlugin {
    public static final String MOD_ID = "slimefunhelperwurstintegration";
    public static Config WURST_INTEGRATION;

    @Override
    public void onConfigBootstrap() {
        WURST_INTEGRATION = registerConfig(
                loadRuntimeConfig("wurst-intergation.yml", "Wurst integration settings"));
    }

    @Override
    public void onListenerBootstrap() {}

    @Override
    public void onModuleBootstrap() {
        ModuleGroup group = new ModuleGroup("Wurst");
        WurstRenderModules.register(group, WURST_INTEGRATION);
        registerModuleGroup(group);
    }

    @Override
    public void postInitialize() {}

    @Override
    public String getPluginId() {
        return MOD_ID;
    }

    @Override
    public ModContainer getContainer() {
        return FabricLoader.getInstance()
                .getModContainer(MOD_ID)
                .orElseThrow(() -> new IllegalStateException("Wurst Integration mod container not found."));
    }
}
