package hema.survive;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;


@Environment(EnvType.CLIENT)
public class SurviveClient implements ClientModInitializer {
    private static int thirst = -1;
    private static int drain = 0;

    private static final int FULL = 0xFF3FA9F5;
    private static final int EMPTY = 0xFF1B2A38;

    @Override
    public void onInitializeClient() {

        ClientPlayNetworking.registerGlobalReceiver(ThirstPayload.ID, ((payload, context) -> {
            thirst = payload.thirst();
            drain = payload.drain();
        }));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> thirst = -1);
        HudRenderCallback.EVENT.register(SurviveClient::render);

    }

    private static void render(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (thirst < 0 || mc.player == null || mc.interactionManager == null
        || mc.options.hudHidden || !mc.interactionManager.hasStatusBars())
            return;

        int w = ctx.getScaledWindowWidth();
        int h = ctx.getScaledWindowHeight();
        int right = w / 2 + 91;
        int y = h - 49 - (mc.player.getAir() < mc.player.getMaxAir() ? 10 : 0);

        int full = thirst / 200;
        boolean half = thirst % 200 >= 100;
        for (int i = 0; i < 10; i++) {
            int x = right - i * 8 - 9;
            drop(ctx, x, y, EMPTY, false);
            if (i < full)
                drop(ctx, x, y, FULL, false);
            else if (i == full && half)
                drop(ctx, x, y, FULL, true);
        }
        if (drain >= 5) {
            Text hot = Text.translatable("hud.survive.hot");
            ctx.drawText(mc.textRenderer, hot, right - 84 - mc.textRenderer.getWidth(hot), y, 0xFF5555, true);
        }
    }

    private static void drop(DrawContext c, int x, int y, int color, boolean leftHalfOnly) {
        c.fill(x + 3, y, leftHalfOnly ? x + 4 : x + 5, y  + 3, color);
        c.fill(x + 1, y + 3, leftHalfOnly ? x + 4 : x + 7, y + 9, color);
    }
}
