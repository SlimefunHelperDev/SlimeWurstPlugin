package me.matl114.wurstintegration.modules;

import me.matl114.managers.config.Config;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public final class MobEspModule extends WurstRenderModule {
    private final EnumRef<WurstEspStyle> style = addEnum("style", WurstEspStyle.class, WurstEspStyle.BOXES);
    private final EnumRef<WurstBoxSize> boxSize = addEnum("box-size", WurstBoxSize.class, WurstBoxSize.FANCY);
    private final FlagRef filterInvisible = addFlag("filter-invisible", false);
    private final FlagRef filterHostile = addFlag("filter-hostile", false);
    private final EnumRef<WurstAttackFilter> filterNeutral = addEnum("filter-neutral", WurstAttackFilter.class, WurstAttackFilter.OFF);
    private final FlagRef filterPassive = addFlag("filter-passive", false);
    private final FlagRef filterWater = addFlag("filter-passive-water", false);
    private final FlagRef filterBats = addFlag("filter-bats", false);
    private final FlagRef filterSlimes = addFlag("filter-slimes", false);
    private final FlagRef filterPets = addFlag("filter-pets", false);
    private final FlagRef filterVillagers = addFlag("filter-villagers", false);
    private final FlagRef filterZombieVillagers = addFlag("filter-zombie-villagers", false);
    private final FlagRef filterGolems = addFlag("filter-golems", false);
    private final EnumRef<WurstAttackFilter> filterPiglins = addEnum("filter-piglins", WurstAttackFilter.class, WurstAttackFilter.OFF);
    private final EnumRef<WurstAttackFilter> filterZombiePiglins = addEnum("filter-zombie-piglins", WurstAttackFilter.class, WurstAttackFilter.OFF);
    private final EnumRef<WurstAttackFilter> filterEndermen = addEnum("filter-endermen", WurstAttackFilter.class, WurstAttackFilter.OFF);
    private final FlagRef filterShulkers = addFlag("filter-shulkers", false);
    private final FlagRef filterAllays = addFlag("filter-allays", false);
    private final FlagRef filterNamed = addFlag("filter-named", false);
    private final FlagRef filterArmorStands = addFlag("filter-armor-stands", false);
    public MobEspModule(Config config) { super(config, "MobESP", "mob-esp"); }
    @Override protected void collect() {
        for (Entity entity : mc.world.getEntities()) {
            String id = net.minecraft.registry.Registries.ENTITY_TYPE.getId(entity.getType()).getPath();
            if (!(entity instanceof MobEntity || id.equals("armor_stand")) || isFilteredEntity(entity)) {
                continue;
            }
            float distance = mc.player.distanceTo(entity) / 20F;
            int red = (int) (MathHelper.clamp(2 - distance, 0, 1) * 255);
            int green = (int) (MathHelper.clamp(distance, 0, 1) * 255);
            int color = 0x80000000 | (red << 16) | (green << 8);
            drawEntityEsp(entity, style.get(), boxSize.get(), color);
        }
    }
    private boolean isFilteredEntity(Entity entity) {
        String id = net.minecraft.registry.Registries.ENTITY_TYPE.getId(entity.getType()).getPath();
        if (filterInvisible.get() && entity.isInvisible()) return true;
        if (filterNamed.get() && entity.hasCustomName()) return true;
        if (filterArmorStands.get() && id.equals("armor_stand")) return true;
        if (filterHostile.get() && entity instanceof net.minecraft.entity.mob.HostileEntity) return true;
        if (entity instanceof net.minecraft.entity.mob.Angerable
                && shouldFilterAttackMode(filterNeutral.get(), entity)) return true;
        if (filterPassive.get() && entity instanceof MobEntity
                && !(entity instanceof net.minecraft.entity.mob.HostileEntity)) return true;
        if (filterWater.get() && (id.contains("fish") || id.contains("dolphin") || id.contains("squid"))) return true;
        if (filterBats.get() && id.equals("bat")) return true;
        if (filterSlimes.get() && (id.contains("slime") || id.equals("magma_cube"))) return true;
        if (filterPets.get() && (id.contains("wolf") || id.contains("cat") || id.contains("parrot"))) return true;
        if (filterVillagers.get() && (id.contains("villager") || id.equals("wandering_trader"))) return true;
        if (filterZombieVillagers.get() && id.equals("zombie_villager")) return true;
        if (filterGolems.get() && id.contains("golem")) return true;
        if (id.equals("piglin") && shouldFilterAttackMode(filterPiglins.get(), entity)) return true;
        if (id.equals("zombified_piglin") && shouldFilterAttackMode(filterZombiePiglins.get(), entity)) return true;
        if (id.equals("enderman") && shouldFilterAttackMode(filterEndermen.get(), entity)) return true;
        if (filterShulkers.get() && id.equals("shulker")) return true;
        return filterAllays.get() && id.equals("allay");
    }
}
