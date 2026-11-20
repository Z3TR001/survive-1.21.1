package hema.survive.mixin;

import hema.survive.SlotLock;
import hema.survive.SurviveClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    @Inject(method = "drawSlot", at = @At("TAIL"))
    private void survive$drawLockedX(DrawContext context, Slot slot, CallbackInfo ci) {
        if (slot.inventory instanceof PlayerInventory pi && SlotLock.isLocked(pi.player, slot.getIndex())) {
            context.getMatrices().push();
            context.getMatrices().translate(0f, 0f, 300f);
            SurviveClient.drawX(context, slot.x, slot.y);
            context.getMatrices().pop();
        }
    }
}