/*
 * This file is part of the Pollux Client distribution (https://github.com/PolluxDevelopment/pollux-client/).
 * Copyright (c) 2020 Pollux Development.
 */

package rardoger.polluxclient.modules.player;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import rardoger.polluxclient.events.entity.player.StartBreakingBlockEvent;
import rardoger.polluxclient.events.world.PostTickEvent;
import rardoger.polluxclient.modules.Category;
import rardoger.polluxclient.modules.ToggleModule;
import rardoger.polluxclient.settings.BoolSetting;
import rardoger.polluxclient.settings.Setting;
import rardoger.polluxclient.settings.SettingGroup;
import rardoger.polluxclient.utils.Utils;
import rardoger.polluxclient.utils.misc.Pool;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class PacketMine extends ToggleModule {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> oneByOne = sgGeneral.add(new BoolSetting.Builder()
            .name("one-by-one")
            .description("Mines blocks one by one.")
            .defaultValue(true)
            .build()
    );

    private final Pool<Block> blockPool = new Pool<>(Block::new);
    private final List<Block> blocks = new ArrayList<>();

    public PacketMine() {
        super(Category.Player, "packet-mine", "Sends packets to mine blocks without the mining animation.");
    }

    @Override
    public void onDeactivate() {
        for (Block block : blocks) blockPool.free(block);
        blocks.clear();
    }

    @EventHandler
    private final Listener<StartBreakingBlockEvent> onStartBreakingBlock = new Listener<>(event -> {
        if (mc.world.getBlockState(event.blockPos).getHardness(mc.world, event.blockPos) < 0) return;

        Block block = blockPool.get();
        block.blockPos = event.blockPos;
        block.direction = event.direction;
        blocks.add(block);

        event.cancel();
    });

    @EventHandler
    private final Listener<PostTickEvent> onTick = new Listener<>(event -> {
        if (oneByOne.get()) {
            if (blocks.size() > 0 && blocks.get(blocks.size() - 1).sendPackets()) {
                blocks.remove(blocks.size() - 1);
            }
        } else {
            blocks.removeIf(Block::sendPackets);
        }
    });

    private class Block {
        public BlockPos blockPos;
        public Direction direction;

        public boolean sendPackets() {
            if (mc.world.getBlockState(blockPos).getBlock() == Blocks.AIR) return true;
            if (Utils.distance(mc.player.getX() - 0.5, mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ() - 0.5, blockPos.getX() + direction.getOffsetX(), blockPos.getY() + direction.getOffsetY(), blockPos.getZ() + direction.getOffsetZ()) > mc.interactionManager.getReachDistance()) return true;

            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockPos, direction));
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction));
            return false;
        }
    }
}
