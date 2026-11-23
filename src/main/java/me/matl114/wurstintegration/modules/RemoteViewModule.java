package me.matl114.wurstintegration.modules;

import me.matl114.managers.config.Config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public final class RemoteViewModule extends WurstRenderModule {
    private Entity previousCameraEntity;
    private Entity remoteViewTarget;
    public RemoteViewModule(Config config) { super(config, "RemoteView", "remote-view"); }
    @Override protected void onWurstEnable() {
        if (checkNull()) { enable.set(false); return; }
        LivingEntity closest = null;
        double closestDistance = Double.MAX_VALUE;
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity living) || living == mc.player || !living.isAlive() || living.isRemoved()) continue;
            double distance = living.squaredDistanceTo(mc.player);
            if (distance < closestDistance) { closest = living; closestDistance = distance; }
        }
        if (closest == null) { enable.set(false); return; }
        previousCameraEntity = mc.getCameraEntity();
        remoteViewTarget = closest;
        mc.setCameraEntity(closest);
    }
    @Override protected void onWurstDisable() {
        if (remoteViewTarget != null && mc.getCameraEntity() == remoteViewTarget) {
            mc.setCameraEntity(previousCameraEntity == null ? mc.player : previousCameraEntity);
        }
        remoteViewTarget = null;
        previousCameraEntity = null;
    }
    @Override protected void collect() {
        if (remoteViewTarget == null || remoteViewTarget.isRemoved()) enable.set(false);
    }
}
