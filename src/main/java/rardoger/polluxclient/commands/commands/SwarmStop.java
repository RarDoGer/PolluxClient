package rardoger.polluxclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import rardoger.polluxclient.commands.Command;
import rardoger.polluxclient.modules.ModuleManager;
import rardoger.polluxclient.modules.combat.Swarm;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class SwarmStop extends Command {

    public SwarmStop() {
        super("s","(highlight)stop(default) Stop all current tasks.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("stop").executes(context -> {
            Swarm swarm = ModuleManager.INSTANCE.get(Swarm.class);
            if(swarm.isActive()) {
                if (swarm.currentMode == Swarm.Mode.Queen && swarm.server != null) {
                    swarm.server.sendMessage(context.getInput());
                } else {
                    swarm.idle();
                }
            }
            return SINGLE_SUCCESS;
        }));
    }
}
