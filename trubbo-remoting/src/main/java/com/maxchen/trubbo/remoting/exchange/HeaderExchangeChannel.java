package com.maxchen.trubbo.remoting.exchange;

import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.remoting.exchange.api.ExchangeChannel;
import com.maxchen.trubbo.remoting.netty.api.Client;

import java.util.concurrent.Future;

public class HeaderExchangeChannel implements ExchangeChannel {
    private final Client client;

    public HeaderExchangeChannel(Client client) {
        this.client = client;
    }

    @Override
    public Future<Response> request(Object message) {
        if (!(message instanceof Request request)) {
            throw new IllegalArgumentException("message must be instance of Request");
        }
        RpcContext context = RpcContext.getContext();
        context.setRequest(true);
        context.setRequestId(request.getRequestId());
        client.send(message);
        return RpcFuture.newFuture(this, request);
    }

    public void connect() {
        client.connect();
    }

    public void disconnect() {
        client.disconnect();
    }

    @Override
    public void send(Object message) {
        client.send(message);
    }

    @Override
    public void close() {
        client.disconnect();
    }
}
