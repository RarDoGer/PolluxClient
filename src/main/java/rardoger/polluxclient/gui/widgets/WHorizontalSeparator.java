/*
 * This file is part of the Pollux Client distribution (https://github.com/PolluxDevelopment/pollux-client/).
 * Copyright (c) 2020 Pollux Development.
 */

package rardoger.polluxclient.gui.widgets;

import rardoger.polluxclient.gui.GuiConfig;
import rardoger.polluxclient.gui.renderer.GuiRenderer;
import rardoger.polluxclient.gui.renderer.Region;

public class WHorizontalSeparator extends WWidget {
    private final String text;
    private double textWidth;

    public WHorizontalSeparator(String text) {
        this.text = text;
        this.textWidth = -1;
    }

    public WHorizontalSeparator() {
        this(null);
    }

    @Override
    protected void onCalculateSize(GuiRenderer renderer) {
        if (text != null) textWidth = renderer.textWidth(text);

        width = 0;
        height = text != null ? renderer.textHeight() : 1;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        double textStart = Math.round(width / 2.0 - textWidth / 2.0 - 2);
        double textEnd = 2 + textStart + textWidth + 2;

        double offsetY = Math.round(height / 2.0);

        if (text != null) {
            renderer.quad(Region.FULL, x, y + offsetY, textStart, 1, GuiConfig.INSTANCE.separator);
            renderer.text(text, x + textStart + 2, y, false, GuiConfig.INSTANCE.separator);
            renderer.quad(Region.FULL, x + textEnd, y + offsetY, width - textEnd, 1, GuiConfig.INSTANCE.separator);
        } else {
            renderer.quad(Region.FULL, x, y, width, height, GuiConfig.INSTANCE.separator);
        }
    }
}
