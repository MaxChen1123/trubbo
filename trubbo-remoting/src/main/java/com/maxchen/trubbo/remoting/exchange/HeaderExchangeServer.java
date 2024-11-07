package com.maxchen.trubbo.remoting.exchange;

import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.remoting.exchange.api.ExchangeServer;
import com.maxchen.trubbo.remoting.netty.NettyServer;
import com.maxchen.trubbo.remoting.netty.api.ChannelHandler;
import com.maxchen.trubbo.remoting.netty.api.Server;

public class HeaderExchangeServer implements ExchangeServer {
    private final Server nettyServer;

    public HeaderExchangeServer(ChannelHandler handler) {
        RpcContext context = RpcContext.getContext();
        int port = context.getUrl().getPort();
        this.nettyServer = new NettyServer(port, handler);
    }

    @Override
    public void bind() {
        nettyServer.bind();
    }

    @Override
    public void close() {
        nettyServer.close();
    }
}
