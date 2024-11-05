package com.maxchen.trubbo.remoting.netty.api;

public interface Channel {

    void send(Object message);

    void close();
}
