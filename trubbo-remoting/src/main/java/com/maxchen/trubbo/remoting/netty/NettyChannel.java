package com.maxchen.trubbo.remoting.netty;

import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.common.util.TrubboProtocolUtil;
import com.maxchen.trubbo.remoting.netty.api.Channel;
import com.maxchen.trubbo.remoting.netty.codec.protocol.TrubboHeader;
import com.maxchen.trubbo.remoting.netty.codec.protocol.TrubboMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Getter
@NoArgsConstructor
public class NettyChannel implements Channel {
    public static final Map<io.netty.channel.Channel, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();
    private io.netty.channel.Channel channel;
    @Setter
    private volatile long lastReadTime;
    @Setter
    private volatile long lastWriteTime;

    public static NettyChannel getChannel(io.netty.channel.Channel channel) {
        if (CHANNEL_MAP.containsKey(channel)) {
            return (NettyChannel) CHANNEL_MAP.get(channel);
        } else {
            return new NettyChannel(channel);
        }
    }

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
            if (context.isOneWay()) {
                info = TrubboProtocolUtil.setOneWay(info);
            }
            trubboHeader.setInfo(info);
        }
        TrubboMessage trubboMessage = new TrubboMessage(trubboHeader, message);
        log.debug("NettyChannel.send send {}", trubboMessage);
        channel.writeAndFlush(trubboMessage);
    }

}
