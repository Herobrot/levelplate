package com.herobrot.levelplate.util;

import com.herobrot.levelplate.Levelplate;
import com.herobrot.levelplate.data.LevelplateAttachments;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import org.joml.Matrix4f;

import java.util.Objects;

public class LevelplateRender {

    private static final ResourceLocation ICONS = ResourceLocation.fromNamespaceAndPath(Levelplate.MOD_ID, "textures/icons.png");

    public static void renderNameplate(Mob mob, PoseStack poseStack, MultiBufferSource buffer, EntityRenderDispatcher dispatcher, Font font, boolean isVisible, int packedLight) {
        Minecraft client = Minecraft.getInstance();

        // Multiplicamos la distancia configurada (128) por sí misma = 16384
        double maxDistanceSqr = Levelplate.CONFIG.squaredDistance * Levelplate.CONFIG.squaredDistance;

        // CUIDADO: Si tienes el F1 presionado (hideGui), no se dibujará
        if (client.options.hideGui || !Levelplate.CONFIG.showLevel || dispatcher.distanceToSqr(mob) > maxDistanceSqr || mob.isVehicle()) {
            return;
        }

        LevelplateAttachments.MobLevelData mobData = mob.getData(LevelplateAttachments.MOB_DATA);

        if (isVisible && mobData.showLabel) {
            poseStack.pushPose();

            // 1. Elevar por encima del mob
            poseStack.translate(0.0D, mob.getBbHeight() + Levelplate.CONFIG.levelplateHeight, 0.0D);

            // 2. Rotar hacia el jugador
            poseStack.mulPose(dispatcher.cameraOrientation());

            // 3. LA ESCALA MÁGICA: X DEBE SER POSITIVO, Y DEBE SER NEGATIVO
            float scale = Math.abs(Levelplate.CONFIG.levelplateSize);
            if (scale < 0.001f) scale = 0.025f; // Fallback de seguridad
            poseStack.scale(scale, -scale, scale);

            if (Levelplate.CONFIG.healthBar) {
                poseStack.pushPose();
                poseStack.scale(1.5f, 1.5f, 1f);

                Matrix4f barMatrix = poseStack.last().pose();
                VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.text(ICONS));

                // Fondo
                drawQuad(barMatrix, vertexConsumer, 20, 0, 40, packedLight);

                float health = mob.getHealth() / mob.getMaxHealth();
                int healthWidth = Math.round(40 * health);

                // Relleno
                if (healthWidth > 0) {
                    poseStack.translate(0.0f, 0.0f, -0.01f);
                    drawQuad(poseStack.last().pose(), vertexConsumer, -20 + healthWidth, 6, healthWidth, packedLight);
                }

                poseStack.popPose();
                // Subir el texto para que no tape la barra
                poseStack.translate(0.0D, -9.0D, 0.0D);
            }

            Matrix4f matrix4f = poseStack.last().pose();
            float bgOpacity = client.options.getBackgroundOpacity(Levelplate.CONFIG.backgroundOpacity);
            int bgColor = (int) (bgOpacity * 255.0F) << 24;

            // FORZAR OPACIDAD 100% PARA EVITAR CONFIGURACIONES INVISIBLES
            int finalNameColor = Levelplate.CONFIG.nameColor | 0xFF000000;

            String mobName = mob.hasCustomName() ? Objects.requireNonNull(mob.getCustomName()).getString() : mob.getName().getString();

            if (Levelplate.CONFIG.showHealth) {
                mobName += " " + Component.translatable("text.levelplate.health", Math.round(mob.getHealth()), Math.round(mob.getMaxHealth())).getString();
            }

            String levelString = Component.translatable("text.levelplate.level", mobData.level).getString();
            Component text = Component.literal(levelString + " " + Component.translatable("text.levelplate.name", mobName).getString());

            float textX = (float) (-font.width(text) / 2);

            font.drawInBatch(text, textX, 0.0F, finalNameColor, true, matrix4f, buffer, Font.DisplayMode.SEE_THROUGH, bgColor, packedLight);
            font.drawInBatch(text, textX, 0.0F, finalNameColor, true, matrix4f, buffer, Font.DisplayMode.NORMAL, 0, packedLight);

            poseStack.popPose();
        }
    }

    private static void drawQuad(Matrix4f matrix, VertexConsumer consumer, float x2, float v, float width, int light) {
        float minU = (float) 0 / 256.0F;
        float maxU = ((float) 0 + width) / 256.0F;
        float minV = v / 256.0F;
        float maxV = (v + (float) 6) / 256.0F;

        consumer.addVertex(matrix, (float) -20, (float) 0, 0.0F).setColor(255, 255, 255, 255).setUv(minU, minV).setLight(light);
        consumer.addVertex(matrix, (float) -20, (float) 6, 0.0F).setColor(255, 255, 255, 255).setUv(minU, maxV).setLight(light);
        consumer.addVertex(matrix, x2, (float) 6, 0.0F).setColor(255, 255, 255, 255).setUv(maxU, maxV).setLight(light);
        consumer.addVertex(matrix, x2, (float) 0, 0.0F).setColor(255, 255, 255, 255).setUv(maxU, minV).setLight(light);
    }
}