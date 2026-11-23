package me.matl114.wurstintegration.modules;

import java.util.ArrayList;
import java.util.List;
import me.matl114.managers.config.Config;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.hacks.utils.config.WrapRGBA;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;

public final class TrajectoriesModule extends WurstRenderModule {
    private final NBTRef<WrapRGBA> missColor = addColor("miss-color", 0xFF808080);
    private final NBTRef<WrapRGBA> entityHitColor = addColor("entity-hit-color", 0xFFFF0000);
    private final NBTRef<WrapRGBA> blockHitColor = addColor("block-hit-color", 0xFF00FF00);
    public TrajectoriesModule(Config config) { super(config, "Trajectories", "trajectories"); }
    @Override protected void collect() {
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof ProjectileEntity projectile)) continue;
            Vec3d position = projectile.getPos();
            Vec3d velocity = projectile.getVelocity();
            List<Vec3d> path = new ArrayList<>(32);
            path.add(position);
            for (int tick = 0; tick < 30; tick++) {
                position = position.add(velocity);
                velocity = velocity.multiply(0.99).add(0, -0.03, 0);
                path.add(position);
            }
            lineCollector.submit(path, missColor.get().color());
        }
    }
}
