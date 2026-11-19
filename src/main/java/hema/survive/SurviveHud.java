package hema.survive;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public final class SurviveHud {
    private SurviveHud() {

    }

    private static final Identifier DROP_FULL = Survive.id("textures/gui/thirst_full.png");
    private static final Identifier DROP_HALF = Survive.id("textures/gui/thirst_half.png");
    private static final Identifier DROP_EMPTY = Survive.id("textures/gui/thirst_empty.png");
    private static final Identifier HEAT_ICON = Survive.id("textures/gui/heat.png");


    private static final int OK = 0, HURT = 1, BLEED = 2;
    private static final String[] STATE_NAMES = {"ok", "hurt", "bleed"};
    private static final Map<String, Identifier> BODY_TEXTURES = new HashMap<>();

    public static void render(DrawContext ctx, int thirst, int drain, int sugar) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.interactionManager == null || mc.options.hudHidden || !mc.interactionManager.hasStatusBars())
            return;

        long now = System.currentTimeMillis();
        RenderSystem.enableBlend();
        ctx.getMatrices().push();
        ctx.getMatrices().translate(0f, 0f, 1000f);

        if (thirst >= 0)
            thirstBar(ctx, mc, player, thirst, drain, now);
        bodyFigure(ctx, player, sugar, now);

        ctx.getMatrices().pop();

    }

    private static void thirstBar(DrawContext ctx, MinecraftClient MC,  ClientPlayerEntity player, int thirst, int drain, long now) {
        int w = ctx.getScaledWindowWidth();
        int h = ctx.getScaledWindowHeight();
        int right = w / 2 + 91;
        int y = h - 49 - (player.getAir() < player.getMaxAir() ? 10 : 0);

        boolean parched = thirst <= Thirst.SPRINT_LIMIT;
        int full = thirst / 200;
        boolean half = thirst % 200 >= 100;
        for (int i = 0; i < 10; i++) {
            int x = right - i * 8 - 9;
            int dy = (parched && (int) ((now / 120 + i) % 4) == 0) ? 1 : 0;
            Identifier tex = i < full ? DROP_FULL : (i == full && half ? DROP_HALF : DROP_EMPTY);
            ctx.drawTexture(tex, x, y + dy, 0f, 0f, 9, 9, 9, 9);
        }

        if (drain >= 5 && (now / 400) % 3 != 0) {
            ctx.drawTexture(HEAT_ICON, right - 94, y, 0f, 0f, 9, 9, 9, 9);
        }
    }

    private static void part(DrawContext ctx, String name, int state, int x, int y, int w, int h) {
        Identifier tex = BODY_TEXTURES.computeIfAbsent(name + "_" + STATE_NAMES[state],
                k -> Survive.id("textures/gui/body/" + k + ".png"));
        ctx.drawTexture(tex, x, y, 0f, 0f, w, h, w, h);
    }

    private static void bodyFigure(DrawContext ctx, ClientPlayerEntity p, int sugar, long now) {
        int mode = SurviveClient.bodyMode();
        if (mode == 2)
            return;

        int head = OK, torso = OK, armRight = OK, armLeft = OK, legRight = OK, legLeft = OK;
        if (p.hasStatusEffect(ModEffects.BROKEN_LEG)) {
            legRight = HURT;
            legLeft = HURT;
        }

        boolean rightIsMixin = p.getMainArm() == Arm.RIGHT;
        if (p.hasStatusEffect(ModEffects.BROKEN_MAIN_HAND)) {
            if (rightIsMixin)
                armRight = HURT;
            else
                armLeft = HURT;
        }
        if (p.hasStatusEffect(ModEffects.BROKEN_OFF_HAND)) {
            if (rightIsMixin)
                armLeft = HURT;
            else
                armRight = HURT;
        }

        boolean injured = head + torso + armRight + armLeft + legRight + legLeft > 0;
        if (mode == 1 && !injured)
            return;

        int x = 8, y = 8;
        ctx.fill(x - 3, y - 3, x + 27, y + 47, 0x55000000);
        part(ctx, "head", head, x + 7, y, 10, 10);
        part(ctx, "torso", torso, x + 6, y + 11, 12, 16);
        part(ctx, "arm_right", armRight, x, y + 11, 5, 16);
        part(ctx, "arm_left", armLeft, x + 19, y + 11, 5, 16);
        part(ctx, "leg_right", legRight, x + 6, y + 28, 5, 16);
        part(ctx, "leg_left", legLeft, x + 13, y + 28, 5, 16);
    }


}
