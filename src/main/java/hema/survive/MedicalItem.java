package hema.survive;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class MedicalItem extends Item {

    private final int useTicks;
    private final UseAction action;
    private final Predicate<PlayerEntity> canTreat;
    private final Consumer<ServerPlayerEntity> treat;

    public MedicalItem(Settings settings, int useTicks, UseAction action,
                       Predicate<PlayerEntity> canTreat, Consumer<ServerPlayerEntity> treat) {
        super(settings);
        this.useTicks = useTicks;
        this.action = action;
        this.canTreat = canTreat;
        this.treat = treat;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return action;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return useTicks;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!canTreat.test(user)) {
            if (!world.isClient) user.sendMessage(Text.translatable("message.survive.nothing_to_treat"), true);
            return TypedActionResult.fail(stack);
        }
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient && user instanceof ServerPlayerEntity player && canTreat.test(player)) {
            treat.accept(player);
            stack.decrementUnlessCreative(1, player);
        }
        return stack;
    }
}
