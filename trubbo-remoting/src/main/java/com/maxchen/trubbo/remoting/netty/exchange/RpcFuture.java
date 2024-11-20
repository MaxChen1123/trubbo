package com.maxchen.trubbo.remoting.netty.exchange;

import com.maxchen.trubbo.common.exception.RpcTimeoutException;
import com.maxchen.trubbo.common.util.NamedThreadFactory;
import com.maxchen.trubbo.remoting.netty.api.Channel;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RpcFuture extends CompletableFuture<Response> {
    private static final Map<Long, RpcFuture> FUTURE_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, Timeout> TIMEOUT_MAP = new ConcurrentHashMap<>();
    public static Timer FUTURE_TIMER = new HashedWheelTimer(new NamedThreadFactory("future-timeout-detection"),
            100, TimeUnit.MILLISECONDS);
    @Getter
    private final long requestId;
    private final Channel channel;
    private final Request request;
    @Getter
    @Setter
    private volatile boolean isTimeout = false;
    @Getter
    @Setter
    private volatile boolean isCancelled = false;
    //ms | if <= 0, no timeout

    private RpcFuture(long requestId, Channel channel, Request request, long timeout) {
        this.requestId = requestId;
        this.channel = channel;
        this.request = request;
        Timeout newTimeout = FUTURE_TIMER.newTimeout(new FutureTimeoutTask(this), timeout, TimeUnit.MILLISECONDS);
        TIMEOUT_MAP.put(requestId, newTimeout);
    }

    private RpcFuture(long requestId, Channel channel, Request request) {
        this.requestId = requestId;
        this.channel = channel;
        this.request = request;
    }

    private void doReceive(Response response) {
        CHANNEL_MAP.remove(requestId);
        if (!isTimeout) {
            Timeout _timeout = TIMEOUT_MAP.get(requestId);
            if (_timeout != null) {
                _timeout.cancel();
            }
            this.complete(response);
        } else if (response.isException()) {
            this.completeExceptionally(response.getException());
        }
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

    public static RpcFuture newFuture(Channel channel, Request request, long timeout) {
        long id = request.getRequestId();
        RpcFuture future = new RpcFuture(id, channel, request, timeout);
        FUTURE_MAP.put(id, future);
        CHANNEL_MAP.put(id, channel);
        return future;
    }

    static class FutureTimeoutTask implements TimerTask {
        private final RpcFuture future;

        public FutureTimeoutTask(RpcFuture future) {
            this.future = future;
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            if (future.isCancelled()) {
                return;
            } else {
                future.setTimeout(true);
                Response timeoutResponse = Response.builder()
                        .requestId(future.getRequestId())
                        .isException(true)
                        .exception(new RpcTimeoutException("RpcFuture timeout, at " + LocalTime.now()))
                        .build();
                RpcFuture.receiveResponse(timeoutResponse);
            }
        }
    }

}

