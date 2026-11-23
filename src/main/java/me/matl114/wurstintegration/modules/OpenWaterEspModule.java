package me.matl114.wurstintegration.modules;

import me.matl114.managers.config.Config;
import java.util.List;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class OpenWaterEspModule extends WurstRenderModule {
    public OpenWaterEspModule(Config config) {
        super(config, "OpenWaterESP", "open-water-esp");
    }

    @Override
    protected void collect() {
        FishingBobberEntity bobber = mc.player.fishHook;
        if (bobber == null || bobber.isRemoved()) {
            return;
        }

        BlockPos pos = bobber.getBlockPos();
        Box box = new Box(
                pos.getX() - 2, pos.getY() - 1, pos.getZ() - 2,
                pos.getX() + 3, pos.getY() + 2, pos.getZ() + 3);
        boolean inOpenWater = bobber.isInOpenWater();
        int color = inOpenWater ? 0x8000FF00 : 0x80FF0000;
        boxCollector.submit(box, color);

        if (!inOpenWater) {
            lineCollector.submit(List.of(
                    new Vec3d(box.minX, box.maxY, box.minZ), new Vec3d(box.maxX, box.maxY, box.maxZ)), color);
            lineCollector.submit(List.of(
                    new Vec3d(box.minX, box.maxY, box.maxZ), new Vec3d(box.maxX, box.maxY, box.minZ)), color);
        }
    }
}
