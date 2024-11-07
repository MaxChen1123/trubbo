package com.maxchen.trubbo.remoting.exchange;

import com.maxchen.trubbo.remoting.AbstractChannelHandler;
import com.maxchen.trubbo.remoting.netty.NettyChannel;
import com.maxchen.trubbo.remoting.netty.api.Channel;
import com.maxchen.trubbo.remoting.netty.api.ChannelHandler;

public class HeartBeatHandler extends AbstractChannelHandler {
    public HeartBeatHandler(ChannelHandler handler) {
        super(handler);
    }

    @Override
    public void connected(Channel channel) {
        refreshLastWriteTime(channel);
        refreshLastReadTime(channel);
        handler.connected(channel);
    }

    @Override
    public void received(Channel channel, Object message) {
        refreshLastReadTime(channel);
        handler.received(channel, message);
    }

    @Override
    public void sent(Channel channel, Object message) {
        refreshLastWriteTime(channel);
        handler.sent(channel, message);
    }

    private static void refreshLastReadTime(Channel channel) {
        ((NettyChannel) channel).setLastReadTime(System.currentTimeMillis());
    }

    private static void refreshLastWriteTime(Channel channel) {
        ((NettyChannel) channel).setLastWriteTime(System.currentTimeMillis());
    }
}
