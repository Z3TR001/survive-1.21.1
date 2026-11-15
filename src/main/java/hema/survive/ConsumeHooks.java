package hema.survive;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public final class ConsumeHooks {
    private ConsumeHooks() {}

    public static void onConsume(ServerPlayerEntity p, ItemStack stack) {
        if (p.isCreative() || p.isSpectator()) return;

        if (stack.isOf(SurviveItems.BOILED_WATER)) {
            Thirst.add(p, 800);
        } else if (stack.isOf(Items.POTION)) {
            PotionContentsComponent contents = stack.get(DataComponentTypes.POTION_CONTENTS);
            if (contents != null && contents.matches(Potions.WATER)) {
                Thirst.add(p, 400);
                infect(p, "message.survive.parasites");   // always
            } else {
                Thirst.add(p, 200);
            }
        } else if (stack.isOf(Items.MILK_BUCKET)) {
            Thirst.add(p, 300);
        } else if (stack.isIn(ModTags.HYDRATING_FOODS)) {
            Thirst.add(p, 200);
        }

        if (stack.isIn(ModTags.RISKY_FOODS)) {
            infect(p, "message.survive.bad_food");        // always
        }
    }

    private static void infect(ServerPlayerEntity p, String messageKey) {
        if (p.hasStatusEffect(ModEffects.PARASITES))
            return;
        p.addStatusEffect(new StatusEffectInstance(ModEffects.PARASITES, Tuning.PARASITE_TICKS, 0, false, false, true));
        p.sendMessage(Text.translatable(messageKey), true);
    }
}