package com.maxchen.trubbo.remoting.exchange;

import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.common.URL;
import com.maxchen.trubbo.remoting.exchange.api.ExchangeChannel;
import com.maxchen.trubbo.remoting.exchange.api.ExchangeClient;
import com.maxchen.trubbo.remoting.netty.NettyClient;
import com.maxchen.trubbo.remoting.netty.api.ChannelHandler;

import java.util.concurrent.Future;

public class HeaderExchangeClient implements ExchangeClient {
    private ExchangeChannel channel;

    public HeaderExchangeClient(ChannelHandler channelHandler) {
        RpcContext context = RpcContext.getContext();
        URL url = context.getUrl();
        String host = url.getHost();
        int port = url.getPort();

        channel = new HeaderExchangeChannel(
                new NettyClient(host, port, new HeaderExchangeHandler(channelHandler)));
    }

    @Override
    public void connect() {
        channel.connect();
    }

    @Override
    public void disconnect() {
        channel.disconnect();
    }

    @Override
    public void send(Object message) {
        channel.send(message);
    }

    @Override
    public Future<Response> request(Object message) {
        return channel.request(message);
    }

    @Override
    public void response(Object message) {
        throw new UnsupportedOperationException("Client cannot response");
    }
}
