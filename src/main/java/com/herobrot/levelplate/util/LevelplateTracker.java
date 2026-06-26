package com.herobrot.levelplate.util;

import com.herobrot.levelplate.Levelplate;
import com.herobrot.levelplate.data.LevelplateAttachments;
import com.herobrot.levelplate.network.payload.LevelPacket;
import com.herobrot.scalingdifficulty.data.ModAttachments;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.neoforged.neoforge.network.PacketDistributor;

public class LevelplateTracker {

    public static void startTracking(Mob mob, ServerPlayer player) {
        LevelplateAttachments.MobLevelData mobData = mob.getData(LevelplateAttachments.MOB_DATA);

        // Verificamos si la entidad está excluida en la configuración
        String entityName = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        boolean isExcluded = Levelplate.CONFIG.excludedEntities.contains(entityName);

        // Si no está excluida, forzamos que muestre la etiqueta
        if (!isExcluded) {
            mobData.showLabel = true;
        }

        if (mobData.showLabel) {
            int mobLevel = getMobLevel(mob);

            mob.setData(LevelplateAttachments.MOB_DATA, new LevelplateAttachments.MobLevelData(mobLevel, true));
            PacketDistributor.sendToPlayer(player, new LevelPacket(mobLevel, mob.getId(), true));
        }
    }

    @SuppressWarnings("unchecked")
    public static int getMobLevel(Mob mob) {
        int level = 1;

        if (Levelplate.isScalingDifficultyLoaded && Levelplate.CONFIG.useRpgDifficultyLvl) {
            float multiplier = mob.getData(ModAttachments.DIFFICULTY_MULTIPLIER);
            if (multiplier == 0.0f) multiplier = 1.0f;

            level = (int) (Levelplate.CONFIG.levelMultiplier * multiplier - Levelplate.CONFIG.levelMultiplier);
        } else {
            EntityType<? extends LivingEntity> type = (EntityType<? extends LivingEntity>) mob.getType();

            if (DefaultAttributes.hasSupplier(type)) {
                double baseMaxHealth = DefaultAttributes.getSupplier(type).getBaseValue(Attributes.MAX_HEALTH);
                if (baseMaxHealth > 0) {
                    level = (int) (Levelplate.CONFIG.levelMultiplier * mob.getAttributeBaseValue(Attributes.MAX_HEALTH) / baseMaxHealth) - Levelplate.CONFIG.levelMultiplier + 1;
                }
            }
        }

        return Math.max(level, 1);
    }
}