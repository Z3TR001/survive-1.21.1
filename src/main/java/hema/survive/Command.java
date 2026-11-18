package hema.survive;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public final class Command {
    private Command() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("survive")
                        .requires(src -> src.hasPermissionLevel(2))
                        .then(CommandManager.literal("status").executes(Command::status))
                        .then(CommandManager.literal("thirst")
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(0, Thirst.MAX))
                                                .executes(ctx -> {
                                                    ServerPlayerEntity p = ctx.getSource().getPlayerOrThrow();
                                                    Thirst.set(p, IntegerArgumentType.getInteger(ctx, "value"));
                                                    Thirst.sync(p, Heat.drainPerSecond(p));
                                                    int now = Thirst.get(p);
                                                    ctx.getSource().sendFeedback(() -> Text.literal("Thirst set to " + now), false);
                                                    return 1;
                                                }))))
                        .then(CommandManager.literal("break")
                                .then(CommandManager.literal("leg").executes(ctx -> {
                                    boolean ok = InjuryEvents.breakLeg(ctx.getSource().getPlayerOrThrow());
                                    ctx.getSource().sendFeedback(() -> Text.literal(ok ? "Leg broken" : "Already broken"), false);
                                    return 1;
                                }))
                                .then(CommandManager.literal("mainhand").executes(ctx -> {
                                    boolean ok = InjuryEvents.breakHand(ctx.getSource().getPlayerOrThrow(), true);
                                    ctx.getSource().sendFeedback(() -> Text.literal(ok ? "Main hand broken" : "Already Broken"), false);
                                    return 1;
                                }))
                                .then(CommandManager.literal("offhand").executes(ctx -> {
                                    boolean ok = InjuryEvents.breakHand(ctx.getSource().getPlayerOrThrow(), false);
                                    ctx.getSource().sendFeedback(() -> Text.literal(ok ? "Off hand broken" : "Already broken"), false);
                                    return 1;
                                })))
                ));
    }

    private static int status(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity p = ctx.getSource().getPlayerOrThrow();
        String msg = "Thirst " + Thirst.get(p) + "/" + Thirst.MAX + ", drain " + Heat.drainPerSecond(p) + "/s";
        String finalMsg = msg;
        ctx.getSource().sendFeedback(() -> Text.literal(finalMsg), false);
        return 1;
    }
}