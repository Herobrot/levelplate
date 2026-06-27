package com.herobrot.levelplate.network;

import com.herobrot.levelplate.Levelplate;
import com.herobrot.levelplate.data.LevelplateAttachments;
import com.herobrot.levelplate.network.payload.LevelPacket;
import com.herobrot.levelplate.network.payload.TitlePacket;
import com.herobrot.levelplate.util.LevelplateTracker;

import com.herobrot.scalingdifficulty.api.DifficultyCalculator;

import com.yungnickyoung.minecraft.travelerstitles.TravelersTitlesCommon;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Skeleton;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class LevelplateNetwork {
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Levelplate.MOD_ID);

        registrar.playBidirectional(TitlePacket.PACKET_ID, TitlePacket.STREAM_CODEC, LevelplateNetwork::handleTitlePacket);
        registrar.playToClient(LevelPacket.PACKET_ID, LevelPacket.STREAM_CODEC, LevelplateNetwork::handleLevelPacket);
    }

    private static void handleTitlePacket(final TitlePacket payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow().isServerbound()) {
                if (context.player() instanceof ServerPlayer player) {
                    Skeleton skeleton = EntityType.SKELETON.create(player.serverLevel());
                    if (skeleton != null) {
                        skeleton.moveTo(player.getX(), player.getY(), player.getZ(), 0.0f, 0.0f);

                        if (Levelplate.isScalingDifficultyLoaded) {
                            DifficultyCalculator.applyScaling(skeleton, player.serverLevel());
                        }
                        String entityName = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(skeleton.getType()).toString();
                        context.reply(new TitlePacket(LevelplateTracker.getMobLevel(skeleton, entityName)));

                        skeleton.discard();
                    }
                }
            } else {
                int mobLevel = payload.level();
                if (TravelersTitlesCommon.titleManager.biomeTitleRenderer.displayedTitle != null) {
                    TravelersTitlesCommon.titleManager.biomeTitleRenderer.displayTitle(
                            TravelersTitlesCommon.titleManager.biomeTitleRenderer.displayedTitle,
                            Component.translatable("text.scalingdifficulty.title", mobLevel)
                    );
                }
            }
        });
    }

    @SuppressWarnings("resource")
    private static void handleLevelPacket(final LevelPacket payload, final IPayloadContext context) {
        context.enqueueWork(() -> {

            if (context.player().level().getEntity(payload.mobId()) instanceof Mob mob) {

                mob.setData(LevelplateAttachments.MOB_DATA, new LevelplateAttachments.MobLevelData(payload.mobLevel(), payload.hasRpgLabel()));
            }
        });
    }
}