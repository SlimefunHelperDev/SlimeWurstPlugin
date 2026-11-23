package me.matl114.wurstintegration.modules;

import me.matl114.managers.config.Config;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.hacks.utils.config.WrapRGBA;
import net.minecraft.entity.ItemEntity;

public final class ItemEspModule extends WurstRenderModule {
    private final EnumRef<WurstEspStyle> style = addEnum("style", WurstEspStyle.class, WurstEspStyle.BOXES);
    private final EnumRef<WurstBoxSize> boxSize = addEnum("box-size", WurstBoxSize.class, WurstBoxSize.FANCY);
    private final NBTRef<WrapRGBA> color = addColor("color", 0xFFFFFF00);
    public ItemEspModule(Config config) { super(config, "ItemESP", "item-esp"); }
    @Override protected void collect() {
        renderEntityEsp(entity -> entity instanceof ItemEntity, style.get(), boxSize.get(), color.get().color(), -1);
    }
}
