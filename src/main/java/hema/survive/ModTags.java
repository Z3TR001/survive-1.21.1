package hema.survive;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModTags {
    private ModTags() {}

    public static final TagKey<Item> HYDRATING_FOODS = TagKey.of(RegistryKeys.ITEM, Survive.id("hydrating_foods"));
    public static final TagKey<Item> RISKY_FOODS = TagKey.of(RegistryKeys.ITEM, Survive.id("risky_foods"));
}
