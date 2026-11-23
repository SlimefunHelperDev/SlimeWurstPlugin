package me.matl114.wurstintegration.modules;

import me.matl114.managers.config.Config;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.EnumRef;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;
import me.matl114.managers.config.NBTRef;
import me.matl114.hacks.utils.config.WrapRGBA;

public final class MobSpawnEspModule extends WurstWorldScannerModule {
    private final EnumRef<WurstChunkArea> drawDistance = addEnum("draw-distance", WurstChunkArea.class, WurstChunkArea.A9);
    private final NBTRef<WrapRGBA> nightColor = addColor("night-color", 0xFFFFFF00);
    private final NBTRef<WrapRGBA> dayColor = addColor("day-color", 0xFFFF0000);
    private final DoubleRef opacity = addDouble("opacity", 0.5, 0, 1);
    private final FlagRef depthTest = addFlag("depth-test", true);
    private final EnumRef<WurstHitboxCheck> hitboxCheck = addEnum("hitbox-check", WurstHitboxCheck.class, WurstHitboxCheck.OFF);

    public MobSpawnEspModule(Config config) {
        super(config, "MobSpawnESP", "mob-spawn-esp");
    }

    @Override
    protected boolean matchesScannedBlock(BlockPos pos, BlockState state) {
        return state.isAir()
                && mc.world.getLightLevel(pos) <= 7
                && !mc.world.getBlockState(pos.down()).isAir()
                && checkHitbox(pos);
    }

    @Override
    protected void collect() {
        int alpha = (int)(opacity.get() * 255) << 24;
        int color = (mc.world.getTimeOfDay() % 24000L < 12000L
                ? dayColor.get().color()
                : nightColor.get().color());
        renderScanBoxes(drawDistance.get(), 4096, (color & 0x00FFFFFF) | alpha);
    }

    private boolean checkHitbox(BlockPos pos) {
        if (hitboxCheck.get() == WurstHitboxCheck.OFF) return true;
        Box spawnBox = EntityType.CREEPER.getSpawnBox(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        return hitboxCheck.get() == WurstHitboxCheck.SLOW
                ? isSpaceEmptySlow(spawnBox)
                : mc.world.isSpaceEmpty(spawnBox);
    }

    private static synchronized boolean isSpaceEmptySlow(Box box) {
        return mc.world.isSpaceEmpty(box);
    }
}
