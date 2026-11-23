package me.matl114.wurstintegration.modules;

import me.matl114.managers.config.Config;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.hacks.utils.config.WrapRGBA;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;

public final class CaveFinderModule extends WurstWorldScannerModule {
    private final EnumRef<WurstChunkArea> area = addEnum("area", WurstChunkArea.class, WurstChunkArea.A11);
    private final IntRef limit = addInt("limit", 5, 3, 6);
    private final NBTRef<WrapRGBA> color = addColor("color", 0xFFFF0000);
    private final DoubleRef opacity = addDouble("opacity", 0, 0, 1);

    public CaveFinderModule(Config config) {
        super(config, "CaveFinder", "cave-finder");
    }

    @Override
    protected boolean matchesScannedBlock(BlockPos pos, BlockState state) {
        return state.isAir()
                && pos.getY() < 64
                && mc.world.getLightLevel(pos) <= 3
                && !mc.world.getBlockState(pos.down()).isAir();
    }

    @Override
    protected void collect() {
        int max = (int)Math.pow(10, limit.get());
        int alpha = opacity.get() == 0 ? 0x80 : (int)(opacity.get() * 255) << 24;
        renderScanBoxes(area.get(), max, (color.get().color() & 0x00FFFFFF) | alpha);
    }
}
