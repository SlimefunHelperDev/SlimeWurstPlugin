package me.matl114.wurstintegration.modules;

import me.matl114.managers.config.Config;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public final class BarrierEspModule extends WurstWorldScannerModule {
    public BarrierEspModule(Config config) {
        super(config, "BarrierESP", "barrier-esp");
    }

    @Override
    protected boolean matchesScannedBlock(BlockPos pos, BlockState state) {
        return state.isOf(Blocks.BARRIER);
    }

    @Override
    protected void collect() {
        renderScanBoxes(WurstChunkArea.A11, 4096, 0xFFFF55FF);
    }
}
