package com.maxchen.trubbo.remoting.netty.api;

public interface ChannelHandler {
    void connected(Channel channel);

    void disconnected(Channel channel);

    void received(Channel channel, Object message);

    void caught(Channel channel, Throwable exception);
}
