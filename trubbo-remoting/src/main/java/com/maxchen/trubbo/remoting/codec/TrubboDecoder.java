package com.maxchen.trubbo.remoting.codec;

import com.maxchen.trubbo.common.TrubboProtocolConstant;
import com.maxchen.trubbo.remoting.codec.protocol.TrubboHeader;
import com.maxchen.trubbo.remoting.codec.protocol.TrubboMessage;
import com.maxchen.trubbo.remoting.codec.serialization.HessianSerializer;
import com.maxchen.trubbo.remoting.codec.serialization.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class TrubboDecoder extends ByteToMessageDecoder {
    private final Serialization serializer =new HessianSerializer();
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes()< TrubboProtocolConstant.HEADER_LENGTH){
            return;
        }
        byteBuf.markReaderIndex();
        short magic=byteBuf.readShort();
        if(magic!= TrubboHeader.MAGIC){
            byteBuf.resetReaderIndex();
            throw new RuntimeException("Unknown magic code:"+magic);
        }
        byte info=byteBuf.readByte();
        long messageId=byteBuf.readLong();
        int size=byteBuf.readInt();
        if(byteBuf.readableBytes()<size){
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] bytes=new byte[size];
        byteBuf.readBytes(bytes);
        // TODO
        Object object=serializer.deserialize(bytes,Object.class);
        TrubboMessage trubboMessage = new TrubboMessage(new TrubboHeader(info, messageId, size), object);
        list.add(trubboMessage);
    }
}
