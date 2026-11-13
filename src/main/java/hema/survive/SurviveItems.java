package hema.survive;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class SurviveItems {
    private SurviveItems() {}

    public static final Item BOILED_WATER = register("boiled_water", new BoiledWaterItem(new Item.Settings().maxCount(16)));



            private static Item register(String name, Item item){
        return Registry.register(Registries.ITEM, Survive.id(name), item);
            }

            public static void init() {
                ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(e -> {
                    e.add(BOILED_WATER);
                });
            }
}
