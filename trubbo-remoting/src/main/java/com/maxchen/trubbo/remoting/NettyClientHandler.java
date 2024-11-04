package com.maxchen.trubbo.remoting;

import com.maxchen.trubbo.remoting.api.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler {
    private final ChannelHandler handler;
    public NettyClientHandler(ChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        log.info("NettyClientHandler.channelRead0 read {}", o);
        handler.received(new NettyChannel(),o);
    }
}
