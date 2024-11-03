package com.maxchen.trubbo.remoting.api;

import com.maxchen.trubbo.common.URL;

public interface Client {
    void connect(URL url);
    void disconnect();
    void send(Object message);
}
