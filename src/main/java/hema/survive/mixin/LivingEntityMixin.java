package hema.survive.mixin;

import hema.survive.ConsumeHooks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Inject(method = "consumeItem", at = @At("HEAD"))
	private void ailments$onConsume(CallbackInfo ci) {
		if ((Object) this instanceof ServerPlayerEntity player) {
			ItemStack active = player.getActiveItem();
			if (!active.isEmpty()) {
				ConsumeHooks.onConsume(player, active.copy());
			}
		}
	}
}