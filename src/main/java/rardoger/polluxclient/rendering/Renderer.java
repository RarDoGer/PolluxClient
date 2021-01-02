/*
 * This file is part of the Pollux Client distribution (https://github.com/PolluxDevelopment/pollux-client/).
 * Copyright (c) 2020 Pollux Development.
 */

package rardoger.polluxclient.rendering;

import rardoger.polluxclient.events.render.RenderEvent;
import rardoger.polluxclient.utils.render.color.Color;
import net.minecraft.client.render.VertexFormats;

public class Renderer {
    public static final MeshBuilder NORMAL = new MeshBuilder();
    public static final MeshBuilder LINES = new MeshBuilder();

    private static boolean building;

    public static void begin(RenderEvent event) {
        if (!building) {
            NORMAL.begin(event, DrawMode.Triangles, VertexFormats.POSITION_COLOR);
            LINES.begin(event, DrawMode.Lines, VertexFormats.POSITION_COLOR);

            building = true;
        }
    }

    public static void end() {
        if (building) {
            NORMAL.end();
            LINES.end();

            building = false;
        }
    }

    public static void boxWithLines(MeshBuilder normal, MeshBuilder lines, double x1, double y1, double z1, double x2, double y2, double z2, Color sideColor, Color lineColor, ShapeMode mode, int excludeDir) {
        if (mode == ShapeMode.Sides || mode == ShapeMode.Both) {
            normal.boxSides(x1, y1, z1, x2, y2, z2, sideColor, excludeDir);
        }

        if (mode == ShapeMode.Lines || mode == ShapeMode.Both) {
            lines.boxEdges(x1, y1, z1, x2, y2, z2, lineColor, excludeDir);
        }
    }

    public static void boxWithLines(MeshBuilder normal, MeshBuilder lines, double x, double y, double z, double size, Color sideColor, Color lineColor, ShapeMode mode, int excludeDir) {
        boxWithLines(normal, lines, x, y, z, x + size, y + size, z + size, sideColor, lineColor, mode, excludeDir);
    }

    public static void quadWithLines(MeshBuilder normal, MeshBuilder lines, double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, Color sideColor, Color lineColor, ShapeMode mode) {
        if (mode == ShapeMode.Sides || mode == ShapeMode.Both) {
            normal.quad(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, sideColor);
        }

        if (mode == ShapeMode.Lines || mode == ShapeMode.Both) {
            lines.line(x1, y1, z1, x2, y2, z2, lineColor);
            lines.line(x2, y2, z2, x3, y3, z3, lineColor);
            lines.line(x3, y3, z3, x4, y4, z4, lineColor);
            lines.line(x4, y4, z4, x1, y1, z1, lineColor);
        }
    }

    public static void quadWithLinesHorizontal(MeshBuilder normal, MeshBuilder lines, double x, double y, double z, double size, Color sideColor, Color lineColor, ShapeMode mode) {
        quadWithLines(normal, lines, x, y, z, x, y, z + size, x + size, y, z + size, x + size, y, z, sideColor, lineColor, mode);
    }

    public static void quadWithLinesVertical(MeshBuilder normal, MeshBuilder lines, double x1, double y1, double z1, double x2, double y2, Color sideColor, Color lineColor, ShapeMode mode) {
        quadWithLines(normal, lines, x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1, sideColor, lineColor, mode);
    }
}
