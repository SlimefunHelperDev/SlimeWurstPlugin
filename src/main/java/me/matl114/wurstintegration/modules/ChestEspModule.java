package me.matl114.wurstintegration.modules;

import java.util.function.Predicate;
import me.matl114.hacks.utils.config.WrapRGBA;
import me.matl114.managers.config.Config;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.NBTRef;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CrafterBlock;
import net.minecraft.block.DecoratedPotBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.DropperBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.TrappedChestBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class ChestEspModule extends WurstRenderModule {
    private final EnumRef<WurstEspStyle> style = addEnum("style", WurstEspStyle.class, WurstEspStyle.BOXES);

    private final FlagRef includeChests = addFlag("include-normal-chests", true);
    private final NBTRef<WrapRGBA> chestColor = addColor("chest-color", 0xFF00FF00);
    private final FlagRef includeTrappedChests = addFlag("include-trap-chests", true);
    private final NBTRef<WrapRGBA> trappedChestColor = addColor("trap-chest-color", 0xFFFF8000);
    private final FlagRef includeEnderChests = addFlag("include-ender-chests", true);
    private final NBTRef<WrapRGBA> enderChestColor = addColor("ender-color", 0xFF00FFFF);
    private final FlagRef includeChestCarts = addFlag("include-chest-carts", true);
    private final NBTRef<WrapRGBA> chestCartColor = addColor("chest-cart-color", 0xFFFFFF00);
    private final FlagRef includeChestBoats = addFlag("include-chest-boats", true);
    private final NBTRef<WrapRGBA> chestBoatColor = addColor("chest-boat-color", 0xFFFFFF00);
    private final FlagRef includeBarrels = addFlag("include-barrels", true);
    private final NBTRef<WrapRGBA> barrelColor = addColor("barrel-color", 0xFF00FF00);
    private final FlagRef includePots = addFlag("include-pots", false);
    private final NBTRef<WrapRGBA> potColor = addColor("pots-color", 0xFF00FF00);
    private final FlagRef includeShulkers = addFlag("include-shulkers", true);
    private final NBTRef<WrapRGBA> shulkerColor = addColor("shulker-color", 0xFFFF00FF);
    private final FlagRef includeHoppers = addFlag("include-hoppers", false);
    private final NBTRef<WrapRGBA> hopperColor = addColor("hopper-color", 0xFFFFFFFF);
    private final FlagRef includeHopperCarts = addFlag("include-hopper-carts", false);
    private final NBTRef<WrapRGBA> hopperCartColor = addColor("hopper-cart-color", 0xFFFFFF00);
    private final FlagRef includeDroppers = addFlag("include-droppers", false);
    private final NBTRef<WrapRGBA> dropperColor = addColor("dropper-color", 0xFFFFFFFF);
    private final FlagRef includeDispensers = addFlag("include-dispensers", false);
    private final NBTRef<WrapRGBA> dispenserColor = addColor("dispenser-color", 0xFFFF8000);
    private final FlagRef includeCrafters = addFlag("include-crafters", false);
    private final NBTRef<WrapRGBA> crafterColor = addColor("crafter-color", 0xFFFFFFFF);
    private final FlagRef includeFurnaces = addFlag("include-furnaces", false);
    private final NBTRef<WrapRGBA> furnaceColor = addColor("furnace-color", 0xFFFF0000);

    public ChestEspModule(Config config) { super(config, "ChestESP", "chest-esp"); }

    @Override
    protected void collect() {
        collectBlockGroup(includeChests, chestColor, state -> state.getBlock() instanceof net.minecraft.block.ChestBlock
                && !(state.getBlock() instanceof TrappedChestBlock));
        collectBlockGroup(includeTrappedChests, trappedChestColor, state -> state.getBlock() instanceof TrappedChestBlock);
        collectBlockGroup(includeEnderChests, enderChestColor, state -> state.getBlock() instanceof EnderChestBlock);
        collectBlockGroup(includeBarrels, barrelColor, state -> state.getBlock() instanceof BarrelBlock);
        collectBlockGroup(includeShulkers, shulkerColor, state -> state.getBlock() instanceof ShulkerBoxBlock);
        collectBlockGroup(includePots, potColor, state -> state.getBlock() instanceof DecoratedPotBlock);
        collectBlockGroup(includeHoppers, hopperColor, state -> state.getBlock() instanceof HopperBlock);
        collectBlockGroup(includeDroppers, dropperColor, state -> state.getBlock() instanceof DropperBlock);
        collectBlockGroup(includeDispensers, dispenserColor, state -> state.getBlock() instanceof DispenserBlock);
        collectBlockGroup(includeCrafters, crafterColor, state -> state.getBlock() instanceof CrafterBlock);
        collectBlockGroup(includeFurnaces, furnaceColor, state -> state.getBlock() instanceof AbstractFurnaceBlock);
        renderEntityEsp(entity -> entity instanceof ChestMinecartEntity && includeChestCarts.get(), style.get(),
                WurstBoxSize.ACCURATE, chestCartColor.get().color(), -1);
        renderEntityEsp(entity -> entity instanceof ChestBoatEntity && includeChestBoats.get(), style.get(),
                WurstBoxSize.ACCURATE, chestBoatColor.get().color(), -1);
        renderEntityEsp(entity -> entity instanceof HopperMinecartEntity && includeHopperCarts.get(), style.get(),
                WurstBoxSize.ACCURATE, hopperCartColor.get().color(), -1);
    }

    private void collectBlockGroup(FlagRef include, NBTRef<WrapRGBA> color, Predicate<BlockState> filter) {
        if (!include.get()) return;
        int rgb = color.get().color();
        collectNearbyBlocks(8, 8, 4096, (pos, state) -> {
            if (!filter.test(state)) return false;
            if (style.get().hasLines()) tracerCollector.submit(Vec3d.ofCenter(pos), rgb);
            return style.get().hasBoxes();
        }, rgb);
    }
}
