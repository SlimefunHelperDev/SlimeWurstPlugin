package me.matl114.wurstintegration.modules;

import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.managers.config.Config;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.FlagRef;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;

public final class TriggerBotModule extends BaseModule {
    private final FlagRef enable;
    private final DoubleRef range;
    private final FlagRef attackWhileBlocking;

    public TriggerBotModule(Config config) {
        super("TriggerBot");
        var root = makePath(config, "wurst.trigger-bot");
        enable = flagBuilder(root.addEnable()).build();
        bindFlag(enable);
        range = doubleBuilder(root.add("range"))
                .defaultValue(4.25)
                .validator(value -> value >= 1 && value <= 6)
                .build();
        attackWhileBlocking = flagBuilder(root.add("attack-while-blocking"))
                .defaultValue(false)
                .build();
    }

    @Override
    public void registerAll() {
        super.registerAll();
        registerListener(Listener.getPreHandleInputEvents(), this::onHandleInput);
    }

    private void onHandleInput() {
        if (!enable.get() || mc.player == null || mc.world == null || mc.interactionManager == null
                || mc.currentScreen != null) {
            return;
        }

        if (!attackWhileBlocking.get() && mc.player.isUsingItem()) {
            return;
        }

        if (mc.player.getAttackCooldownProgress(0.5F) < 1.0F
                || !(mc.crosshairTarget instanceof EntityHitResult hit)) {
            return;
        }

        Entity target = hit.getEntity();
        if (target == mc.player || target.isSpectator() || !target.isAttackable() || !target.canHit()
                || mc.player.squaredDistanceTo(target) > range.get() * range.get()) {
            return;
        }

        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
    }
}
