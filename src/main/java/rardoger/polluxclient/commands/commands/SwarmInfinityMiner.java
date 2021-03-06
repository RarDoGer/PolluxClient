package rardoger.polluxclient.commands.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import rardoger.polluxclient.commands.Command;
import rardoger.polluxclient.modules.ModuleManager;
import rardoger.polluxclient.modules.combat.Swarm;
import rardoger.polluxclient.modules.player.InfinityMiner;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class SwarmInfinityMiner extends Command {

    public SwarmInfinityMiner() {
        super("s", "(highlight)im <?TargetBlock> <?RepairBlock>(default) - Start Infinity Miner.");
    }

    public void runInfinityMiner() {
        InfinityMiner infinityMiner = ModuleManager.INSTANCE.get(InfinityMiner.class);
        if (infinityMiner.isActive()) infinityMiner.toggle();
        infinityMiner.smartModuleToggle.set(true);
        if (!infinityMiner.isActive()) infinityMiner.toggle();
    }


    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("im").executes(context -> {
                    Swarm swarm = ModuleManager.INSTANCE.get(Swarm.class);
                    if (swarm.isActive()) {
                        if (swarm.currentMode == Swarm.Mode.Queen) {
                            swarm.server.sendMessage(context.getInput());
                        } else {
                            runInfinityMiner();
                        }
                    }
                    return SINGLE_SUCCESS;
                }).then(argument("target", BlockStateArgumentType.blockState()).executes(context -> {
                    Swarm swarm = ModuleManager.INSTANCE.get(Swarm.class);
                    if (swarm.isActive()) {
                        if (swarm.currentMode == Swarm.Mode.Queen) {
                            swarm.server.sendMessage(context.getInput());
                        } else {
                            ModuleManager.INSTANCE.get(InfinityMiner.class).targetBlock.set(context.getArgument("target", BlockStateArgument.class).getBlockState().getBlock());
                            runInfinityMiner();
                        }
                    }
                    return SINGLE_SUCCESS;
                }).then(argument("repair", BlockStateArgumentType.blockState()).executes(context -> {
                    Swarm swarm = ModuleManager.INSTANCE.get(Swarm.class);
                    if (swarm.isActive()) {
                        if (swarm.currentMode == Swarm.Mode.Queen) {
                            swarm.server.sendMessage(context.getInput());
                        } else {
                            ModuleManager.INSTANCE.get(InfinityMiner.class).targetBlock.set(context.getArgument("target", BlockStateArgument.class).getBlockState().getBlock());
                            ModuleManager.INSTANCE.get(InfinityMiner.class).repairBlock.set(context.getArgument("repair", BlockStateArgument.class).getBlockState().getBlock());
                            runInfinityMiner();
                        }
                    }
                    return SINGLE_SUCCESS;
                })))
                        .then(literal("logout").then(argument("autologout", BoolArgumentType.bool()).executes(context -> {
                            Swarm swarm = ModuleManager.INSTANCE.get(Swarm.class);
                            if (swarm.isActive()) {
                                if (swarm.currentMode == Swarm.Mode.Queen) {
                                    swarm.server.sendMessage(context.getInput());
                                } else {
                                    boolean bool = context.getArgument("autologout", Boolean.class);
                                    InfinityMiner infinityMiner = ModuleManager.INSTANCE.get(InfinityMiner.class);
                                    infinityMiner.autoLogOut.set(bool);
                                }
                            }
                            return SINGLE_SUCCESS;
                        })))
                        .then(literal("walkhome").then(argument("walkhome", BoolArgumentType.bool()).executes(context -> {
                            Swarm swarm = ModuleManager.INSTANCE.get(Swarm.class);
                            if (swarm.isActive()) {
                                if (swarm.currentMode == Swarm.Mode.Queen) {
                                    swarm.server.sendMessage(context.getInput());
                                } else {
                                    boolean bool = context.getArgument("walkhome", Boolean.class);
                                    InfinityMiner infinityMiner = ModuleManager.INSTANCE.get(InfinityMiner.class);
                                    infinityMiner.autoWalkHome.set(bool);
                                }
                            }
                            return SINGLE_SUCCESS;
                        })))
        );
    }
}
