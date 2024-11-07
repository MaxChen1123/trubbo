package com.maxchen.trubbo.remoting.exchange;

import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.remoting.AbstractChannelHandler;
import com.maxchen.trubbo.remoting.netty.api.Channel;
import com.maxchen.trubbo.remoting.netty.api.ChannelHandler;

public class HeaderExchangeHandler extends AbstractChannelHandler {
    public HeaderExchangeHandler(ChannelHandler handler) {
        super(handler);
    }

    @Override
    public void received(Channel channel, Object message) {
        RpcContext context = RpcContext.getContext();
        if (!context.isRequest()) {
            RpcFuture.receiveResponse((Response) message);
        }
        handler.received(channel, message);
    }

}
