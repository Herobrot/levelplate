package com.herobrot.levelplate.mixin.compat;

import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import net.neoforged.fml.loading.FMLLoader;

public class LevelplateMixinPlugin implements IMixinConfigPlugin {
    private boolean isModLoaded(String modId) {
        return FMLLoader.getLoadingModList().getMods().stream()
                .anyMatch(info -> info.getModId().equals(modId));
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if ((mixinClassName.contains("compat.GeoEntityRendererMixin") || mixinClassName.contains("compat.GeoReplacedEntityRendererMixin")) && !isModLoaded("geckolib")) {
            return false;
        }
        return !mixinClassName.contains("compat.TitleRenderManagerMixin") || isModLoaded("travelerstitles");
    }

    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}