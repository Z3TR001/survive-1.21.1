package hema.survive;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.random.Random;


public class InjuryEvents {
    private InjuryEvents() {

    }
    public static void register() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register(InjuryEvents::afterDamage);
    }

    private static void afterDamage(LivingEntity entity, DamageSource source, float base, float taken, boolean blocked) {
        if (!(entity instanceof ServerPlayerEntity p) || p.isCreative() || p.isSpectator() || taken <= 0f)
            return;
        Random r = p.getRandom();

        if (source.isOf(DamageTypes.FALL) || source.isOf(DamageTypes.FLY_INTO_WALL)) {
            if (taken >= Tuning.LEG_MIN_FALL_DAMAGE && r.nextFloat() < Math.min(0.95f, 0.30f + taken * 0.07f)) {
                breakLeg(p);
            }
            return;
        }


        boolean byMob = source.getAttacker() instanceof LivingEntity;
        boolean projectile = source.isIn(DamageTypeTags.IS_PROJECTILE);
        boolean blast = source.isIn(DamageTypeTags.IS_EXPLOSION);
        boolean  crush = source.isOf(DamageTypes.FALLING_ANVIL) || source.isOf(DamageTypes.FALLING_BLOCK) ||
                source.isOf(DamageTypes.STALAGMITE);

        if ((byMob || projectile || blast || crush) && taken >= Tuning.HAND_MIN_HIT_DAMAGE && r.nextFloat() < Math.min(0.8f, 0.2f + taken * 0.05f)) {
            breakRandomHand(p, r);
        }
    }

    public static boolean breakLeg(ServerPlayerEntity p) {
        if (p.hasStatusEffect(ModEffects.BROKEN_LEG))
            return false;
        p.addStatusEffect(new StatusEffectInstance(ModEffects.BROKEN_LEG, Tuning.LEG_TICKS, 0, false, false, true));
        crack(p, "message.survive.broken_leg");
        return true;
    }

    public static boolean breakHand(ServerPlayerEntity p, boolean main) {
        RegistryEntry<StatusEffect> effect = main ? ModEffects.BROKEN_MAIN_HAND : ModEffects.BROKEN_OFF_HAND;
        if (p.hasStatusEffect(effect))
            return false;

        Hand hand = main ? Hand.MAIN_HAND : Hand.OFF_HAND;
        ItemStack held = p.getStackInHand(hand);
        if (!held.isEmpty()) {
            ItemEntity dropped = p.dropStack(held.copy());
            if (dropped != null)
                dropped.setPickupDelay(80);
            p.setStackInHand(hand, ItemStack.EMPTY);
        }
        p.addStatusEffect(new StatusEffectInstance(effect, Tuning.HAND_TICKS, 0, false, false, true));
        crack(p, main ? "message.survive.broken_main_hand" : "message.survive.broken_off_hand");
        return true;
    }

    private static void breakRandomHand(ServerPlayerEntity p, Random r) {
        boolean main = r.nextBoolean();
        if (!breakHand(p, main))
            breakHand(p, !main);
    }

    private static void crack(ServerPlayerEntity p, String messageKey) {
        p.sendMessage(Text.translatable(messageKey), true);
        ModSounds.play(p, ModSounds.BONE_CRACK, 1.0f, 0.9f + p.getRandom().nextFloat() * 0.2f);
    }
}
