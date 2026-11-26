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

    public static final Item SPLINT = register("splint", new MedicalItem(new Item.Settings().maxCount(16),
            60, UseAction.NONE, ModSounds.BANDAGE,
            p -> p.hasStatusEffect(ModEffects.BROKEN_LEG) || p.hasStatusEffect(ModEffects.BROKEN_MAIN_HAND)
                    || p.hasStatusEffect(ModEffects.BROKEN_OFF_HAND),
            p -> {
                if (p.hasStatusEffect(ModEffects.BROKEN_LEG)) p.removeStatusEffect(ModEffects.BROKEN_LEG);
                else if (p.hasStatusEffect(ModEffects.BROKEN_MAIN_HAND)) p.removeStatusEffect(ModEffects.BROKEN_MAIN_HAND);
                else p.removeStatusEffect(ModEffects.BROKEN_OFF_HAND);
            }));


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
