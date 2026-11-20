package hema.survive.mixin;


import hema.survive.SlotLock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin {
    @Shadow @Final public Inventory inventory;

    @Shadow
    public abstract int getIndex();

    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    private void survive$lockInsert(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (this.inventory instanceof PlayerInventory pi && SlotLock.isLocked(pi.player, this.getIndex())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canTakeItems", at = @At("HEAD"), cancellable = true)
    private void survive$lockTake(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (this.inventory instanceof PlayerInventory pi && SlotLock.isLocked(pi.player, this.getIndex())) {
            cir.setReturnValue(false);
        }
    }
}