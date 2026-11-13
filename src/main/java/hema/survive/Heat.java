package hema.survive;

import net.minecraft.server.network.ServerPlayerEntity;

public final class Heat {
    private Heat() {

    }

    public static int drainPerSecond(ServerPlayerEntity p) {
        return 2;
    }
}
