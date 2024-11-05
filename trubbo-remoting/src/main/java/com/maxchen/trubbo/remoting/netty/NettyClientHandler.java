package com.maxchen.trubbo.remoting.netty;

import com.maxchen.trubbo.remoting.codec.protocol.TrubboMessage;
import com.maxchen.trubbo.remoting.netty.api.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<TrubboMessage> {
    private final ChannelHandler handler;

    public NettyClientHandler(ChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TrubboMessage o) throws Exception {
        log.info("NettyClientHandler.channelRead0 read {}", o);
        handler.received(NettyChannel.CHANNEL_MAP.get(channelHandlerContext.channel()), o);
    }
}
