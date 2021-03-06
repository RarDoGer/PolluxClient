/*
 * This file is part of the Pollux Client distribution (https://github.com/PolluxDevelopment/pollux-client/).
 * Copyright (c) 2020 Pollux Development.
 */

package rardoger.polluxclient.modules.render;

import rardoger.polluxclient.modules.Category;
import rardoger.polluxclient.modules.ToggleModule;
import rardoger.polluxclient.settings.*;
import rardoger.polluxclient.utils.render.color.SettingColor;
import rardoger.polluxclient.utils.render.color.Color;
import rardoger.polluxclient.utils.render.color.ColorUtil;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class Chams extends ToggleModule {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    private final Setting<List<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
            .name("entities")
            .description("Select entities to show through walls.")
            .defaultValue(new ArrayList<>(0))
            .build()
    );

    public final Setting<Boolean> throughWalls = sgGeneral.add(new BoolSetting.Builder()
            .name("through-walls")
            .description("Renders entities through walls.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> colored = sgGeneral.add(new BoolSetting.Builder()
            .name("colored")
            .description("Renders entity models with a custom color.")
            .defaultValue(true)
            .build()
    );

    // Colors

    private final SettingGroup sgColors = settings.createGroup("Colors");

    private final Setting<SettingColor> playersColor = sgColors.add(new ColorSetting.Builder()
            .name("players-color")
            .description("The other player's color.")
            .defaultValue(new SettingColor(255, 255, 255))
            .build()
    );

    private final Setting<SettingColor> animalsColor = sgColors.add(new ColorSetting.Builder()
            .name("animals-color")
            .description("The animal's color.")
            .defaultValue(new SettingColor(25, 255, 25, 255))
            .build()
    );

    private final Setting<SettingColor> waterAnimalsColor = sgColors.add(new ColorSetting.Builder()
            .name("water-animals-color")
            .description("The water animal's color.")
            .defaultValue(new SettingColor(25, 25, 255, 255))
            .build()
    );

    private final Setting<SettingColor> monstersColor = sgColors.add(new ColorSetting.Builder()
            .name("monsters-color")
            .description("The monster's color.")
            .defaultValue(new SettingColor(255, 25, 25, 255))
            .build()
    );

    private final Setting<SettingColor> ambientColor = sgColors.add(new ColorSetting.Builder()
            .name("ambient-color")
            .description("The ambient's color.")
            .defaultValue(new SettingColor(25, 25, 25, 255))
            .build()
    );

    private final Setting<SettingColor> miscColor = sgColors.add(new ColorSetting.Builder()
            .name("misc-color")
            .description("The misc color.")
            .defaultValue(new SettingColor(175, 175, 175, 255))
            .build()
    );

    private static final Color WHITE = new Color(255, 255, 255);

    public Chams() {
        super(Category.Render, "chams", "Renders entities through walls.");
    }

    public boolean ignoreRender(Entity entity) {
        return !isActive() || !entities.get().contains(entity.getType());
    }

    public boolean renderChams(EntityModel<LivingEntity> model, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, LivingEntity entity) {
        if (ignoreRender(entity) || !colored.get()) return false;
        Color color = getColor(entity);
        model.render(matrices, vertices, light, overlay, (float)color.r/255f, (float)color.g/255f, (float)color.b/255f, (float)color.a/255f);
        return true;
    }

    // TODO: 30/12/2020 Fix crystal chams

//    public boolean renderChamsCrystal(ModelPart modelPart, MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
//        if (!isActive() || !entities.get().contains(EntityType.END_CRYSTAL) || !colored.get()) return false;
//        Color color = miscColor.get();
//        modelPart.render(matrices, vertices, light, overlay, (float)color.r/255f, (float)color.g/255f, (float)color.b/255f, (float)color.a/255f);
//        return true;
//    }

    private Color getColor(Entity entity) {
        return ColorUtil.getEntityColor(entity, playersColor.get(), animalsColor.get(), waterAnimalsColor.get(), monstersColor.get(), ambientColor.get(), miscColor.get());
    }
}
