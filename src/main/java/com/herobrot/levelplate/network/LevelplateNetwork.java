package com.herobrot.levelplate.network;

import com.herobrot.levelplate.Levelplate;
import com.herobrot.levelplate.data.LevelplateAttachments;
import com.herobrot.levelplate.network.payload.LevelPacket;
import com.herobrot.levelplate.network.payload.TitlePacket;

import com.herobrot.scalingdifficulty.api.DifficultyCalculator;
import com.herobrot.scalingdifficulty.data.DimensionDifficultyLoader;
import com.herobrot.scalingdifficulty.data.DimensionSettings;
import com.yungnickyoung.minecraft.travelerstitles.TravelersTitlesCommon;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import com.herobrot.scalingdifficulty.compat.LevelplateCompat;

public class LevelplateNetwork {
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Levelplate.MOD_ID);
        registrar.playBidirectional(TitlePacket.PACKET_ID, TitlePacket.STREAM_CODEC, LevelplateNetwork::handleTitlePacket);
        registrar.playToClient(LevelPacket.PACKET_ID, LevelPacket.STREAM_CODEC, LevelplateNetwork::handleLevelPacket);
    }

    @SuppressWarnings("resource")
    private static void handleTitlePacket(final TitlePacket payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow().isServerbound()) {
                if (context.player() instanceof ServerPlayer player) {
                    int mobLevel = 1;
                    if (Levelplate.isScalingDifficultyLoaded) {
                        float rawMultiplier = DifficultyCalculator.calculateRawMultiplier(player.serverLevel(), player.blockPosition(), false);
                        DimensionSettings settings = DimensionDifficultyLoader.getSettings(player.serverLevel().dimension().location().toString());
                        float cappedMultiplier = (float) Math.min(rawMultiplier, settings.maxFactorHealth);
                        mobLevel = LevelplateCompat.getLevelFromMultiplier(cappedMultiplier);
                    }
                    context.reply(new TitlePacket(mobLevel));
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