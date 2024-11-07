package com.maxchen.trubbo.remoting;

import com.maxchen.trubbo.remoting.netty.api.Channel;
import com.maxchen.trubbo.remoting.netty.api.ChannelHandler;

abstract public class AbstractChannelHandler implements ChannelHandler {
    protected final ChannelHandler handler;

    public AbstractChannelHandler(ChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    public void connected(Channel channel) {
        handler.connected(channel);
    }

    @Override
    public void disconnected(Channel channel) {
        handler.disconnected(channel);
    }

    @Override
    public void received(Channel channel, Object message) {
        handler.received(channel, message);
    }

    @Override
    public void caught(Channel channel, Throwable exception) {
        handler.caught(channel, exception);
    }
}