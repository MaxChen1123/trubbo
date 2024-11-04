package com.maxchen.trubbo.remoting;

import com.maxchen.trubbo.common.URL;
import com.maxchen.trubbo.remoting.api.Channel;
import com.maxchen.trubbo.remoting.api.ChannelHandler;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

public class NettyChannel implements Channel {
    public static Map<io.netty.channel.Channel,Channel> CHANNEL_MAP;
    @Override
    public URL getUrl() {
        return null;
    }

    @Override
    public ChannelHandler getChannelHandler() {
        return null;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void send(Object message) {

    }

    @Override
    public void close() {

    }
}
