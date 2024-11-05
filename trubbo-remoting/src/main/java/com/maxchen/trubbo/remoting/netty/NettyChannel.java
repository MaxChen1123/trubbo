package com.maxchen.trubbo.remoting.netty;

import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.common.util.TrubboProtocolUtil;
import com.maxchen.trubbo.remoting.codec.protocol.TrubboHeader;
import com.maxchen.trubbo.remoting.codec.protocol.TrubboMessage;
import com.maxchen.trubbo.remoting.netty.api.Channel;
import io.netty.channel.ChannelFuture;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@NoArgsConstructor
public class NettyChannel implements Channel {
    public static final Map<io.netty.channel.Channel, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();

    private io.netty.channel.Channel channel;

    public NettyChannel(io.netty.channel.Channel channel) {
        this.channel = channel;
        CHANNEL_MAP.put(channel, this);
    }

    @Override
    public void send(Object message) {
        RpcContext context = RpcContext.getContext();
        TrubboHeader trubboHeader = new TrubboHeader();
        if (context != null) {
            trubboHeader.setMessageId(context.getRequestId());
            byte info = 0;
            if (context.isRequest()) {
                info = TrubboProtocolUtil.setRequest(info);
            }
            if (context.isHeartBeat()) {
                info = TrubboProtocolUtil.setHeartBeat(info);
            }
            if (context.isAsync()) {
                info = TrubboProtocolUtil.setAsync(info);
            }
            trubboHeader.setInfo(info);
        }
        TrubboMessage trubboMessage = new TrubboMessage(trubboHeader, message);
        channel.writeAndFlush(trubboMessage);
    }

    @Override
    public void close() {
        ChannelFuture disconnect = channel.disconnect();
        disconnect.syncUninterruptibly();
    }
}
