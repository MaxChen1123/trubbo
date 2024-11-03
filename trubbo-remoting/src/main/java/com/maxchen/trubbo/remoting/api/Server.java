package com.maxchen.trubbo.remoting.api;

import com.maxchen.trubbo.common.URL;

public interface Server {
    void bind(URL url);
    void close();
}
