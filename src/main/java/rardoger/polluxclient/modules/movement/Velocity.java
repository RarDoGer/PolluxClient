/*
 * This file is part of the Pollux Client distribution (https://github.com/PolluxDevelopment/pollux-client/).
 * Copyright (c) 2020 Pollux Development.
 */

package rardoger.polluxclient.modules.movement;

import rardoger.polluxclient.modules.Category;
import rardoger.polluxclient.modules.ToggleModule;
import rardoger.polluxclient.settings.DoubleSetting;
import rardoger.polluxclient.settings.Setting;
import rardoger.polluxclient.settings.SettingGroup;

public class Velocity extends ToggleModule {
    private final SettingGroup sg = settings.getDefaultGroup();

    private final Setting<Double> horizontal = sg.add(new DoubleSetting.Builder()
            .name("horizontal-multiplier")
            .description("How much velocity you will take horizontally.")
            .defaultValue(0)
            .min(0)
            .max(1)
            .sliderMin(0)
            .sliderMax(1)
            .build()
    );

    private final Setting<Double> vertical = sg.add(new DoubleSetting.Builder()
            .name("vertical-multiplier")
            .description("How much velocity you will take vertically.")
            .defaultValue(0)
            .min(0)
            .max(1)
            .sliderMin(0)
            .sliderMax(1)
            .build()
    );

    public Velocity() {
        super(Category.Movement, "velocity", "Prevents you from being moved by external forces.");
    }

    public double getHorizontal() {
        return isActive() ? horizontal.get() : 1;
    }

    public double getVertical() {
        return isActive() ? vertical.get() : 1;
    }
}
