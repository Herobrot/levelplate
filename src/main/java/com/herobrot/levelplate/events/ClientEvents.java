package com.herobrot.levelplate.events;

import com.herobrot.levelplate.Levelplate;
import com.herobrot.levelplate.config.LevelplateConfig;
import com.herobrot.levelplate.util.LevelplateRender;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Mob;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(modid = Levelplate.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    public static void registerConfigScreen(ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (container, parentScreen) ->
                AutoConfig.getConfigScreen(LevelplateConfig.class, parentScreen).get());
    }

    @SubscribeEvent
    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
        if (event.getEntity() instanceof Mob mob) {
            Minecraft client = Minecraft.getInstance();
            LevelplateRender.renderNameplate(mob, event.getPoseStack(), event.getMultiBufferSource(), client.getEntityRenderDispatcher(), client.font, !mob.isInvisibleTo(client.player), event.getPackedLight());
        }
    }
}