package com.maxchen.trubbo.remoting.exchange;

import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.common.URL.UrlConstant;
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

        RpcFuture future;
        URL url = context.getUrl();
        String timeout = url.getParameter(UrlConstant.TIMEOUT_KEY);
        if (timeout != null) {
            future = RpcFuture.newFuture(this, request, Long.parseLong(timeout));
        } else {
            future = RpcFuture.newFuture(this, request);
        }
        client.send(message);
        return future;
    }

    @Override
    public void response(Object message) {
        if (!(message instanceof Response response)) {
            throw new IllegalArgumentException("message must be instance of Request");
        }
        RpcContext context = RpcContext.getContext();
        context.setRequest(false);
        context.setRequestId(response.getRequestId());
        client.send(message);
    }

    public void connect() {
        client.connect();
    }

    public void disconnect() {
        client.disconnect();
    }

    @Override
    public void send(Object message) {
        if (!(message instanceof Request request)) {
            throw new IllegalArgumentException("message must be instance of Request");
        }
        RpcContext context = RpcContext.getContext();
        context.setRequest(true);
        context.setRequestId(request.getRequestId());
        client.send(message);
    }

    public long getLastReadTime() {
        return client.getLastReadTime();
    }

    public long getLastWriteTime() {
        return client.getLastWriteTime();
    }

}
