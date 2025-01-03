package com.maxchen.trubbo.remoting.netty.codec;

import com.maxchen.trubbo.remoting.netty.codec.protocol.TrubboHeader;
import com.maxchen.trubbo.remoting.netty.codec.protocol.TrubboMessage;
import com.maxchen.trubbo.remoting.netty.codec.serialization.KryoSerializer;
import com.maxchen.trubbo.remoting.netty.codec.serialization.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrubboEncoder extends MessageToByteEncoder<TrubboMessage> {
    private final Serialization serializer = new KryoSerializer();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, TrubboMessage message, ByteBuf byteBuf) throws Exception {
        TrubboHeader header = message.getHeader();
        byte[] serialized;
        try {
            serialized = serializer.serialize(message.getBody());
        } catch (Exception e) {
            log.error("TrubboEncoder.encode error", e);
            return;
        }
        int size = serialized.length;
        byteBuf.writeShort(TrubboHeader.MAGIC);
        byteBuf.writeByte(header.getInfo());
        byteBuf.writeLong(header.getMessageId());
        byteBuf.writeInt(size);
        byteBuf.writeBytes(serialized);
    }
}
