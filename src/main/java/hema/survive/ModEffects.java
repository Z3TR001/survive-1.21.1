package hema.survive;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;
import java.util.Set;

public final class ModEffects {
    private ModEffects() {

    }

    private static final Set<RegistryEntry<StatusEffect>> AILMENTS = new HashSet<>();

    public static final RegistryEntry<StatusEffect> PARASITES = reg("parasites", new Parasites());
    public static final RegistryEntry<StatusEffect> BROKEN_LEG = reg("broken_leg", new BrokenLeg());

    public static boolean isAilment(RegistryEntry<StatusEffect> effect) {
        return AILMENTS.contains(effect);
    }

    private static RegistryEntry<StatusEffect> reg(String name, StatusEffect effect) {
        RegistryEntry<StatusEffect> entry = Registry.registerReference(Registries.STATUS_EFFECT, Survive.id(name), effect);
        AILMENTS.add(entry);
        return entry;

    }

    public static void init() {

    }

    static class Parasites extends StatusEffect {
        Parasites() {
            super(StatusEffectCategory.HARMFUL, 0x5C7A2E);
        }

        @Override
        public boolean canApplyUpdateEffect(int duration, int amplifier) {
            return duration % 100 == 0;
        }

        @Override
        public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
            if (entity instanceof ServerPlayerEntity p) {
                p.addExhaustion(1.0f);
                Thirst.add(p, -100);
                if (p.getRandom().nextFloat() < 0.15f) {
                    p.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 0, false, false, true));
                }
            }
            return true;
        }
    }

    static class BrokenLeg extends StatusEffect {
        BrokenLeg() {
            super(StatusEffectCategory.HARMFUL, 0xB6B4A);
            addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, Survive.id("effect.broken_leg_speed"), -0.5,
                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            addAttributeModifier(EntityAttributes.GENERIC_JUMP_STRENGTH, Survive.id("effect.broken_leg_jump"),
                    -0.75, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        }
    }
}