package com.maxchen.trubbo.remoting.netty.exchange.api;

import com.maxchen.trubbo.remoting.netty.api.Channel;
import com.maxchen.trubbo.remoting.netty.exchange.Response;

import java.util.concurrent.Future;

public interface ExchangeChannel extends Channel {
    Future<Response> request(Object message);

    void response(Object message);

    void connect();

    void disconnect();
}
