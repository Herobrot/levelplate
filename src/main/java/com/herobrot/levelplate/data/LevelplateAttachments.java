package com.herobrot.levelplate.data;

import com.herobrot.levelplate.Levelplate;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class LevelplateAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Levelplate.MOD_ID);

    public static final Supplier<AttachmentType<MobLevelData>> MOB_DATA = ATTACHMENT_TYPES.register(
            "mob_data",
            () -> AttachmentType.builder(() -> new MobLevelData(1, true)).build()
    );

    public static class MobLevelData {
        public int level;
        public boolean showLabel;

        public MobLevelData(int level, boolean showLabel) {
            this.level = level;
            this.showLabel = showLabel;
        }
    }
}