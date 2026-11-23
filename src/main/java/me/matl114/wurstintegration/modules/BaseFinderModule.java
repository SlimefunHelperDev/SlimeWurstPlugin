package me.matl114.wurstintegration.modules;

import java.util.List;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.managers.config.Config;
import me.matl114.managers.config.NBTRef;
import me.matl114.hacks.utils.config.WrapRGBA;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;

public final class BaseFinderModule extends WurstWorldScannerModule {
    private final NBTRef<EntrySet<net.minecraft.block.Block>> naturalBlocks = addBlockSet("natural-blocks", List.of(
            "minecraft:acacia_leaves", "minecraft:acacia_log", "minecraft:air", "minecraft:allium",
            "minecraft:amethyst_block", "minecraft:amethyst_cluster", "minecraft:andesite", "minecraft:azure_bluet",
            "minecraft:bedrock", "minecraft:birch_leaves", "minecraft:birch_log", "minecraft:blue_orchid",
            "minecraft:brown_mushroom", "minecraft:brown_mushroom_block", "minecraft:bubble_column",
            "minecraft:budding_amethyst", "minecraft:calcite", "minecraft:cave_air", "minecraft:clay",
            "minecraft:coal_ore", "minecraft:cobweb", "minecraft:copper_ore", "minecraft:cornflower",
            "minecraft:dandelion", "minecraft:dark_oak_leaves", "minecraft:dark_oak_log", "minecraft:dead_bush",
            "minecraft:deepslate", "minecraft:deepslate_coal_ore", "minecraft:deepslate_copper_ore",
            "minecraft:deepslate_diamond_ore", "minecraft:deepslate_emerald_ore", "minecraft:deepslate_gold_ore",
            "minecraft:deepslate_iron_ore", "minecraft:deepslate_lapis_ore", "minecraft:deepslate_redstone_ore",
            "minecraft:diamond_ore", "minecraft:diorite", "minecraft:dirt", "minecraft:dripstone_block",
            "minecraft:emerald_ore", "minecraft:fern", "minecraft:glow_lichen", "minecraft:gold_ore",
            "minecraft:granite", "minecraft:grass", "minecraft:grass_block", "minecraft:gravel", "minecraft:ice",
            "minecraft:infested_stone", "minecraft:iron_ore", "minecraft:jungle_leaves", "minecraft:jungle_log",
            "minecraft:kelp", "minecraft:kelp_plant", "minecraft:lapis_ore", "minecraft:large_amethyst_bud",
            "minecraft:large_fern", "minecraft:lava", "minecraft:lilac", "minecraft:lily_of_the_valley",
            "minecraft:lily_pad", "minecraft:medium_amethyst_bud", "minecraft:mossy_cobblestone",
            "minecraft:mushroom_stem", "minecraft:nether_quartz_ore", "minecraft:netherrack", "minecraft:oak_leaves",
            "minecraft:oak_log", "minecraft:obsidian", "minecraft:orange_tulip", "minecraft:oxeye_daisy",
            "minecraft:peony", "minecraft:pink_tulip", "minecraft:pointed_dripstone", "minecraft:poppy",
            "minecraft:red_mushroom", "minecraft:red_mushroom_block", "minecraft:red_tulip", "minecraft:redstone_ore",
            "minecraft:rose_bush", "minecraft:sand", "minecraft:sandstone", "minecraft:seagrass",
            "minecraft:small_amethyst_bud", "minecraft:smooth_basalt", "minecraft:snow", "minecraft:spawner",
            "minecraft:spruce_leaves", "minecraft:spruce_log", "minecraft:stone", "minecraft:sunflower",
            "minecraft:tall_grass", "minecraft:tall_seagrass", "minecraft:tuff", "minecraft:vine",
            "minecraft:water", "minecraft:white_tulip"));
    private final NBTRef<WrapRGBA> color = addColor("color", 0xFFFF0000);

    public BaseFinderModule(Config config) {
        super(config, "BaseFinder", "base-finder");
        naturalBlocks.addUpdateListener(ignored -> refreshWorldScanner());
    }

    @Override
    protected boolean matchesScannedBlock(BlockPos pos, BlockState state) {
        return !state.isAir() && !naturalBlocks.get().test(state.getBlock());
    }

    @Override
    protected void collect() {
        renderScanBoxes(WurstChunkArea.A11, 10000, color.get().color());
    }
}
