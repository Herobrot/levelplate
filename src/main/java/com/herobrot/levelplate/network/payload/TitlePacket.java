package com.herobrot.levelplate.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record TitlePacket(int level) implements CustomPacketPayload {

    public static final Type<TitlePacket> PACKET_ID = new Type<>(ResourceLocation.fromNamespaceAndPath("levelplate", "title_packet"));

    // Uso correcto de StreamCodec para NeoForge 1.21.1
    public static final StreamCodec<RegistryFriendlyByteBuf, TitlePacket> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeInt(payload.level()),
            buf -> new TitlePacket(buf.readInt())
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}