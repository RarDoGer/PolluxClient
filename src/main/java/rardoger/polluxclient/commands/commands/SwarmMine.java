package rardoger.polluxclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import rardoger.polluxclient.commands.Command;
import rardoger.polluxclient.modules.ModuleManager;
import rardoger.polluxclient.modules.combat.Swarm;
import rardoger.polluxclient.utils.player.Chat;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class SwarmMine extends Command {

    public SwarmMine() {
        super("s", "(highlight)mine <playername>(default) - Baritone Mine A Block");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("mine")
                .then(argument("block", BlockStateArgumentType.blockState())
                        .executes(context -> {
                            try {
                                Swarm swarm = ModuleManager.INSTANCE.get(Swarm.class);
                                if (swarm.isActive()) {
                                    if (swarm.currentMode == Swarm.Mode.Queen && swarm.server != null)
                                        swarm.server.sendMessage(context.getInput());
                                    if (swarm.currentMode != Swarm.Mode.Queen) {
                                        swarm.targetBlock = context.getArgument("block",BlockStateArgument.class).getBlockState();
                                    } else Chat.info("Null block");
                                }
                            } catch (Exception e) {
                                Chat.info("Error in baritone command. " + e.getClass().getSimpleName());
                            }
                            return SINGLE_SUCCESS;
                        })
                )
        );
    }
}
