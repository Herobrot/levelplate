package com.herobrot.levelplate.mixin.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import com.herobrot.levelplate.util.LevelplateRender;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@Mixin(GeoEntityRenderer.class)
public abstract class GeoEntityRendererMixin<T extends Entity & GeoAnimatable> {
    @Inject(method = "renderFinal*", at = @At("HEAD"), remap = false)
    private void renderFinalMixin(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int colour, CallbackInfo info) {
        if (animatable instanceof Mob mob) {
            Minecraft client = Minecraft.getInstance();
            assert client.player != null;
            boolean isVisible = !mob.isInvisibleTo(client.player);

            LevelplateRender.renderNameplate(
                    mob,
                    poseStack,
                    bufferSource,
                    client.getEntityRenderDispatcher(),
                    client.font,
                    isVisible,
                    packedLight
            );
        }
    }

    @Inject(method = "shouldShowName(Lnet/minecraft/world/entity/Entity;)Z", at = @At("RETURN"), cancellable = true)
    private void shouldShowNameMixin(T entity, CallbackInfoReturnable<Boolean> info) {
        if (info.getReturnValue() && entity instanceof Mob) {
            info.setReturnValue(false);
        }
    }
}