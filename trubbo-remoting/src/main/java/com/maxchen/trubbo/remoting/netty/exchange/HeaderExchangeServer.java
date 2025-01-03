package com.maxchen.trubbo.remoting.netty.exchange;

import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.remoting.netty.NettyServer;
import com.maxchen.trubbo.remoting.netty.api.ChannelHandler;
import com.maxchen.trubbo.remoting.netty.api.Server;
import com.maxchen.trubbo.remoting.netty.exchange.api.ExchangeServer;

public class HeaderExchangeServer implements ExchangeServer {
    private final Server nettyServer;

    public HeaderExchangeServer(ChannelHandler handler) {
        RpcContext context = RpcContext.getContext();
        int port = context.getUrl().getPort();
        this.nettyServer = new NettyServer(port, ChannelHandlers.getServerChannelHandler(handler));
    }

    @Override
    public void bind() {
        nettyServer.bind();
    }

    @Override
    public void close() {
        nettyServer.close();
    }

    @Override
    public int getPort() {
        return nettyServer.getPort();
    }
}
