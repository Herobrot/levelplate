package com.herobrot.levelplate.waila;

import com.herobrot.levelplate.Levelplate;
import com.herobrot.levelplate.data.LevelplateAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
@SuppressWarnings("unused")
public class JadeCompat implements IWailaPlugin {

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        // Le indicamos a Jade que ejecute nuestra lógica cuando el jugador mire un Mob
        registration.registerEntityComponent(new LevelplateProvider(), Mob.class);
    }

    public static class LevelplateProvider implements IEntityComponentProvider {

        @Override
        public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
            if (accessor.getEntity() instanceof Mob mob) {
                // Recuperamos el Attachment sin hacer cálculos pesados en el cliente
                LevelplateAttachments.MobLevelData mobData = mob.getData(LevelplateAttachments.MOB_DATA);

                if (mobData.showLabel) {
                    // Inyectamos el nivel directamente en el HUD del cliente
                    tooltip.add(Component.translatable("text.levelplate.level", String.valueOf(mobData.level)));
                }
            }
        }

        @Override
        public ResourceLocation getUid() {
            return ResourceLocation.fromNamespaceAndPath(Levelplate.MOD_ID, "level_info");
        }
    }
}