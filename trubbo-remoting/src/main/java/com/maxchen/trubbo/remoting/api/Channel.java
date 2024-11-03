package com.maxchen.trubbo.remoting.api;

import com.maxchen.trubbo.common.URL;

public interface Channel {
    URL getUrl();

    ChannelHandler getChannelHandler();

    boolean isConnected();

    void send(Object message);

    void close();
}
