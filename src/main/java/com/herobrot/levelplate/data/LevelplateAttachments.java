package com.herobrot.levelplate.data;

import com.herobrot.levelplate.Levelplate;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class LevelplateAttachments {

    // Registro diferido para los Attachments
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Levelplate.MOD_ID);

    // Creamos nuestro "Attachment" que contendrá la información del mob.
    // Por defecto iniciará en nivel 1 y con la etiqueta invisible.
    public static final Supplier<AttachmentType<MobLevelData>> MOB_DATA = ATTACHMENT_TYPES.register(
            "mob_data",
            () -> AttachmentType.builder(() -> new MobLevelData(1, true)).build()
    );

    // Objeto contenedor de datos (reemplaza las variables inyectadas de MobEntityAccess)
    public static class MobLevelData {
        public int level;
        public boolean showLabel;

        public MobLevelData(int level, boolean showLabel) {
            this.level = level;
            this.showLabel = showLabel;
        }
    }
}