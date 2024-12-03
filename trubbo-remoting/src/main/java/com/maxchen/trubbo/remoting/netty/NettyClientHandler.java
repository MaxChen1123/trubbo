package com.maxchen.trubbo.remoting.netty;

import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.remoting.netty.api.Channel;
import com.maxchen.trubbo.remoting.netty.api.ChannelHandler;
import com.maxchen.trubbo.remoting.netty.codec.protocol.TrubboHeader;
import com.maxchen.trubbo.remoting.netty.codec.protocol.TrubboMessage;
import com.maxchen.trubbo.remoting.netty.exchange.Response;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClientHandler extends ChannelDuplexHandler {
    private final ChannelHandler handler;

    public NettyClientHandler(ChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object o) throws Exception {
        if (o instanceof TrubboMessage message) {
            log.debug("NettyClientHandler.channelRead0 read {}", o);
            handler.received(NettyChannel.getChannel(ctx.channel()), message);
        } else {
            log.warn("NettyClientHandler.channelRead0 read unknown object {}", o);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel connected, remoteAddress:{}", ctx.channel().remoteAddress());
        handler.connected(NettyChannel.getChannel(ctx.channel()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel disconnected, remoteAddress:{}", ctx.channel().remoteAddress());
        handler.disconnected(NettyChannel.getChannel(ctx.channel()));
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        if (msg instanceof TrubboMessage message) {
            final Channel channel = NettyChannel.CHANNEL_MAP.get(ctx.channel());
            TrubboHeader header = message.getHeader();
            boolean isRequest = RpcContext.getContext().isRequest();

            promise.addListener(future -> {
                if (future.isSuccess()) {
                    handler.sent(channel, msg);
                    return;
                }
                Throwable t = future.cause();
                if (t != null && isRequest) {
                    Response response = Response.builder()
                            .requestId(header.getMessageId())
                            .isException(true)
                            .exception(t)
                            .build();
                    handler.received(channel, response);
                }
            });
        } else {
            log.warn("NettyClientHandler.write unknown object {}", msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("NettyClientHandler.exceptionCaught caught exception", cause);
        handler.caught(NettyChannel.getChannel(ctx.channel()), cause);
    }
}
