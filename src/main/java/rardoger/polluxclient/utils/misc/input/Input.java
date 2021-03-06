/*
 * This file is part of the Pollux Client distribution (https://github.com/PolluxDevelopment/pollux-client/).
 * Copyright (c) 2020 Pollux Development.
 */

package rardoger.polluxclient.utils.misc.input;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class Input {
    private static final boolean[] keys = new boolean[512];

    public static void setKeyState(int key, boolean pressed) {
        if (key < keys.length) keys[key] = pressed;
    }

    public static boolean isPressed(KeyBinding keyBinding) {
        int key = KeyBindingHelper.getBoundKeyOf(keyBinding).getCode();
        if (key == GLFW.GLFW_KEY_UNKNOWN) return false;
        return key < keys.length && keys[key];
    }

    public static boolean isPressed(int key) {
        if (key == GLFW.GLFW_KEY_UNKNOWN) return false;
        return key < keys.length && keys[key];
    }
}
