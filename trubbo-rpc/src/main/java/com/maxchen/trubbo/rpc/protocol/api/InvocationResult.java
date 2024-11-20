package com.maxchen.trubbo.rpc.protocol.api;

import com.maxchen.trubbo.remoting.netty.exchange.Response;

import java.util.concurrent.Future;

public interface InvocationResult {
    Response get();

    Future<Response> getFuture();
}

