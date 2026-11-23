package me.matl114.wurstintegration.modules;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.WorldTasks;
import me.matl114.managers.config.Config;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;

public abstract class WurstWorldScannerModule extends WurstRenderModule {
    protected final Map<ChunkPos, Map<BlockPos, BlockState>> scannedBlocks = new ConcurrentHashMap<>();

    private boolean scannerInitialized;
    private boolean scannerRefreshPending;

    protected WurstWorldScannerModule(Config config, String moduleName, String path) {
        super(config, moduleName, path);
    }

    protected abstract boolean matchesScannedBlock(BlockPos pos, BlockState state);

    @Override
    public void registerAll() {
        super.registerAll();
        registerListener(Listener.getPreWorldScannListener(), this::requestWorldScan);
        registerListener(Listener.getResetWorldScannListener(), this::resetWorldScan);
        registerListener(Listener.getWorldScannChunkBlockFilterList(), this::addWorldScanFilter);
        registerListener(Listener.getWorldScannChunkResult(), this::onChunkScanResult);
        registerListener(Listener.getWorldScannBlockResult(), this::onBlockScanResult);
        registerListener(Listener.getPostGameTick(), this::onScannerPostGameTick);
        scannerInitialized = true;
        if (enable.get()) {
            refreshWorldScanner();
        }
    }

    @Override
    public void onEnableModule() {
        super.onEnableModule();
        if (scannerInitialized) {
            refreshWorldScanner();
        }
    }

    @Override
    public void onDisableModule() {
        super.onDisableModule();
        if (scannerInitialized) {
            scannedBlocks.clear();
            refreshWorldScanner();
        }
    }

    protected final void refreshWorldScanner() {
        scannerRefreshPending = true;
        if (!checkNull()) {
            scannerRefreshPending = false;
            WorldTasks.restartWorldScanner();
        }
    }

    protected final void renderScanBoxes(WurstChunkArea area, int limit, int color) {
        if (checkNull()) {
            return;
        }
        ChunkPos center = mc.player.getChunkPos();
        int count = 0;
        for (var chunkEntry : scannedBlocks.entrySet()) {
            if (!area.contains(center, chunkEntry.getKey())) {
                continue;
            }
            for (BlockPos pos : chunkEntry.getValue().keySet()) {
                if (count++ >= limit) {
                    return;
                }
                boxCollector.submit(new Box(pos), color);
            }
        }
    }

    private void onScannerPostGameTick() {
        if (scannerRefreshPending && enable.get() && !checkNull()) {
            scannerRefreshPending = false;
            WorldTasks.restartWorldScanner();
        }
    }

    private void requestWorldScan(Event<Boolean> event) {
        if (enable.get()) {
            event.context(Boolean.TRUE);
        }
    }

    private void resetWorldScan() {
        scannedBlocks.clear();
    }

    private void addWorldScanFilter(Event<List<BiPredicate<BlockPos, BlockState>>> event) {
        if (enable.get()) {
            event.context.add(this::matchesScannedBlock);
        }
    }

    private void onChunkScanResult(Event<Map<BlockPos, BlockState>> event) {
        if (!enable.get()) {
            return;
        }
        ChunkPos chunkPos = event.getArgs(0);
        Map<BlockPos, BlockState> filtered = new ConcurrentHashMap<>();
        event.context.forEach((pos, state) -> {
            if (matchesScannedBlock(pos, state)) {
                filtered.put(pos, state);
            }
        });
        if (filtered.isEmpty()) {
            scannedBlocks.remove(chunkPos);
        } else {
            scannedBlocks.put(chunkPos, filtered);
        }
    }

    private void onBlockScanResult(Event<BlockState> event) {
        if (!enable.get()) {
            return;
        }
        BlockPos pos = event.getArgs(0);
        ChunkPos chunkPos = event.getArgs(1);
        Map<BlockPos, BlockState> chunk = scannedBlocks.computeIfAbsent(chunkPos, ignored -> new ConcurrentHashMap<>());
        if (matchesScannedBlock(pos, event.context)) {
            chunk.put(pos, event.context);
        } else {
            chunk.remove(pos);
            if (chunk.isEmpty()) {
                scannedBlocks.remove(chunkPos, chunk);
            }
        }
    }
}
