package com.maxchen.trubbo.remoting.netty.codec;

import com.maxchen.trubbo.remoting.netty.codec.protocol.TrubboHeader;
import com.maxchen.trubbo.remoting.netty.codec.protocol.TrubboMessage;
import com.maxchen.trubbo.remoting.netty.codec.serialization.KyroSerializer;
import com.maxchen.trubbo.remoting.netty.codec.serialization.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class TrubboEncoder extends MessageToByteEncoder<TrubboMessage> {
    private final Serialization serializer = new KyroSerializer();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, TrubboMessage message, ByteBuf byteBuf) throws Exception {
        TrubboHeader header = message.getHeader();
        byte[] serialized = serializer.serialize(message.getBody());
        int size = serialized.length;
        byteBuf.writeShort(TrubboHeader.MAGIC);
        byteBuf.writeByte(header.getInfo());
        byteBuf.writeLong(header.getMessageId());
        byteBuf.writeInt(size);
        byteBuf.writeBytes(serialized);
    }
}
