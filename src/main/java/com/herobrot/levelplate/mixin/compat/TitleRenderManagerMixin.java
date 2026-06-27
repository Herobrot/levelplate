package com.herobrot.levelplate.mixin.compat;

import com.yungnickyoung.minecraft.travelerstitles.render.TitleRenderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import com.herobrot.levelplate.Levelplate;
import com.herobrot.levelplate.network.payload.TitlePacket;
import net.neoforged.neoforge.network.PacketDistributor;

@Mixin(value = TitleRenderManager.class, remap = false)
public class TitleRenderManagerMixin {
    @Inject(
            method = "updateBiomeTitle",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/yungnickyoung/minecraft/travelerstitles/render/TitleRenderer;addRecentEntry(Ljava/lang/Object;)V"
            )
    )
    private void updateBiomeTitleMixin(Level world, BlockPos playerPos, Player player, boolean isPlayerUnderground, CallbackInfo ci) {
        if (Levelplate.CONFIG.levelTitle) {
            PacketDistributor.sendToServer(new TitlePacket(0));
        }
    }
}