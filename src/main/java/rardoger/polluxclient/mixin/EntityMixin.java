/*
 * This file is part of the Pollux Client distribution (https://github.com/PolluxDevelopment/pollux-client/).
 * Copyright (c) 2020 Pollux Development.
 */

package rardoger.polluxclient.mixin;

import rardoger.polluxclient.PolluxClient;
import rardoger.polluxclient.events.EventStore;
import rardoger.polluxclient.events.entity.player.JumpVelocityMultiplierEvent;
import rardoger.polluxclient.modules.ModuleManager;
import rardoger.polluxclient.modules.movement.NoSlow;
import rardoger.polluxclient.modules.movement.Velocity;
import rardoger.polluxclient.modules.render.ESP;
import rardoger.polluxclient.utils.render.Outlines;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public World world;

    @Shadow public abstract BlockPos getBlockPos();

    @Shadow protected abstract BlockPos getVelocityAffectingPos();

    @Redirect(method = "setVelocityClient", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocity(DDD)V"))
    private void setVelocityClientEntiySetVelocityProxy(Entity entity, double x, double y, double z) {
        if ((Object) this != MinecraftClient.getInstance().player) {
            entity.setVelocity(x, y, z);
            return;
        }

        Velocity velocity = ModuleManager.INSTANCE.get(Velocity.class);
        System.out.println(entity.getVelocity());
        //System.out.println(x + ", " + y + ", " + z);
        //System.out.println((x * velocity.getHorizontal()) + ", " + (y * velocity.getVertical()) + ", " + (z * velocity.getHorizontal()));
        entity.setVelocity(entity.getVelocity().x + x * velocity.getHorizontal(), entity.getVelocity().y + y * velocity.getVertical(), entity.getVelocity().z + z * velocity.getHorizontal());
    }

    @Inject(method = "getJumpVelocityMultiplier", at = @At("HEAD"), cancellable = true)
    private void onGetJumpVelocityMultiplier(CallbackInfoReturnable<Float> info) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            float f = world.getBlockState(getBlockPos()).getBlock().getJumpVelocityMultiplier();
            float g = world.getBlockState(getVelocityAffectingPos()).getBlock().getJumpVelocityMultiplier();
            float a = f == 1.0D ? g : f;

            JumpVelocityMultiplierEvent event = PolluxClient.postEvent(EventStore.jumpVelocityMultiplierEvent());
            info.setReturnValue(a * event.multiplier);
        }
    }

    @Redirect(method = "addVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d addVelocityVec3dAddProxy(Vec3d vec3d, double x, double y, double z) {
        if ((Object) this != MinecraftClient.getInstance().player) return vec3d.add(x, y, z);

        Velocity velocity = ModuleManager.INSTANCE.get(Velocity.class);
        return vec3d.add(x * velocity.getHorizontal(), y * velocity.getVertical(), z * velocity.getHorizontal());
    }

    @Inject(method = "move", at = @At("HEAD"))
    private void onMove(MovementType type, Vec3d movement, CallbackInfo info) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            PolluxClient.EVENT_BUS.post(EventStore.playerMoveEvent(type, movement));
        } else if ((Object) this instanceof LivingEntity) {
            PolluxClient.EVENT_BUS.post(EventStore.livingEntityMoveEvent((LivingEntity) (Object) this, movement));
        }
    }

    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    private void onGetTeamColorValue(CallbackInfoReturnable<Integer> info) {
        if (Outlines.renderingOutlines) {
            info.setReturnValue(ModuleManager.INSTANCE.get(ESP.class).getColor((Entity) (Object) this).getPacked());
        }
    }

    @Redirect(method = "getVelocityMultiplier", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"))
    private Block getVelocityMultiplierGetBlockProxy(BlockState blockState) {
        if (blockState.getBlock() == Blocks.SOUL_SAND && ModuleManager.INSTANCE.get(NoSlow.class).soulSand()) return Blocks.STONE;
        return blockState.getBlock();
    }


    @Inject(method = "isInvisibleTo(Lnet/minecraft/entity/player/PlayerEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void isInvisibleToCanceller(PlayerEntity player, CallbackInfoReturnable<Boolean> info) {
        if (ModuleManager.INSTANCE.get(ESP.class).isActive() && ModuleManager.INSTANCE.get(ESP.class).showInvis.get()) info.setReturnValue(false);
    }
}
