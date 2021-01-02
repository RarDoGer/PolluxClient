/*
 * This file is part of the Pollux Client distribution (https://github.com/PolluxDevelopment/pollux-client/).
 * Copyright (c) 2020 Pollux Development.
 */

package rardoger.polluxclient.mixin;

import rardoger.polluxclient.mixininterface.IChatHudLine;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChatHudLine.class)
public class ChatHudLineMixin<T> implements IChatHudLine<T> {
    @Shadow private int creationTick;

    @Shadow private int id;

    @Shadow private T text;

    @Override
    public void setText(T text) {
        this.text = text;
    }

    @Override
    public void setTimestamp(int timestamp) {
        this.creationTick = timestamp;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }
}
