package hema.survive;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class BoiledWaterItem extends Item {
    public BoiledWaterItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public int getMaxUseTime(ItemStack stacks, LivingEntity user) {
        return 32;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        PlayerEntity player = user instanceof PlayerEntity pe ? pe : null;
        if (player != null) {
            player.incrementStat(Stats.USED.getOrCreateStat(this));
            stack.decrementUnlessCreative(1, player);
        }
        if (player == null || !player.isInCreativeMode()) {
            if (stack.isEmpty()) return new ItemStack(Items.GLASS_BOTTLE);
            if (player != null) player.getInventory().insertStack(new ItemStack(Items.GLASS_BOTTLE));
        }
        user.emitGameEvent(GameEvent.DRINK);
        return stack;
    }
}
