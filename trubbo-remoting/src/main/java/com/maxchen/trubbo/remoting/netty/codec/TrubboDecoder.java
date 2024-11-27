package com.maxchen.trubbo.remoting.netty.codec;

import com.maxchen.trubbo.common.TrubboProtocolConstant;
import com.maxchen.trubbo.common.util.TrubboProtocolUtil;
import com.maxchen.trubbo.remoting.netty.codec.protocol.TrubboHeader;
import com.maxchen.trubbo.remoting.netty.codec.protocol.TrubboMessage;
import com.maxchen.trubbo.remoting.netty.codec.serialization.KyroSerializer;
import com.maxchen.trubbo.remoting.netty.codec.serialization.Serialization;
import com.maxchen.trubbo.remoting.netty.exchange.Request;
import com.maxchen.trubbo.remoting.netty.exchange.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class TrubboDecoder extends ByteToMessageDecoder {
    private final Serialization serializer = new KyroSerializer();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < TrubboProtocolConstant.HEADER_LENGTH) {
            return;
        }
        byteBuf.markReaderIndex();
        short magic = byteBuf.readShort();
        if (magic != TrubboHeader.MAGIC) {
            byteBuf.resetReaderIndex();
            throw new RuntimeException("Unknown magic code:" + magic);
        }
        byte info = byteBuf.readByte();
        long messageId = byteBuf.readLong();
        int size = byteBuf.readInt();
        if (byteBuf.readableBytes() < size) {
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] bytes = new byte[size];
        byteBuf.readBytes(bytes);
        Class<?> clazz;
        if (TrubboProtocolUtil.isRequest(info)) {
            clazz = Request.class;
        } else {
            clazz = Response.class;
        }
        Object object = null;
        try {
            object = serializer.deserialize(bytes, clazz);
        } catch (Exception e) {
            log.error("TrubboDecoder.decode error", e);
            return;
        }
        TrubboMessage trubboMessage = new TrubboMessage(new TrubboHeader(info, messageId, size), object);
        list.add(trubboMessage);
    }
}
