package com.maxchen.trubbo.registry.api;

import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.remoting.zookeeper.ZookeeperListener;

public interface Registry {
    void register(URL url);

    void unregister(URL url);

    void subscribe(String serviceName, ZookeeperListener listener);

    void unsubscribe(URL url);
}
