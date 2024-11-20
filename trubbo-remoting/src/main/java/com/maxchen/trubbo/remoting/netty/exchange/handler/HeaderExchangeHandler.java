package com.maxchen.trubbo.remoting.netty.exchange.handler;

import com.maxchen.trubbo.remoting.netty.api.Channel;
import com.maxchen.trubbo.remoting.netty.api.ChannelHandler;
import com.maxchen.trubbo.remoting.netty.exchange.Response;
import com.maxchen.trubbo.remoting.netty.exchange.RpcFuture;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeaderExchangeHandler extends AbstractChannelHandler {
    public HeaderExchangeHandler(ChannelHandler handler) {
        super(handler);
    }

    @Override
    public void received(Channel channel, Object message) {
        if (message instanceof Response response) {
            RpcFuture.receiveResponse(response);
        }
        handler.received(channel, message);
    }

}
