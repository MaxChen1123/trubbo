package com.maxchen.trubbo.remoting.zookeeper;

import com.maxchen.trubbo.remoting.zookeeper.api.ListenerCallback;
import lombok.Getter;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

public class ZookeeperListener implements CuratorCacheListener {
    @Getter
    private final String path;

    private final ListenerCallback callback;

    public ZookeeperListener(String path, ListenerCallback callback) {
        this.path = path;
        this.callback = callback;
    }

    @Override
    public void event(Type type, ChildData oldData, ChildData data) {
        callback.callback(type, data, oldData);
    }
}
