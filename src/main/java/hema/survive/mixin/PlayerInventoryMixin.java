package hema.survive.mixin;

import hema.survive.SlotLock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow @Final public PlayerEntity player;
    @Shadow @Final public DefaultedList<ItemStack> main;

    @Shadow
    protected abstract boolean canStackAddMore(ItemStack existingStack, ItemStack stack);

    @Inject(method = "getEmptySlot", at = @At("RETURN"), cancellable = true)
    private void survive$skipLockedEmpty(CallbackInfoReturnable<Integer> cir) {
        int slot = cir.getReturnValue();
        if (slot >= 0 && SlotLock.isLocked(this.player, slot)) {
            for (int j = 0; j < this.main.size(); j++) {
                if (!SlotLock.isLocked(this.player, j) && this.main.get(j).isEmpty()) {
                    cir.setReturnValue(j);
                    return;
                }
            }
            cir.setReturnValue(-1);
        }
    }

    @Inject(method = "getOccupiedSlotWithRoomForStack", at = @At("RETURN"), cancellable = true)
    private void survive$skipLockedMerge(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        int slot = cir.getReturnValue();
        if (slot >= 0 && SlotLock.isLocked(this.player, slot)) {
            for (int j = 0; j < this.main.size(); j++) {
                if (!SlotLock.isLocked(this.player, j) && this.canStackAddMore(this.main.get(j), stack)) {
                    cir.setReturnValue(j);
                    return;
                }
            }
            cir.setReturnValue(-1);
        }
    }
}