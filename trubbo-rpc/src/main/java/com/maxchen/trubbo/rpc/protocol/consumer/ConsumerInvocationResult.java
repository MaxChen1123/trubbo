package com.maxchen.trubbo.rpc.protocol.consumer;

import com.maxchen.trubbo.remoting.netty.exchange.Response;
import com.maxchen.trubbo.rpc.protocol.api.InvocationResult;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@AllArgsConstructor
public class ConsumerInvocationResult implements InvocationResult {
    private Future<Response> future;

    @SneakyThrows
    @Override
    public Response get() {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw e.getCause();
        }
    }

    @Override
    public Future<Response> getFuture() {
        return future;
    }
}
