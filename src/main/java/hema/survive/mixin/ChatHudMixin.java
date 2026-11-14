package hema.survive.mixin;

import hema.survive.SurviveClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;IIIZ)V", at = @At("RETURN"))
    private void survive$afterChat(DrawContext ctx, int currentTick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
        SurviveClient.render(ctx);
    }
}