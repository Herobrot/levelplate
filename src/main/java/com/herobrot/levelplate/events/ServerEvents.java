package com.herobrot.levelplate.events;

import com.herobrot.levelplate.Levelplate;
import com.herobrot.levelplate.util.LevelplateTracker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;


@EventBusSubscriber(modid = Levelplate.MOD_ID)
public class ServerEvents {
    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {

        if (event.getTarget() instanceof Mob mob && event.getEntity() instanceof ServerPlayer player) {
            LevelplateTracker.startTracking(mob, player);
        }
    }
}