/*
 * This file is part of the Pollux Client distribution (https://github.com/PolluxDevelopment/pollux-client/).
 * Copyright (c) 2020 Pollux Development.
 */

package rardoger.polluxclient.mixin;

import rardoger.polluxclient.mixininterface.IMinecraftServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements IMinecraftServer {
    @Shadow @Final protected LevelStorage.Session session;

    @Override
    public LevelStorage.Session getSession() {
        return session;
    }
}
