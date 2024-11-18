package com.maxchen.trubbo.remoting.zookeeper.api;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

public interface ListenerCallback {
    void callback(CuratorCacheListener.Type type, ChildData data, ChildData oldData);
}
