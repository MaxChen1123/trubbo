package com.maxchen.trubbo.remoting.netty.exchange;

import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.remoting.netty.api.Client;
import com.maxchen.trubbo.remoting.netty.exception.NettySendException;
import com.maxchen.trubbo.remoting.netty.exchange.api.ExchangeChannel;

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
        RpcFuture future;
        URL url = context.getUrl();
        //String timeout = url.getParameter(UrlConstant.TIMEOUT_KEY);
        String timeout = (String) request.getAttachment("timeout");
        if (timeout != null) {
            future = RpcFuture.newFuture(this, request, Long.parseLong(timeout));
        } else {
            future = RpcFuture.newFuture(this, request);
        }
        try {
            client.send(request);
        } catch (Exception e) {
            throw new NettySendException("");
        }
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
