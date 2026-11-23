/*
 * Portions of this module are adapted from Wurst 7.
 * Copyright (c) 2014-2026 Wurst-Imperium and contributors.
 * Licensed under the GNU General Public License, version 3. See LICENSE.txt.
 */
package me.matl114.wurstintegration.modules;

import me.matl114.hacks.api.ModuleGroup;
import me.matl114.managers.config.Config;

public final class WurstRenderModules {
    private WurstRenderModules() {}

    public static void register(ModuleGroup group, Config config) {
        group.registerFactories(manager -> new BarrierEspModule(config).register(manager));
        group.registerFactories(manager -> new BaseFinderModule(config).register(manager));
        group.registerFactories(manager -> new CameraDistanceModule(config).register(manager));
        group.registerFactories(manager -> new CameraNoClipModule(config).register(manager));
        group.registerFactories(manager -> new CaveFinderModule(config).register(manager));
        group.registerFactories(manager -> new ChestEspModule(config).register(manager));
        group.registerFactories(manager -> new FullbrightModule(config).register(manager));
        group.registerFactories(manager -> new ItemEspModule(config).register(manager));
        group.registerFactories(manager -> new MobEspModule(config).register(manager));
        group.registerFactories(manager -> new MobSpawnEspModule(config).register(manager));
        group.registerFactories(manager -> new OpenWaterEspModule(config).register(manager));
        group.registerFactories(manager -> new OverlayModule(config).register(manager));
        group.registerFactories(manager -> new PlayerEspModule(config).register(manager));
        group.registerFactories(manager -> new PortalEspModule(config).register(manager));
        group.registerFactories(manager -> new ProphuntEspModule(config).register(manager));
        group.registerFactories(manager -> new RemoteViewModule(config).register(manager));
        group.registerFactories(manager -> new TrajectoriesModule(config).register(manager));
        group.registerFactories(manager -> new AutoRespawnModule(config).register(manager));
        group.registerFactories(manager -> new TriggerBotModule(config).register(manager));
    }

}
