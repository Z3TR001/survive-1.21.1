package hema.survive.mixin;

import hema.survive.ConsumeHooks;
import hema.survive.HandRestrictions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Inject(method = "getStackInHand", at = @At("HEAD"), cancellable = true)
	private void survive$brokenHand(Hand hand, CallbackInfoReturnable<ItemStack> cir) {
		if ((Object) this instanceof PlayerEntity player && HandRestrictions.isBroken(player, hand)) {
			cir.setReturnValue(ItemStack.EMPTY);
		}
	}

	@Inject(method = "consumeItem", at = @At("HEAD"))
	private void survive$onConsume(CallbackInfo ci) {
		if ((Object) this instanceof ServerPlayerEntity player) {
			ItemStack active = player.getActiveItem();
			if (!active.isEmpty()) {
				ConsumeHooks.onConsume(player, active.copy());
			}
		}
	}
}