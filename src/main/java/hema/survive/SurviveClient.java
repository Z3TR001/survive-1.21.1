package hema.survive;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import org.lwjgl.glfw.GLFW;


@Environment(EnvType.CLIENT)
public class SurviveClient implements ClientModInitializer {
    private static int thirst = -1;
    private static int drain = 0;

    private static final int SHADE = 0x88000000;
    private static final int RED = 0xFFD83030;


    private static int bodyMode = 0;
    private static KeyBinding bodyKey;

    public static int bodyMode() {
        return bodyMode;
    }

    @Override
    public void onInitializeClient() {

        HudRenderCallback.EVENT.register(SurviveClient::hud);

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

    private static void hud(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity p = mc.player;
        if (p == null || mc.interactionManager == null || mc.options.hudHidden || !mc.interactionManager.hasStatusBars())
            return;

        boolean main = SlotLock.mainBroken(p);
        boolean off = SlotLock.offBroken(p);

        if (!main && !off)
            return;

        int w = ctx.getScaledWindowWidth();
        int h = ctx.getScaledWindowHeight();
        int y = h - 19;

        ctx.getMatrices().push();
        ctx.getMatrices().translate(0f, 0f, 1000f);
        if (main) {
            for (int i = 0; i < 9; i++) {
                drawX(ctx, w / 2 - 88 + i * 20, y);
            }
        }
        if (off) {
            boolean left = p.getMainArm().getOpposite() == Arm.LEFT;
            drawX(ctx, left ? w / 2 - 91 - 26 : w / 2 + 91 + 10, y);
        }
        ctx.getMatrices().pop();
    }

    public static void drawX(DrawContext ctx, int x, int y) {
        ctx.fill(x, y, x + 16, y + 16 , SHADE);
        for (int i = 0; i < 15; i++) {
            ctx.fill(x + i, y + i, Math.min(x + i + 2, x + 16), y + i + 1, RED);
            ctx.fill(Math.max(x + 14 - i , x), y + i, x + 16 - i, y + i, RED);
        }

    }
}