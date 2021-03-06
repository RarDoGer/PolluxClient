/*
 * This file is part of the Pollux Client distribution (https://github.com/PolluxDevelopment/pollux-client/).
 * Copyright (c) 2020 Pollux Development.
 */

package rardoger.polluxclient.modules.render.hud.modules;

import me.zero.alpine.listener.Listener;
import rardoger.polluxclient.PolluxClient;
import rardoger.polluxclient.events.pollux.ActiveModulesChangedEvent;
import rardoger.polluxclient.events.pollux.ModuleVisibilityChangedEvent;
import rardoger.polluxclient.modules.ModuleManager;
import rardoger.polluxclient.modules.ToggleModule;
import rardoger.polluxclient.modules.render.hud.HUD;
import rardoger.polluxclient.modules.render.hud.HudRenderer;

import java.util.ArrayList;
import java.util.List;

public class ActiveModulesHud extends HudModule {
    public enum Sort {
        ByBiggest,
        BySmallest
    }

    private final List<ToggleModule> modules = new ArrayList<>();
    private boolean update = true;

    public ActiveModulesHud(HUD hud) {
        super(hud, "active-modules", "Displays your active modules.");

        PolluxClient.EVENT_BUS.subscribe(new Listener<ActiveModulesChangedEvent>(event -> update = true));
        PolluxClient.EVENT_BUS.subscribe(new Listener<ModuleVisibilityChangedEvent>(event -> update = true));
    }

    public void recalculate() {
        update = true;
    }

    @Override
    public void update(HudRenderer renderer) {
        if (ModuleManager.INSTANCE == null) {
            box.setSize(renderer.textWidth("Active Modules"), renderer.textHeight());
            return;
        }

        if (!update) return;
        update = false;
        modules.clear();

        for (ToggleModule module : ModuleManager.INSTANCE.getActive()) {
            if (module.isVisible()) modules.add(module);
        }

        modules.sort((o1, o2) -> {
            double _1 = getModuleWidth(renderer, o1);
            double _2 = getModuleWidth(renderer, o2);

            if (hud.activeModulesSort() == Sort.BySmallest) {
                double temp = _1;
                _1 = _2;
                _2 = temp;
            }

            int a = Double.compare(_1, _2);
            if (a == 0) return 0;
            return a < 0 ? 1 : -1;
        });

        double width = 0;
        double height = 0;

        for (int i = 0; i < modules.size(); i++) {
            ToggleModule module = modules.get(i);

            width = Math.max(width, getModuleWidth(renderer, module));
            height += renderer.textHeight();
            if (i > 0) height += 2;
        }

        box.setSize(width, height);
    }

    @Override
    public void render(HudRenderer renderer) {
        double x = box.getX();
        double y = box.getY();

        if (ModuleManager.INSTANCE == null) {
            renderer.text("Active Modules", x, y, hud.color);
            return;
        }

        for (ToggleModule module : modules) {
            renderModule(renderer, module, x + box.alignX(getModuleWidth(renderer, module)), y);

            y += 2 + renderer.textHeight();
        }
    }

    private void renderModule(HudRenderer renderer, ToggleModule module, double x, double y) {
        renderer.text(module.title, x, y, module.color);

        String info = module.getInfoString();
        if (info != null) {
            renderer.text(info, x + renderer.textWidth(module.title) + renderer.textWidth(" "), y, hud.secondaryColor());
        }
    }

    private double getModuleWidth(HudRenderer renderer, ToggleModule module) {
        String info = module.getInfoString();
        double width = renderer.textWidth(module.title);
        if (info != null) width += renderer.textWidth(" ") + renderer.textWidth(info);
        return width;
    }
}
