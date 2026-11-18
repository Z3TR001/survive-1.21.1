package hema.survive;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
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
    }

    public static boolean breakLeg(ServerPlayerEntity p) {
        if (p.hasStatusEffect(ModEffects.BROKEN_LEG))
            return false;
        p.addStatusEffect(new StatusEffectInstance(ModEffects.BROKEN_LEG, Tuning.LEG_TICKS, 0, false, false, true));
        crack(p, "message.survive.broken_leg");
        return true;
    }

    private static void crack(ServerPlayerEntity p, String messageKey) {
        p.sendMessage(Text.translatable(messageKey), true);
        ModSounds.play(p, ModSounds.BONE_CRACK, 1.0f, 0.9f + p.getRandom().nextFloat() * 0.2f);
    }
}
