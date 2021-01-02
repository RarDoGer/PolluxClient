/*
 * This file is part of the Pollux Client distribution (https://github.com/PolluxDevelopment/pollux-client/).
 * Copyright (c) 2020 Pollux Development.
 */

package rardoger.polluxclient.gui.renderer;

import rardoger.polluxclient.gui.widgets.Cell;
import rardoger.polluxclient.gui.widgets.WWidget;
import rardoger.polluxclient.rendering.DrawMode;
import rardoger.polluxclient.rendering.MeshBuilder;
import rardoger.polluxclient.utils.render.color.Color;
import net.minecraft.client.render.VertexFormats;

public class GuiDebugRenderer {
    private static final Color CELL_COLOR = new Color(25, 225, 25);
    private static final Color WIDGET_COLOR = new Color(25, 25, 225);

    private final MeshBuilder mb = new MeshBuilder();

    public void render(WWidget widget) {
        mb.begin(null, DrawMode.Lines, VertexFormats.POSITION_COLOR);
        renderWidget(widget);
        mb.end();
    }

    private void renderWidget(WWidget widget) {
        lineBox(widget.x, widget.y, widget.width, widget.height, WIDGET_COLOR);

        for (Cell<?> cell : widget.getCells()) {
            lineBox(cell.getX(), cell.getY(), cell.getWidth(), cell.getHeight(), CELL_COLOR);
            renderWidget(cell.getWidget());
        }
    }

    private void lineBox(double x, double y, double width, double height, Color color) {
        line(x, y, x + width, y, color);
        line(x + width, y, x + width, y + height, color);
        line(x, y, x, y + height, color);
        line(x, y + height, x + width, y + height, color);
    }

    private void line(double x1, double y1, double x2, double y2, Color color) {
        mb.pos(x1, y1, 0).color(color).endVertex();
        mb.pos(x2, y2, 0).color(color).endVertex();
    }
}
