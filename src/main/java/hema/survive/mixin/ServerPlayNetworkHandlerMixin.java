// NEW FILE (stage 7b): create this whole file
package hema.survive.mixin;

import hema.survive.SlotLock;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayerEntity player;

    @Inject(method = "onPlayerAction", at = @At("HEAD"), cancellable = true)
    private void survive$lockHandSwap(PlayerActionC2SPacket packet, CallbackInfo ci) {
        if (packet.getAction() == PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND
                && this.player.getServer() != null && this.player.getServer().isOnThread()
                && !this.player.isCreative() && !this.player.isSpectator()
                && SlotLock.anyBroken(this.player)) {
            this.player.sendMessage(Text.translatable("message.survive.hand_broken"), true);
            ci.cancel();
        }
    }
}