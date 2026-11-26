package hema.survive;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public final class ModSounds {
    private ModSounds() {

    }

    public static final SoundEvent BONE_CRACK = reg("bone_crack");
    public static final SoundEvent STOMACH = reg("stomach");
    public static final SoundEvent BANDAGE = reg("bandage");


    private static SoundEvent reg(String name) {
        Identifier id = Survive.id(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void play(ServerPlayerEntity p, SoundEvent sound, float volume, float pitch) {
        p.getServerWorld().playSound(null, p.getBlockPos(), sound, SoundCategory.PLAYERS, volume, pitch);
    }

    public static void init() {

    }
}
