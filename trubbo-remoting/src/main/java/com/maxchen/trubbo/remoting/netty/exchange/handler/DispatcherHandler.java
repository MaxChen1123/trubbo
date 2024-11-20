package com.maxchen.trubbo.remoting.netty.exchange.handler;

import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.common.util.NamedThreadFactory;
import com.maxchen.trubbo.remoting.netty.api.Channel;
import com.maxchen.trubbo.remoting.netty.api.ChannelHandler;
import com.maxchen.trubbo.remoting.netty.codec.protocol.TrubboHeader;
import com.maxchen.trubbo.remoting.netty.codec.protocol.TrubboMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DispatcherHandler extends AbstractChannelHandler {
    public DispatcherHandler(ChannelHandler handler) {
        super(handler);
    }

    //TODO configuration
    private static final ExecutorService DISPATCHER_EXECUTOR_SERVICE = new ThreadPoolExecutor(4, 8, 60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1000), new NamedThreadFactory("dispatcher-handler"), new ThreadPoolExecutor.AbortPolicy());

    @Override
    public void connected(Channel channel) {
        assert DISPATCHER_EXECUTOR_SERVICE != null;
        DISPATCHER_EXECUTOR_SERVICE.execute(() -> handler.connected(channel));
    }

    @Override
    public void disconnected(Channel channel) {
        assert DISPATCHER_EXECUTOR_SERVICE != null;
        DISPATCHER_EXECUTOR_SERVICE.execute(() -> handler.disconnected(channel));
    }

    @Override
    public void received(Channel channel, Object o) {
        if (o instanceof TrubboMessage message) {
            assert DISPATCHER_EXECUTOR_SERVICE != null;
            DISPATCHER_EXECUTOR_SERVICE.execute(() -> {
                RpcContext context = RpcContext.getContext();
                TrubboHeader.setContext(context, message.getHeader());
                handler.received(channel, message.getBody());
            });
        } else {
            log.warn("DispatcherHandler.received read unknown object {}", o);
        }
    }

    @Override
    public void caught(Channel channel, Throwable exception) {
        assert DISPATCHER_EXECUTOR_SERVICE != null;
        DISPATCHER_EXECUTOR_SERVICE.execute(() -> handler.caught(channel, exception));
    }

}
