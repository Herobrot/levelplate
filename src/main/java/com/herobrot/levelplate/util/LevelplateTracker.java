package com.herobrot.levelplate.util;

import com.herobrot.levelplate.Levelplate;
import com.herobrot.levelplate.compat.CobblemonAPI;
import com.herobrot.levelplate.data.LevelplateAttachments;
import com.herobrot.levelplate.network.payload.LevelPacket;

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
        String entityName = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        int mobLevel = getMobLevel(mob, entityName);
        mob.setData(LevelplateAttachments.MOB_DATA, new LevelplateAttachments.MobLevelData(mobLevel, true));
        PacketDistributor.sendToPlayer(player, new LevelPacket(mobLevel, mob.getId(), true));
    }

    @SuppressWarnings("unchecked")
    public static int getMobLevel(Mob mob, String entityName) {
        if (Levelplate.isCobblemonLoaded && entityName.equals("cobblemon:pokemon")) {
            return CobblemonAPI.getLevel(mob, 1);
        }
        int level = 1;
        if (Levelplate.isScalingDifficultyLoaded && Levelplate.CONFIG.useScalingDifficultyLvl) {
            double maxHealth = mob.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? mob.getAttributeValue(Attributes.MAX_HEALTH) : 0.0;
            double baseHealth = mob.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? mob.getAttributeBaseValue(Attributes.MAX_HEALTH) : 1.0;
            float multiplier = 1.0f;
            if (baseHealth > 0) {
                multiplier = (float) (maxHealth / baseHealth);
            }
            if (multiplier <= 0.0f) multiplier = 1.0f;
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