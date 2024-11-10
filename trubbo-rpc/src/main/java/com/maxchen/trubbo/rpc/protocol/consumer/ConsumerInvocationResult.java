package com.maxchen.trubbo.rpc.protocol.consumer;

import com.maxchen.trubbo.remoting.exchange.Response;
import com.maxchen.trubbo.rpc.protocol.api.InvocationResult;
import lombok.AllArgsConstructor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@AllArgsConstructor
public class ConsumerInvocationResult implements InvocationResult {
    private Future<Response> future;

    @Override
    public Response get() {
        try {
            return future.get();
            //TODO 错误处理
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
