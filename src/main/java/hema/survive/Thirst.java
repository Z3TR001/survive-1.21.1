package hema.survive;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

public class Thirst {
    private Thirst() {}

    public static final int MAX = 2000;
    public static final int SPRINT_LIMIT = 600;

    public static final AttachmentType<Integer> THIRST = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .initializer(() -> MAX)
            .buildAndRegister(Survive.id("thirst"));

    public static void init () {}

    public static int get(PlayerEntity p) {
        return p.getAttachedOrCreate(THIRST);
    }

    public static void set(PlayerEntity p, int value) {
        p.setAttached(THIRST, MathHelper.clamp(value, 0, MAX));
    }

    public static void add(ServerPlayerEntity p, int delta) {
        set(p, get(p) + delta);
        sync(p, Heat.drainPerSecond(p));
    }

    public static void sync(ServerPlayerEntity p, int drain) {
        ServerPlayNetworking.send(p, new ThirstPayload(get(p), drain));
    }
}
