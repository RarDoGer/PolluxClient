/*
 * This file is part of the Pollux Client distribution (https://github.com/PolluxDevelopment/pollux-client/).
 * Copyright (c) 2020 Pollux Development.
 */

package rardoger.polluxclient.modules;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.zero.alpine.listener.Listenable;
import rardoger.polluxclient.PolluxClient;
import rardoger.polluxclient.events.EventStore;
import rardoger.polluxclient.gui.WidgetScreen;
import rardoger.polluxclient.gui.screens.ModuleScreen;
import rardoger.polluxclient.gui.widgets.WWidget;
import rardoger.polluxclient.settings.Settings;
import rardoger.polluxclient.utils.Utils;
import rardoger.polluxclient.utils.misc.ISerializable;
import rardoger.polluxclient.utils.render.color.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.Objects;

public abstract class Module implements Listenable, ISerializable<Module> {
    protected final MinecraftClient mc;

    public final Category category;
    public final String name;
    public final String title;
    public final String description;
    public final Color color;

    public final Settings settings = new Settings();

    public boolean serialize = true;

    private int key = -1;
    public boolean toggleOnKeyRelease = false;

    public Module(Category category, String name, String description) {
        this.mc = MinecraftClient.getInstance();
        this.category = category;
        this.name = name;
        this.title = Utils.nameToTitle(name);
        this.description = description;
        this.color = Color.fromHsv(Utils.random(0.0, 360.0), 0.35, 1);
    }

    public WidgetScreen getScreen() {
        return new ModuleScreen(this);
    }

    public WWidget getWidget() {
        return null;
    }

    public void openScreen() {
        mc.openScreen(getScreen());
    }

    public void doAction(boolean onActivateDeactivate) {
        openScreen();
    }
    public void doAction() {
        doAction(true);
    }

    public LiteralArgumentBuilder<CommandSource> buildCommand() {
        LiteralArgumentBuilder<CommandSource> builder = null;

        if (this instanceof ToggleModule) {
            builder = LiteralArgumentBuilder.literal(name);

            builder.executes(context -> {
                ((ToggleModule) this).toggle();
                return Command.SINGLE_SUCCESS;
            });
        }

        return builder;
    }

    @Override
    public CompoundTag toTag() {
        if (!serialize) return null;
        CompoundTag tag = new CompoundTag();

        tag.putString("name", name);
        tag.putInt("key", key);
        tag.putBoolean("toggleOnKeyRelease", toggleOnKeyRelease);
        tag.put("settings", settings.toTag());

        return tag;
    }

    @Override
    public Module fromTag(CompoundTag tag) {
        // General
        key = tag.getInt("key");
        toggleOnKeyRelease = tag.getBoolean("toggleOnKeyRelease");

        // Settings
        Tag settingsTag = tag.get("settings");
        if (settingsTag instanceof CompoundTag) settings.fromTag((CompoundTag) settingsTag);

        return this;
    }

    public void setKey(int key, boolean postEvent) {
        this.key = key;
        if (postEvent) PolluxClient.EVENT_BUS.post(EventStore.moduleBindChangedEvent(this));
    }
    public void setKey(int key) {
        setKey(key, true);
    }

    public int getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Module module = (Module) o;
        return Objects.equals(name, module.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
