/*
 * This file is part of the Pollux Client distribution (https://github.com/PolluxDevelopment/pollux-client/).
 * Copyright (c) 2020 Pollux Development.
 */

package rardoger.polluxclient.modules.render.hud.modules;

import rardoger.polluxclient.modules.render.hud.HUD;
import rardoger.polluxclient.modules.render.hud.HudRenderer;
import rardoger.polluxclient.rendering.DrawMode;
import rardoger.polluxclient.rendering.Renderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;

public class PlayerModelHud extends HudModule {
    public PlayerModelHud(HUD hud) {
        super(hud, "player-model", "Displays a model of your player.");
    }

    @Override
    public void update(HudRenderer renderer) {
        box.setSize(51 * hud.playerModelScale(), 75 * hud.playerModelScale());
    }

    @Override
    public void render(HudRenderer renderer) {
        MinecraftClient mc = MinecraftClient.getInstance();

        int x = box.getX();
        int y = box.getY();

        if (hud.playerModelBackground()) {
            Renderer.NORMAL.begin(null, DrawMode.Triangles, VertexFormats.POSITION_COLOR);
            Renderer.NORMAL.quad(x, y, box.width, box.height, hud.playerModelColor());
            Renderer.NORMAL.end();
        }

        if (mc.player != null) {
            float yaw = hud.playerModelCopyYaw() ? wrapValue(mc.player.prevYaw, mc.player.yaw) : 0.0f;
            float pitch = hud.playerModelCopyPitch() ? wrapValue(mc.player.prevPitch, mc.player.pitch) : 0.0f;
            InventoryScreen.drawEntity(x + (int) (25 * hud.playerModelScale()), y + (int) (66 * hud.playerModelScale()), (int) (30 * hud.playerModelScale()), -yaw, -pitch, mc.player);
        }
    }

    private float wrapValue(float prev, float current) {
        return MathHelper.wrapDegrees(prev + (current - prev) * MinecraftClient.getInstance().getTickDelta());
    }
}
