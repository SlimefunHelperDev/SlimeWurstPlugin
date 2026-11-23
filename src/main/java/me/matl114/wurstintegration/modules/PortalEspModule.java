package me.matl114.wurstintegration.modules;

import me.matl114.managers.config.Config;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.hacks.utils.config.WrapRGBA;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

public final class PortalEspModule extends WurstWorldScannerModule {
    private final EnumRef<WurstEspStyle> style = addEnum("style", WurstEspStyle.class, WurstEspStyle.BOXES);
    private final EnumRef<WurstChunkArea> area = addEnum("area", WurstChunkArea.class, WurstChunkArea.A11);
    private final NBTRef<WrapRGBA> netherColor = addColor("nether-portal-color", 0xFFFF0000);
    private final NBTRef<WrapRGBA> endColor = addColor("end-portal-color", 0xFF00FF00);
    private final NBTRef<WrapRGBA> frameColor = addColor("end-portal-frame-color", 0xFF0000FF);
    private final NBTRef<WrapRGBA> gatewayColor = addColor("end-gateway-color", 0xFFFFFF00);
    private final FlagRef includeNether = addFlag("include-nether-portals", true);
    private final FlagRef includeEnd = addFlag("include-end-portals", true);
    private final FlagRef includeFrame = addFlag("include-end-portal-frames", true);
    private final FlagRef includeGateway = addFlag("include-end-gateways", true);

    public PortalEspModule(Config config) {
        super(config, "PortalESP", "portal-esp");
        includeNether.addUpdateListener(ignored -> refreshWorldScanner());
        includeEnd.addUpdateListener(ignored -> refreshWorldScanner());
        includeFrame.addUpdateListener(ignored -> refreshWorldScanner());
        includeGateway.addUpdateListener(ignored -> refreshWorldScanner());
    }

    @Override
    protected boolean matchesScannedBlock(BlockPos pos, BlockState state) {
        return state.isOf(Blocks.NETHER_PORTAL) && includeNether.get()
                || state.isOf(Blocks.END_PORTAL) && includeEnd.get()
                || state.isOf(Blocks.END_PORTAL_FRAME) && includeFrame.get()
                || state.isOf(Blocks.END_GATEWAY) && includeGateway.get();
    }

    @Override
    protected void collect() {
        ChunkPos center = mc.player.getChunkPos();
        for (var entry : scannedBlocks.entrySet()) {
            if (!area.get().contains(center, entry.getKey())) {
                continue;
            }
            for (var block : entry.getValue().entrySet()) {
                BlockPos pos = block.getKey();
                int color = colorFor(block.getValue());
                if (style.get().hasBoxes()) {
                    boxCollector.submit(new net.minecraft.util.math.Box(pos), color);
                }
                if (style.get().hasLines()) {
                    tracerCollector.submit(Vec3d.ofCenter(pos), color);
                }
            }
        }
    }

    private int colorFor(BlockState state) {
        if (state.isOf(Blocks.NETHER_PORTAL)) return netherColor.get().color();
        if (state.isOf(Blocks.END_PORTAL)) return endColor.get().color();
        if (state.isOf(Blocks.END_PORTAL_FRAME)) return frameColor.get().color();
        return gatewayColor.get().color();
    }
}
