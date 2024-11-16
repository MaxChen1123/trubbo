package com.maxchen.trubbo.remoting.exchange;

import com.maxchen.trubbo.remoting.exchange.handler.DispatcherHandler;
import com.maxchen.trubbo.remoting.exchange.handler.HeaderExchangeHandler;
import com.maxchen.trubbo.remoting.exchange.handler.HeartBeatHandler;
import com.maxchen.trubbo.remoting.netty.api.ChannelHandler;

//this class is used to wrap the handler made in Protocol layer
public class ChannelHandlers {
    public static ChannelHandler getClientChannelHandler(ChannelHandler handler) {
        return new DispatcherHandler(new HeartBeatHandler(new HeaderExchangeHandler(handler)));
    }

    public static ChannelHandler getServerChannelHandler(ChannelHandler handler) {
        return new DispatcherHandler(new HeartBeatHandler(new HeaderExchangeHandler(handler)));
    }
}
