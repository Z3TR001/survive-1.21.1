package hema.survive.mixin;


import hema.survive.SlotLock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
    @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
    private void survive$lockSwap(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (actionType == SlotActionType.SWAP && SlotLock.isLocked(player, button)) {
            ci.cancel();
        }
    }
}