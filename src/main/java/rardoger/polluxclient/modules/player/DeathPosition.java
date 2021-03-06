/*
 * This file is part of the Pollux Client distribution (https://github.com/PolluxDevelopment/pollux-client/).
 * Copyright (c) 2020 Pollux Development.
 */

package rardoger.polluxclient.modules.player;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalXZ;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import rardoger.polluxclient.events.entity.TookDamageEvent;
import rardoger.polluxclient.gui.widgets.WButton;
import rardoger.polluxclient.gui.widgets.WLabel;
import rardoger.polluxclient.gui.widgets.WTable;
import rardoger.polluxclient.gui.widgets.WWidget;
import rardoger.polluxclient.modules.Category;
import rardoger.polluxclient.modules.ToggleModule;
import rardoger.polluxclient.settings.BoolSetting;
import rardoger.polluxclient.settings.Setting;
import rardoger.polluxclient.settings.SettingGroup;
import rardoger.polluxclient.utils.Utils;
import rardoger.polluxclient.utils.player.Chat;
import rardoger.polluxclient.waypoints.Waypoint;
import rardoger.polluxclient.waypoints.Waypoints;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DeathPosition extends ToggleModule {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> createWaypoint = sgGeneral.add(new BoolSetting.Builder()
            .name("create-waypoint")
            .description("Creates a waypoint when you die.")
            .defaultValue(true)
            .build()
    );

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final WLabel label = new WLabel("You have no latest death.");

    public DeathPosition() {
        super(Category.Player, "death-position", "Sends you the coordinates to your latest death.");
    }

    private final Map<String, Double> deathPos = new HashMap<>();
    private Waypoint waypoint;

    @SuppressWarnings("unused")
    @EventHandler
    private final Listener<TookDamageEvent> onTookDamage = new Listener<>(event -> {
        if (mc.player == null) return;
        if (event.entity.getUuid() != null && event.entity.getUuid().equals(mc.player.getUuid()) && event.entity.getHealth() <= 0) {
            deathPos.put("x", mc.player.getX());
            deathPos.put("z", mc.player.getZ());
            label.setText(String.format("Latest death: %.1f, %.1f, %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ()));

            String time = dateFormat.format(new Date());
            Chat.info(this, "Died at (highlight)%.0f(default), (highlight)%.0f(default), (highlight)%.0f (default)on (highlight)%s(default).", mc.player.getX(), mc.player.getY(), mc.player.getZ(), time);

            // Create waypoint
            if (createWaypoint.get()) {
                waypoint = new Waypoint();
                waypoint.name = "Death " + time;

                waypoint.x = (int) mc.player.getX();
                waypoint.y = (int) mc.player.getY() + 2;
                waypoint.z = (int) mc.player.getZ();
                waypoint.maxVisibleDistance = Integer.MAX_VALUE;

                switch (Utils.getDimension()) {
                    case Overworld:
                        waypoint.overworld = true;
                        break;
                    case Nether:
                        waypoint.nether = true;
                        break;
                    case End:
                        waypoint.end = true;
                        break;
                }

                Waypoints.INSTANCE.add(waypoint);
            }
        }
    });

    @Override
    public WWidget getWidget() {
        WTable table = new WTable();
        table.add(label);
        WButton path = new WButton("Path");
        table.add(path);
        path.action = this::path;
        WButton clear = new WButton("Clear");
        table.add(clear);
        clear.action = this::clear;
        return table;
    }

    private void path() {
        if (deathPos.isEmpty() && mc.player != null) {
            Chat.info("No latest death.");
        } else {
            double x = deathPos.get("x"), z = deathPos.get("z");
            if (BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing())
                BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
            BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalXZ((int) x, (int) z));
        }
    }

    private void clear() {
        Waypoints.INSTANCE.remove(waypoint);
        label.setText("No latest Death.");
    }
}
