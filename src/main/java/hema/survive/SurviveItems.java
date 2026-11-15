package hema.survive;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.UseAction;

public class SurviveItems {
    private SurviveItems() {}

    public static final Item BOILED_WATER = register("boiled_water", new BoiledWaterItem(new Item.Settings().maxCount(16)));

    public static final Item ANTIPARASITIC = register("antiparasitic", new MedicalItem(new Item.Settings()
            .maxCount(8), 32, UseAction.DRINK,

            p -> p.hasStatusEffect(ModEffects.PARASITES),
            p -> p.removeStatusEffect(ModEffects.PARASITES)));



            private static Item register(String name, Item item){
        return Registry.register(Registries.ITEM, Survive.id(name), item);
            }

            public static void init() {
                ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(e -> {
                    e.add(BOILED_WATER);
                    e.add(ANTIPARASITIC);
                });
            }
}
