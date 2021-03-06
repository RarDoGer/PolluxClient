/*
 * This file is part of the Pollux Client distribution (https://github.com/PolluxDevelopment/pollux-client/).
 * Copyright (c) 2020 Pollux Development.
 */

package rardoger.polluxclient.modules.combat;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import rardoger.polluxclient.PolluxClient;
import rardoger.polluxclient.events.world.PostTickEvent;
import rardoger.polluxclient.friends.FriendManager;
import rardoger.polluxclient.modules.Category;
import rardoger.polluxclient.modules.ModuleManager;
import rardoger.polluxclient.modules.ToggleModule;
import rardoger.polluxclient.modules.movement.NoFall;
import rardoger.polluxclient.settings.BoolSetting;
import rardoger.polluxclient.settings.IntSetting;
import rardoger.polluxclient.settings.Setting;
import rardoger.polluxclient.settings.SettingGroup;
import rardoger.polluxclient.utils.Utils;
import rardoger.polluxclient.utils.player.DamageCalcUtils;
import rardoger.polluxclient.utils.world.Dimension;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AutoLog extends ToggleModule {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    private final Setting<Integer> health = sgGeneral.add(new IntSetting.Builder()
            .name("health")
            .description("Automatically disconnects when health is lower or equal to this value.")
            .defaultValue(6)
            .min(0)
            .max(20)
            .sliderMax(20)
            .build()
    );

    private final Setting<Boolean> smart = sgGeneral.add(new BoolSetting.Builder()
            .name("smart")
            .description("Disconnects when you're about to take enough damage to kill you.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> onlyTrusted = sgGeneral.add(new BoolSetting.Builder()
            .name("only-trusted")
            .description("Disconnects when a player not on your friends list appears in render distance.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> instantDeath = sgGeneral.add(new BoolSetting.Builder()
            .name("32k")
            .description("Disconnects when a player near you can instantly kill you.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> crystalLog = sgGeneral.add(new BoolSetting.Builder()
            .name("crystal-log")
            .description("Disconnects when a crystal appears near you.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
            .name("range").description("How close a crystal has to be to you before you disconnect.")
            .defaultValue(4)
            .min(1)
            .max(10)
            .sliderMax(5)
            .build()
    );

    private final Setting<Boolean> smartToggle = sgGeneral.add(new BoolSetting.Builder()
            .name("smart-toggle")
            .description("Disables Auto Log after a low-health logout. WILL re-enable once you heal.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> toggleOff = sgGeneral.add(new BoolSetting.Builder()
            .name("toggle-off")
            .description("Disables Auto Log after usage.")
            .defaultValue(true)
            .build()
    );

    public AutoLog() {
        super(Category.Combat, "auto-log", "Automatically disconnects you when certain requirements are met.");
    }

    @EventHandler
    private final Listener<PostTickEvent> onTick = new Listener<>(event -> {
        if (mc.player.getHealth() <= 0) {
            this.toggle();
            return;
        }
        if (mc.player.getHealth() <= health.get()) {
            mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(new LiteralText("[AutoLog] Health was lower than " + health.get() + ".")));
            if(smartToggle.get()) {
                this.toggle();
                enableHealthListener();
            }
        }

        if(smart.get() && mc.player.getHealth() + mc.player.getAbsorptionAmount() - getHealthReduction() < health.get()){
            mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(new LiteralText("[AutoLog] Health was going to be lower than " + health.get() + ".")));
            if (toggleOff.get()) this.toggle();
        }

        for (Entity entity : mc.world.getEntities()) {
            if(entity instanceof PlayerEntity && entity.getUuid() != mc.player.getUuid()) {
                if (onlyTrusted.get() && entity != mc.player && !FriendManager.INSTANCE.isTrusted((PlayerEntity) entity)) {
                        mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(new LiteralText("[AutoLog] A non-trusted player appeared in your render distance.")));
                        if (toggleOff.get()) this.toggle();
                        break;
                }
                if (mc.player.distanceTo(entity) < 8 && instantDeath.get() && DamageCalcUtils.getSwordDamage((PlayerEntity) entity, true)
                        > mc.player.getHealth() + mc.player.getAbsorptionAmount()) {
                    mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(new LiteralText("[AutoLog] Anti-32k measures.")));
                    if (toggleOff.get()) this.toggle();
                    break;
                }
            }
            if (entity instanceof EndCrystalEntity && mc.player.distanceTo(entity) < range.get() && crystalLog.get()) {
                mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(new LiteralText("[AutoLog] End Crystal appeared within specified range.")));
                if (toggleOff.get()) this.toggle();
            }
        }
    });

    private double getHealthReduction(){
        double damageTaken = 0;
        for(Entity entity : mc.world.getEntities()){
            if(entity instanceof EndCrystalEntity && damageTaken < DamageCalcUtils.crystalDamage(mc.player, entity.getPos())){
                damageTaken = DamageCalcUtils.crystalDamage(mc.player, entity.getPos());
            }else if(entity instanceof PlayerEntity && damageTaken < DamageCalcUtils.getSwordDamage((PlayerEntity) entity, true)){
                if(!FriendManager.INSTANCE.isTrusted((PlayerEntity) entity) && mc.player.getPos().distanceTo(entity.getPos()) < 5){
                    if(((PlayerEntity) entity).getActiveItem().getItem() instanceof SwordItem){
                        damageTaken = DamageCalcUtils.getSwordDamage((PlayerEntity) entity, true);
                    }
                }
            }
        }
        if(!ModuleManager.INSTANCE.get(NoFall.class).isActive() && mc.player.fallDistance > 3){
            double damage =mc.player.fallDistance * 0.5;
            if(damage > damageTaken){
                damageTaken = damage;
            }
        }
        if (Utils.getDimension() != Dimension.Overworld) {
            for (BlockEntity blockEntity : mc.world.blockEntities) {
                BlockPos bp = blockEntity.getPos();
                Vec3d pos = new Vec3d(bp.getX(), bp.getY(), bp.getZ());

                if (blockEntity instanceof BedBlockEntity && damageTaken < DamageCalcUtils.bedDamage(mc.player, pos)) {
                    damageTaken = DamageCalcUtils.bedDamage(mc.player, pos);
                }
            }
        }
        return damageTaken;
    }

    private final Listener<PostTickEvent> healthListener = new Listener<>(event -> {
        if(this.isActive()){
            disableHealthListener();
        }
       else if(mc.player != null && mc.world != null && !mc.player.isDead()){
           if(mc.player.getHealth() >= health.get()){
               this.toggle();
               disableHealthListener();
           }
       }
    });

    private void enableHealthListener(){
        PolluxClient.EVENT_BUS.subscribe(healthListener);
    }
    private void disableHealthListener(){
        PolluxClient.EVENT_BUS.unsubscribe(healthListener);
    }


}
