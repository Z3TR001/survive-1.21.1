package hema.survive;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public final class HandRestrictions {
    private HandRestrictions() {

    }

    public static boolean isBroken(PlayerEntity p, Hand hand) {
        return p.hasStatusEffect(hand == Hand.MAIN_HAND ? ModEffects.BROKEN_MAIN_HAND : ModEffects.BROKEN_OFF_HAND);
    }

    private static boolean isBroken(PlayerEntity p) {
        return isBroken(p, Hand.MAIN_HAND) && isBroken(p, Hand.OFF_HAND);
    }

    private static void warn(PlayerEntity p, World w) {
        if (!w.isClient)
            p.sendMessage(Text.translatable("message.survive.hand_broken"), true);
    }

    public static void register() {

        UseBlockCallback.EVENT.register((p, w, hand, hit) -> {
            if (bothBroken(p)) { warn(p, w);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        UseEntityCallback.EVENT.register((p, w, hand, entityh, hit) -> {
            if (bothbroken(p)) {
                warn(p, w);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        AttackEntityCallback.EVENT.register((p, w, hand, pos, dir) -> {
            if (isBroken(p, Hand.MAIN_HAND)) {
                warn(p, w);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        PlayerBlockBreakEvents.BEFORE.register((w, p, pos, state, be) ->
                !isBroken(p, Hand.MAIN_HAND));
    }
}