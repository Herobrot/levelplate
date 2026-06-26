package com.herobrot.levelplate;

import com.herobrot.levelplate.events.ClientEvents;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import com.herobrot.levelplate.config.LevelplateConfig;
import com.herobrot.levelplate.network.LevelplateNetwork;
import com.herobrot.levelplate.data.LevelplateAttachments; // Asegúrate de importar tu clase
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(Levelplate.MOD_ID)
public class Levelplate {

    public static final String MOD_ID = "levelplate";
    public static LevelplateConfig CONFIG;
    public static boolean isScalingDifficultyLoaded = false;

    public Levelplate(IEventBus modEventBus, ModContainer modContainer) {
        // Inicializar Configuración
        AutoConfig.register(LevelplateConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(LevelplateConfig.class).getConfig();

        // Verificar dependencias opcionales
        isScalingDifficultyLoaded = ModList.get().isLoaded("scalingdifficulty");

        // Registrar Eventos del Bus del Mod
        modEventBus.addListener(LevelplateNetwork::register);

        // AQUÍ ESTÁ LA SOLUCIÓN: Registrar los Attachments en el bus
        LevelplateAttachments.ATTACHMENT_TYPES.register(modEventBus);

        if (FMLEnvironment.dist.isClient()) {
            ClientEvents.registerConfigScreen(modContainer);
        }
    }
}