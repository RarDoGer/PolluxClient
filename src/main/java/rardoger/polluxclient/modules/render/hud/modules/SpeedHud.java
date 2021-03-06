/*
 * This file is part of the Pollux Client distribution (https://github.com/PolluxDevelopment/pollux-client/).
 * Copyright (c) 2020 Pollux Development.
 */

package rardoger.polluxclient.modules.render.hud.modules;

import rardoger.polluxclient.modules.ModuleManager;
import rardoger.polluxclient.modules.movement.Timer;
import rardoger.polluxclient.modules.render.hud.HUD;
import net.minecraft.client.MinecraftClient;

public class SpeedHud extends DoubleTextHudModule {
    public SpeedHud(HUD hud) {
        super(hud, "speed", "Displays your horizontal speed.", "Speed: ");
    }

    @Override
    protected String getRight() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return "0,0";

        double tX = Math.abs(mc.player.getX() - mc.player.prevX);
        double tZ = Math.abs(mc.player.getZ() - mc.player.prevZ);
        double length = Math.sqrt(tX * tX + tZ * tZ);

        if (ModuleManager.INSTANCE.get(Timer.class).isActive()){
            length *= ModuleManager.INSTANCE.get(Timer.class).getMultiplier();
        }

        return String.format("%.1f", length * 20);
    }
}
