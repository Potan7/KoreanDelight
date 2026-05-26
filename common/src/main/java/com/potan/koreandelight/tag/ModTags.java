package com.potan.koreandelight.tag;

import com.potan.koreandelight.Koreandelight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
    public static final TagKey<Item> KIMJANG_VEGETABLES = forgeTag("vegetables");
    public static final TagKey<Item> KIMJANG_SEASONINGS = modTag("kimjang_seasonings");

    private static TagKey<Item> modTag(String name) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath(Koreandelight.MODID, name));
    }

    private static TagKey<Item> forgeTag(String name) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", name));
    }
}
