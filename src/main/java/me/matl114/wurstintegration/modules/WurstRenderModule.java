/*
 * Portions of this module are adapted from Wurst 7.
 * Copyright (c) 2014-2026 Wurst-Imperium and contributors.
 * Licensed under the GNU General Public License, version 3. See LICENSE.txt.
 */
package me.matl114.wurstintegration.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.events.impl.Render3D;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.RenderUtils;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.WrapRGBA;
import me.matl114.hacks.utils.render.RenderCollectors;
import me.matl114.hacks.utils.render.RenderElements;
import me.matl114.managers.config.Config;
import me.matl114.managers.config.ConfigEnum;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.ListRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.config.StringRef;
import me.matl114.utils.render.RenderCollector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

public abstract class WurstRenderModule extends BaseModule {
    protected final Config config;
    protected final ModulePath root;
    public final FlagRef enable;

    protected final RenderCollector<Entity> entityCollector =
            RenderCollectors.createEntityBoxCollector(true, false, false);
    protected final RenderCollector<Box> boxCollector = RenderCollectors.createBoxCollector(true, false, false);
    protected final RenderCollector<Box> overlayCollector = RenderCollectors.createBoxCollector(true, true, false);
    protected final RenderCollector<Vec3d> tracerCollector = RenderCollectors.createTracerCollector();
    protected final RenderCollector<List<Vec3d>> lineCollector = RenderCollectors.createLinesCollector();
    protected final RenderCollector<RenderElements.Text> textCollector = RenderCollectors.createTextCollector();

    private boolean initialized;
    protected WurstRenderModule(Config config, String moduleName, String path) {
        super(moduleName);
        this.config = config;
        root = makePath(config, "wurst." + path);
        enable = flagBuilder(root.addEnable()).build();
        bindFlag(enable);
    }

    protected final FlagRef addFlag(String path, boolean defaultValue) {
        return flagBuilder(root.add(path)).defaultValue(defaultValue).build();
    }

    protected final IntRef addInt(String path, int defaultValue, int min, int max) {
        return intBuilder(root.add(path))
                .defaultValue(defaultValue)
                .validator(value -> value >= min && value <= max)
                .build();
    }

    protected final NBTRef<WrapRGBA> addColor(String path, int defaultValue) {
        return builder(root.add(path), WrapRGBA.class)
                .defaultValue(new WrapRGBA(
                        TextColor.fromRgb(defaultValue & 0x00FFFFFF), (defaultValue >>> 24) & 0xFF))
                .build();
    }

    protected final DoubleRef addDouble(String path, double defaultValue, double min, double max) {
        return doubleBuilder(root.add(path))
                .defaultValue(defaultValue)
                .validator(value -> value >= min && value <= max)
                .build();
    }

    protected final StringRef addString(String path, String defaultValue) {
        return builder(root.add(path), StringRef.TYPE).defaultValue(defaultValue).build();
    }

    protected final ListRef addStringList(String path, List<String> defaultValue) {
        return builder(root.add(path), ListRef.TYPE).defaultValue(defaultValue).build();
    }

    protected final NBTRef<EntrySet<Block>> addBlockSet(String path, List<String> defaultBlockIds) {
        List<Block> blocks = defaultBlockIds.stream()
                .map(Identifier::of)
                .map(Registries.BLOCK::getOrEmpty)
                .flatMap(java.util.Optional::stream)
                .toList();
        return builder(root.add(path), EntrySet.<Block>parameter())
                .defaultValue(new EntrySet<>(Registries.BLOCK, blocks))
                .build();
    }

    protected final <T extends ConfigEnum> EnumRef<T> addEnum(String path, Class<T> type, T defaultValue) {
        return builder(root.add(path), type).defaultValue(defaultValue).build();
    }

    protected boolean hasRenderPass() {
        return true;
    }

    protected void collect() {}

    protected void onWurstEnable() {}

    protected void onWurstDisable() {}

    @Override
    public void registerAll() {
        super.registerAll();
        initialized = true;
        if (hasRenderPass()) {
            registerListener(Listener.getPostGameTick(), this::onPostGameTick);
            registerListener(RenderListener.getRender3DEvent(), this::render3D);
        }
        if (enable.get()) {
            onWurstEnable();
        }
    }

    @Override
    public void onEnableModule() {
        super.onEnableModule();
        if (!initialized) {
            return;
        }
        onWurstEnable();
    }

    @Override
    public void onDisableModule() {
        super.onDisableModule();
        if (!initialized) {
            return;
        }
        onWurstDisable();
    }

    private void onPostGameTick() {
        clearCollectors();
        if (!enable.get() || checkNull() || !hasRenderPass()) {
            return;
        }
        collect();
    }

    private void render3D(Event<Render3D> event) {
        if (!enable.get() || checkNull()) {
            return;
        }
        MatrixStack stack = event.context().stack();
        boolean drawStarted = RenderUtils.startDrawVirtual(stack);
        try {
            float partialTick = event.context().partialTicks();
            entityCollector.render3D(stack, partialTick);
            boxCollector.render3D(stack, partialTick);
            overlayCollector.render3D(stack, partialTick);
            tracerCollector.render3D(stack, partialTick);
            lineCollector.render3D(stack, partialTick);
            textCollector.render3D(stack, partialTick);
        } finally {
            if (drawStarted) {
                RenderUtils.stopDrawVirtual(stack);
            }
        }
    }

    protected final void renderEntityEsp(
            Predicate<Entity> filter,
            WurstEspStyle style,
            WurstBoxSize boxSize,
            int color,
            double maxDistance) {
        MinecraftClient client = MinecraftClient.getInstance();
        for (Entity entity : client.world.getEntities()) {
            if (entity == client.player || entity == client.getCameraEntity() || !filter.test(entity)) {
                continue;
            }
            if (maxDistance > 0 && entity.squaredDistanceTo(client.player) > maxDistance * maxDistance) {
                continue;
            }
            drawEntityEsp(entity, style, boxSize, color);
        }
    }

    protected final void drawEntityEsp(Entity entity, WurstEspStyle style, WurstBoxSize boxSize, int color) {
        if (style.hasBoxes()) {
            if (boxSize == WurstBoxSize.FANCY) {
                boxCollector.submit(entity.getBoundingBox().expand(boxSize.extraSize()), color);
            } else {
                entityCollector.submit(entity, color);
            }
        }
        if (style.hasLines()) {
            tracerCollector.submit(entity.getBoundingBox().getCenter(), color);
        }
    }

    protected final void renderLivingTags(Predicate<Entity> filter, int color, boolean health) {
        MinecraftClient client = MinecraftClient.getInstance();
        for (Entity entity : client.world.getEntities()) {
            if (!(entity instanceof net.minecraft.entity.LivingEntity living)
                    || entity == client.player
                    || entity == client.getCameraEntity()
                    || !filter.test(entity)) {
                continue;
            }
            String label = health ? String.format("%.1f", living.getHealth()) : living.getName().getString();
            Vec3d position = entity.getPos().add(0, entity.getHeight() + 0.25, 0);
            textCollector.submit(new RenderElements.Text(Text.literal(label), position), color);
        }
    }

    protected final void collectNearbyBlocks(
            int horizontalRange,
            int verticalRange,
            int maxMarkers,
            BiPredicate<BlockPos, BlockState> filter,
            int color) {
        BlockPos origin = mc.player.getBlockPos();
        int markers = 0;
        for (int x = origin.getX() - horizontalRange; x <= origin.getX() + horizontalRange; x++) {
            for (int y = Math.max(mc.world.getBottomY(), origin.getY() - verticalRange);
                    y <= Math.min(mc.world.getTopYInclusive(), origin.getY() + verticalRange);
                    y++) {
                for (int z = origin.getZ() - horizontalRange; z <= origin.getZ() + horizontalRange; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = mc.world.getBlockState(pos);
                    if (filter.test(pos, state)) {
                        boxCollector.submit(new Box(pos), color);
                        if (++markers >= maxMarkers) {
                            return;
                        }
                    }
                }
            }
        }
    }

    private void clearCollectors() {
        entityCollector.clear();
        boxCollector.clear();
        overlayCollector.clear();
        tracerCollector.clear();
        lineCollector.clear();
        textCollector.clear();
    }

    public enum WurstEspStyle implements ConfigEnum {
        BOXES(true, false),
        LINES(false, true),
        LINES_AND_BOXES(true, true);

        private final boolean boxes;
        private final boolean lines;

        WurstEspStyle(boolean boxes, boolean lines) {
            this.boxes = boxes;
            this.lines = lines;
        }

        public boolean hasBoxes() { return boxes; }
        public boolean hasLines() { return lines; }
        @Override public String getConfigEnumType() { return "wurst_esp_style"; }
    }

    public enum WurstBoxSize implements ConfigEnum {
        ACCURATE(0), FANCY(0.05);

        private final double extraSize;
        WurstBoxSize(double extraSize) { this.extraSize = extraSize; }
        public double extraSize() { return extraSize; }
        @Override public String getConfigEnumType() { return "wurst_box_size"; }
    }

    public enum WurstChunkArea implements ConfigEnum {
        A3(1), A5(2), A7(3), A9(4), A11(5), A13(6), A15(7), A17(8), A19(9),
        A21(10), A23(11), A25(12), A27(13), A29(14), A31(15), A33(16);

        private final int radius;
        WurstChunkArea(int radius) { this.radius = radius; }
        public int radius() { return radius; }
        public boolean contains(ChunkPos center, ChunkPos pos) {
            return Math.abs(center.x - pos.x) <= radius && Math.abs(center.z - pos.z) <= radius;
        }
        @Override public String getConfigEnumType() { return "wurst_chunk_area"; }
    }

    public enum WurstHitboxCheck implements ConfigEnum {
        OFF, SLOW, UNSTABLE;
        @Override public String getConfigEnumType() { return "wurst_hitbox_check"; }
    }

    public enum WurstAttackFilter implements ConfigEnum {
        ON, IF_CALM, OFF;
        @Override public String getConfigEnumType() { return "wurst_attack_filter"; }
    }

    protected final boolean shouldFilterAttackMode(WurstAttackFilter mode, Entity entity) {
        return mode == WurstAttackFilter.ON
                || mode == WurstAttackFilter.IF_CALM && entity instanceof net.minecraft.entity.mob.Angerable angerable
                        && !angerable.hasAngerTime();
    }
}
