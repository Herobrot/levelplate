package com.herobrot.levelplate.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record LevelPacket(int mobLevel, int mobId, boolean hasRpgLabel) implements CustomPacketPayload {

    public static final Type<LevelPacket> PACKET_ID = new Type<>(ResourceLocation.fromNamespaceAndPath("levelplate", "level_packet"));

    // Uso correcto de StreamCodec para NeoForge 1.21.1
    public static final StreamCodec<RegistryFriendlyByteBuf, LevelPacket> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeInt(payload.mobLevel());
                buf.writeInt(payload.mobId());
                buf.writeBoolean(payload.hasRpgLabel());
            },
            buf -> new LevelPacket(buf.readInt(), buf.readInt(), buf.readBoolean())
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}