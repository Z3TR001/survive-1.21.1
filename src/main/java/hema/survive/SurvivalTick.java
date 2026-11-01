package hema.survive;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class SurvivalTick {
    private SurvivalTick() {

    }


    public static void register() {
        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) ->
                Thirst.sync(handler.getPlayer(), Heat.drainPerSecond(handler.getPlayer()))));
        ServerTickEvents.END_SERVER_TICK.register(SurvivalTick::tick);
    }

    private static void tick(MinecraftServer server) {

        int now = server.getTicks();
        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            if (p.isCreative() || p.isSpectator() || !p.isAlive())
                continue;


            boolean noSprint = Thirst.get(p) <= Thirst.SPRINT_LIMIT;
            if (p.isSprinting() && noSprint) {
                p.setSprinting(false);
            }
            if ((now + p.getId()) % 20 == 0) {
                everySecond(p);
            }
        }
    }

    private static void everySecond(ServerPlayerEntity p) {
        int drain = Heat.drainPerSecond(p);
        int thirst = Math.max(0, Thirst.get(p) - drain);
        Thirst.set(p, thirst);

        if (thirst <= Thirst.SPRINT_LIMIT) {
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 0, true, false, true));

        }
        if (thirst == 0) {
            p.damage(p.getDamageSources().dryOut(), 1.0f);
        }
        Thirst.sync(p, drain);
    }
}
