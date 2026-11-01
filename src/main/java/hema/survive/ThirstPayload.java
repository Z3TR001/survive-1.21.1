package hema.survive;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record ThirstPayload(int thirst, int drain) implements CustomPayload {
    public static final Id<ThirstPayload> ID = new Id<>(Survive.id("thirst"));
    public static final PacketCodec<RegistryByteBuf, ThirstPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, ThirstPayload::thirst,
            PacketCodecs.VAR_INT, ThirstPayload::drain,
            ThirstPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(ID, CODEC);
    }
}
