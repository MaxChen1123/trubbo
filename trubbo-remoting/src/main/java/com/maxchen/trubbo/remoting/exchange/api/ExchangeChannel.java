package com.maxchen.trubbo.remoting.exchange.api;

import com.maxchen.trubbo.remoting.exchange.Response;
import com.maxchen.trubbo.remoting.netty.api.Channel;

import java.util.concurrent.Future;

public interface ExchangeChannel extends Channel {
    Future<Response> request(Object message);

    void response(Object message);

    void connect();

    void disconnect();
}
