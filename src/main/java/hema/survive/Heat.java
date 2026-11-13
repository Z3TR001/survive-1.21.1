package hema.survive;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public final class Heat {
    private Heat() {

    }

    public static int drainPerSecond(ServerPlayerEntity p) {

        ServerWorld w = p.getServerWorld();
        BlockPos pos = p.getBlockPos();
        float temp = w.getBiome(pos).value().getTemperature();
        boolean sunny = w.isDay() && w.isSkyVisible(pos);

        int d = 2;
        if (w.getDimension().ultrawarm())
            d += 4;
        else if (temp >= 1.5f) d += sunny ? 4 : 2;
        else if (temp >= 1.0f) d += 1;
        if (p.isOnFire()) d += 3;
        if (p.isSprinting()) d += 2;
        if (p.isTouchingWater()) d -= 2;
        if (temp <= 0.15f) d -= 2;
        if (temp <= 0.15f) d -= 1;
        return Math.max(1, d);
    }
}
