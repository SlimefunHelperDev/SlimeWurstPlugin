package me.matl114.wurstintegration.modules;

import me.matl114.managers.config.Config;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.hacks.modules.combat.TargetSelector;

public final class PlayerEspModule extends WurstRenderModule {
    private final EnumRef<WurstEspStyle> style = addEnum("style", WurstEspStyle.class, WurstEspStyle.LINES_AND_BOXES);
    private final EnumRef<WurstBoxSize> boxSize = addEnum("box-size", WurstBoxSize.class, WurstBoxSize.FANCY);
    private final FlagRef filterSleeping = addFlag("filter-sleeping", false);
    private final FlagRef filterInvisible = addFlag("filter-invisible", false);
    public PlayerEspModule(Config config) { super(config, "PlayerESP", "player-esp"); }
    @Override protected void collect() {
        for (var entity : mc.world.getPlayers()) {
            if (entity == mc.player || entity == mc.getCameraEntity()
                    || (filterSleeping.get() && entity.isSleeping())
                    || (filterInvisible.get() && entity.isInvisible())) {
                continue;
            }
            if (TargetSelector.INSTANCE != null && TargetSelector.INSTANCE.isInFriendList(entity)) {
                drawEntityEsp(entity, style.get(), boxSize.get(), 0x800000FF);
                continue;
            }
            float distance = mc.player.distanceTo(entity) / 20F;
            int red = (int)(Math.max(0, Math.min(1, 2 - distance)) * 255);
            int green = (int)(Math.max(0, Math.min(1, distance)) * 255);
            drawEntityEsp(entity, style.get(), boxSize.get(), 0x80000000 | (red << 16) | (green << 8));
        }
    }
}
