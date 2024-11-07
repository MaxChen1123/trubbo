package com.maxchen.trubbo.remoting.netty;

import com.maxchen.trubbo.remoting.netty.api.ChannelHandler;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServerHandler extends ChannelDuplexHandler {

    private ChannelHandler handler;

    public NettyServerHandler(ChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel connected, remoteAddress:{}", ctx.channel().remoteAddress());
        NettyChannel channel = NettyChannel.getChannel(ctx.channel());
        handler.connected(channel);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel disconnected, remoteAddress:{}", ctx.channel().remoteAddress());
        NettyChannel channel = NettyChannel.getChannel(ctx.channel());
        handler.disconnected(channel);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyChannel channel = NettyChannel.getChannel(ctx.channel());
        handler.received(channel, msg);
    }
}
