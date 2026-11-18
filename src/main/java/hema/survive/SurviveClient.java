package hema.survive;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;


@Environment(EnvType.CLIENT)
public class SurviveClient implements ClientModInitializer {
    private static int thirst = -1;
    private static int drain = 0;


    private static int bodyMode = 0;
    private static KeyBinding bodyKey;

    public static int bodyMode() {
        return bodyMode;
    }

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ThirstPayload.ID, (payload, context) -> {
            thirst = payload.thirst();
            drain = payload.drain();
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> thirst = -1);

        bodyKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.survive.body_hud", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, "key.categories.survive"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (bodyKey.wasPressed()) {
                bodyMode = (bodyMode + 1) % 3;
            }
        });
    }

    public static void render(DrawContext ctx) {
        SurviveHud.render(ctx, thirst, drain, 0);
    }
}