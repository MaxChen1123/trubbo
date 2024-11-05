package com.maxchen.trubbo.remoting.exchange;

import com.maxchen.trubbo.remoting.netty.api.Channel;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class RpcFuture extends CompletableFuture<Response> {
    private static final Map<Long, RpcFuture> FUTURE_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();

    private final long requestId;
    private final Channel channel;
    private final Request request;
    // TODO
    private final long timeout; //ms | if <= 0, no timeout

    public RpcFuture(long requestId, Channel channel, Request request, long timeout) {
        this.requestId = requestId;
        this.channel = channel;
        this.request = request;
        this.timeout = timeout;
    }

    public RpcFuture(long requestId, Channel channel, Request request) {
        this.requestId = requestId;
        this.channel = channel;
        this.request = request;
        this.timeout = -1;
    }

    private void doReceive(Response response) {
        CHANNEL_MAP.remove(requestId);
        this.complete(response);
    }

    public static void receiveResponse(Response response) {
        RpcFuture future = FUTURE_MAP.remove(response.getRequestId());
        if (future != null) {
            future.doReceive(response);
        }
    }


    public static RpcFuture newFuture(Channel channel, Request request) {
        long id = request.getRequestId();
        RpcFuture future = new RpcFuture(id, channel, request);
        FUTURE_MAP.put(id, future);
        CHANNEL_MAP.put(id, channel);
        return future;
    }


}
