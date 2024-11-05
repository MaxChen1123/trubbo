package com.maxchen.trubbo.remoting.netty.api;

public interface Client {
    void connect();

    void disconnect();

    void send(Object message);
}
