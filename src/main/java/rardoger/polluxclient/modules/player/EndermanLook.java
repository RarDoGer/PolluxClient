/*
 * This file is part of the Pollux Client distribution (https://github.com/PolluxDevelopment/pollux-client/).
 * Copyright (c) 2020 Pollux Development.
 */

package rardoger.polluxclient.modules.player;

import com.google.common.collect.Streams;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import rardoger.polluxclient.events.world.PostTickEvent;
import rardoger.polluxclient.modules.Category;
import rardoger.polluxclient.modules.ToggleModule;
import rardoger.polluxclient.settings.EnumSetting;
import rardoger.polluxclient.settings.Setting;
import rardoger.polluxclient.settings.SettingGroup;
import rardoger.polluxclient.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class EndermanLook extends ToggleModule {
    public enum Mode{
        LookAt,
        LookAway
    }

    public EndermanLook() {
        super(Category.Player, "enderman-look", "Either looks at all Endermen or prevents you from looking at Endermen.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> lookMode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("look-mode")
            .description("How this module behaves.")
            .defaultValue(Mode.LookAway)
            .build()
    );

    EndermanEntity enderman = null;

    @EventHandler
    private final Listener<PostTickEvent> onTick = new Listener<>(event -> {
        if (lookMode.get() == Mode.LookAway) {
            if (mc.player.abilities.creativeMode || !shouldLook())
                return;

            PlayerMoveC2SPacket.LookOnly packet = new PlayerMoveC2SPacket.LookOnly(
                    mc.player.yaw,
                    90.0f,
                    mc.player.isOnGround());

            mc.player.networkHandler.sendPacket(packet);
        } else {
            if (enderman != null && !enderman.isAngry()){
                lookAt(enderman);
                return;
            }
            Streams.stream(mc.world.getEntities())
                    .filter(entity -> entity instanceof EndermanEntity)
                    .filter(entity -> !((EndermanEntity)entity).isAngry())
                    .filter(Entity::isAlive)
                    .filter(entity -> mc.player.canSee(entity))
                    .findFirst()
                    .ifPresent(tempEntity -> enderman = ((EndermanEntity)tempEntity));
        }
    });

    private boolean shouldLook() {
        return Streams.stream(mc.world.getEntities())
                .filter(entity -> entity instanceof EndermanEntity)
                .filter(Entity::isAlive)
                .anyMatch(this::angleCheck);
    }

    private boolean angleCheck(Entity entity) {
        Vec3d vec3d = mc.player.getRotationVec(1.0F).normalize();
        Vec3d vec3d2 = new Vec3d(
                entity.getX() - mc.player.getX(),
                entity.getEyeY() - mc.player.getEyeY(),
                entity.getZ() - mc.player.getZ());

        double d = vec3d2.length();
        vec3d2 = vec3d2.normalize();
        double e = vec3d.dotProduct(vec3d2);

        return e > 1.0D - 0.025D / d && mc.player.canSee(entity);
    }

    private void lookAt(Entity ender){
        Vec3d enderVec = new Vec3d(ender.getX(), ender.getEyeY(), ender.getZ());
        float pitch = Utils.getNeededPitch(enderVec);
        float yaw = Utils.getNeededYaw(enderVec);
        PlayerMoveC2SPacket.LookOnly packet = new PlayerMoveC2SPacket.LookOnly(yaw, pitch, mc.player.isOnGround());
        mc.player.networkHandler.sendPacket(packet);
    }
}
