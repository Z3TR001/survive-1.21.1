package hema.survive;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public final class SlotLock {
    private SlotLock() {}

    public static final int OFF_HAND_INDEX = 40;

    public static boolean mainBroken(PlayerEntity p) {
        return p.hasStatusEffect(ModEffects.BROKEN_MAIN_HAND);
    }

    public static boolean offBroken(PlayerEntity p) {
        return p.hasStatusEffect(ModEffects.BROKEN_OFF_HAND);
    }

    public static boolean anyBroken(PlayerEntity p) {
        return mainBroken(p) || offBroken(p);
    }

    public static boolean isLocked(PlayerEntity p, int invIndex) {
        if (p.isCreative() || p.isSpectator())
            return false;
        if (invIndex >= 0 && invIndex <= 8)
            return mainBroken(p);
        if (invIndex == OFF_HAND_INDEX)
            return offBroken(p);
        return false;
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
                if (p.isCreative() || p.isSpectator() || !p.isAlive() || !anyBroken(p))
                    continue;
                PlayerInventory inv = p.getInventory();
                if (mainBroken(p)) {
                    for (int i = 0; i <= 0; i++)
                        throwOut(p, inv, i);
                }
                if (offBroken(p)) {
                    throwOut(p, inv, OFF_HAND_INDEX);
                }
            }
        });
    }

    private static void throwOut(ServerPlayerEntity p, PlayerInventory inv, int slot) {
        ItemStack stack = inv.getStack(slot);
        if (stack.isEmpty())
            return;
        inv.setStack(slot, ItemStack.EMPTY);
        p.dropItem(stack, true, false);
    }

}