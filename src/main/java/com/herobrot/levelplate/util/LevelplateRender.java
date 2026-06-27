package com.herobrot.levelplate.util;

import com.herobrot.levelplate.Levelplate;
import com.herobrot.levelplate.compat.CobblemonAPI;
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
    private static int debugSpamLimiter = 0;

    public static int parseHexColor(String hex, int defaultColor) {
        try {
            if (hex.startsWith("#")) hex = hex.substring(1);
            if (hex.length() == 6) hex = "FF" + hex;
            return (int) Long.parseLong(hex, 16);
        } catch (Exception e) {
            return defaultColor;
        }
    }

    public static void renderNameplate(Mob mob, PoseStack poseStack, MultiBufferSource buffer, EntityRenderDispatcher dispatcher, Font font, boolean isVisible, int packedLight) {
        Minecraft client = Minecraft.getInstance();

        double maxDistanceSqr = Levelplate.CONFIG.renderDistance * Levelplate.CONFIG.renderDistance;

        if (client.options.hideGui || !Levelplate.CONFIG.showLevel || dispatcher.distanceToSqr(mob) > maxDistanceSqr || mob.isVehicle()) {
            return;
        }

        if (!Levelplate.CONFIG.showLevelplateIfObstructed) {
            assert client.player != null;
            if (!client.player.hasLineOfSight(mob)) {
                return;
            }
        }

        LevelplateAttachments.MobLevelData mobData = mob.getData(LevelplateAttachments.MOB_DATA);

        if (isVisible && mobData.showLabel) {
            poseStack.pushPose();

            poseStack.translate(0.0D, mob.getBbHeight() + Levelplate.CONFIG.levelplateHeight, 0.0D);
            poseStack.mulPose(dispatcher.cameraOrientation());

            float scale = Math.abs(Levelplate.CONFIG.levelplateSize);
            if (scale < 0.001f) scale = 0.025f;
            poseStack.scale(scale, -scale, scale);

            if (Levelplate.CONFIG.healthBar) {
                poseStack.pushPose();
                poseStack.scale(1.5f, 1.5f, 1f);

                Matrix4f barMatrix = poseStack.last().pose();
                VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.text(ICONS));

                drawQuad(barMatrix, vertexConsumer, 20, 0, 40, packedLight);

                float health = mob.getHealth() / mob.getMaxHealth();
                int healthWidth = Math.round(40 * health);

                if (healthWidth > 0) {
                    poseStack.translate(0.0f, 0.0f, 0.01f);
                    drawQuad(poseStack.last().pose(), vertexConsumer, -20 + healthWidth, 6, healthWidth, packedLight);
                }

                poseStack.popPose();
                poseStack.translate(0.0D, -9.0D, 0.0D);
            }

            int parsedBgColor = parseHexColor(Levelplate.CONFIG.backgroundColor, 0xFF000000);
            int bgR = (parsedBgColor >> 16) & 0xFF;
            int bgG = (parsedBgColor >> 8) & 0xFF;
            int bgB = parsedBgColor & 0xFF;

            int alpha = (int) (Math.max(0.0f, Math.min(1.0f, Levelplate.CONFIG.backgroundOpacity)) * 255.0F);

            int finalNameColor = parseHexColor(Levelplate.CONFIG.nameColor, 0xFFFFFFFF);
            Matrix4f matrix4f = poseStack.last().pose();

            String mobName = mob.hasCustomName() ? Objects.requireNonNull(mob.getCustomName()).getString() : mob.getName().getString();

            if (Levelplate.isCobblemonLoaded) {
                mobName = CobblemonAPI.getName(mob, mobName);
            }

            if (Levelplate.CONFIG.showHealth && !mobName.equals("???")) {
                mobName += " " + Component.translatable("text.levelplate.health", Math.round(mob.getHealth()), Math.round(mob.getMaxHealth())).getString();
            }

            String levelString = Component.translatable("text.levelplate.level", Component.literal(String.valueOf(mobData.level))).getString();
            Component text = Component.literal(levelString + " " + Component.translatable("text.levelplate.name", mobName).getString());

            float textX = (float) (-font.width(text) / 2);
            float textWidth = font.width(text);
            float textHeight = font.lineHeight;
            float padding = 3.0f;

            if (Levelplate.CONFIG.debugMode && (debugSpamLimiter++ % 60 == 0)) {
                System.out.println("[Levelplate DEBUG] Renderizando Mob: " + mobName);
                System.out.println("-> Color de Fondo Configurado: " + Levelplate.CONFIG.backgroundColor);
                System.out.println("-> RGB Interpretado: R=" + bgR + " G=" + bgG + " B=" + bgB + " Alpha=" + alpha);
                System.out.println("-> Posición Z: -0.01D (Detrás del texto)");
            }

            if (alpha > 0) {
                poseStack.pushPose();
                poseStack.translate(0.0D, 0.0D, -0.01D);

                Matrix4f bgMatrix = poseStack.last().pose();

                VertexConsumer bgConsumer = buffer.getBuffer(RenderType.textBackground());

                drawSolidRect(bgMatrix, bgConsumer,
                        textX - padding, -padding,
                        textWidth + (padding * 2), textHeight + (padding * 2),
                        bgR, bgG, bgB, alpha, packedLight);

                poseStack.popPose();
            }

            if (Levelplate.CONFIG.showLevelplateIfObstructed) {
                font.drawInBatch(text, textX, 0.0F, finalNameColor, true, matrix4f, buffer, Font.DisplayMode.SEE_THROUGH, 0, packedLight);
            }

            font.drawInBatch(text, textX, 0.0F, finalNameColor, true, matrix4f, buffer, Font.DisplayMode.NORMAL, 0, packedLight);
            poseStack.popPose();
        }
    }

    private static void drawQuad(Matrix4f matrix, VertexConsumer consumer, float x2, float v, float width, int light) {
        float minU = 0 / 256.0F;
        float maxU = width / 256.0F;
        float minV = v / 256.0F;
        float maxV = (v + 6.0F) / 256.0F;

        consumer.addVertex(matrix, -20.0F, 0.0F, 0.0F).setColor(255, 255, 255, 255).setUv(minU, minV).setLight(light);
        consumer.addVertex(matrix, -20.0F, 6.0F, 0.0F).setColor(255, 255, 255, 255).setUv(minU, maxV).setLight(light);
        consumer.addVertex(matrix, x2, 6.0F, 0.0F).setColor(255, 255, 255, 255).setUv(maxU, maxV).setLight(light);
        consumer.addVertex(matrix, x2, 0.0F, 0.0F).setColor(255, 255, 255, 255).setUv(maxU, minV).setLight(light);
    }

    private static void drawSolidRect(Matrix4f matrix, VertexConsumer consumer, float x, float y, float width, float height, int r, int g, int b, int alpha, int light) {
        consumer.addVertex(matrix, x, y, 0.0F).setColor(r, g, b, alpha).setLight(light);
        consumer.addVertex(matrix, x, y + height, 0.0F).setColor(r, g, b, alpha).setLight(light);
        consumer.addVertex(matrix, x + width, y + height, 0.0F).setColor(r, g, b, alpha).setLight(light);
        consumer.addVertex(matrix, x + width, y, 0.0F).setColor(r, g, b, alpha).setLight(light);
    }
}