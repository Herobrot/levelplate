package com.herobrot.levelplate.waila;

import com.herobrot.levelplate.Levelplate;
import com.herobrot.levelplate.data.LevelplateAttachments;
import com.herobrot.levelplate.util.LevelplateRender;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.JadeIds;

@WailaPlugin
@SuppressWarnings("unused")
public class JadeCompat implements IWailaPlugin {

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(new LevelplateProvider(), Mob.class);
    }

    public static class LevelplateProvider implements IEntityComponentProvider {
        @Override
        public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
            if (accessor.getEntity() instanceof Mob mob) {
                if (Levelplate.CONFIG.showHostileOnly && !(mob instanceof net.minecraft.world.entity.monster.Enemy)) {
                    return;
                }
                String entityName = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
                if (Levelplate.CONFIG.excludedEntities.contains(entityName)) {
                    return;
                }
                LevelplateAttachments.MobLevelData mobData = mob.getData(LevelplateAttachments.MOB_DATA);
                if (mobData.showLabel) {
                    Component originalName = mob.getName();
                    int argb = LevelplateRender.parseHexColor(Levelplate.CONFIG.nameColor, 0xFFFFFFFF);
                    int rgb = argb & 0x00FFFFFF;
                    Style colorStyle = Style.EMPTY.withColor(rgb);
                    MutableComponent coloredName = originalName.copy().setStyle(colorStyle);
                    MutableComponent levelText = Component.translatable("text.levelplate.level", "§e" + mobData.level + "§r");
                    MutableComponent newCombinedName = levelText.append(" ").append(coloredName);
                    tooltip.replace(JadeIds.CORE_OBJECT_NAME, newCombinedName);
                }
            }
        }

        @Override
        public ResourceLocation getUid() {
            return ResourceLocation.fromNamespaceAndPath(Levelplate.MOD_ID, "mob_level_info");
        }
    }
}