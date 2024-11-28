package com.maxchen.trubbo.remoting.zookeeper;

import com.maxchen.trubbo.remoting.zookeeper.api.ListenerCallback;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

@Slf4j
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
        log.debug("path: {}, type: {}, data: {}", path, type, new String(data.getData()));
        callback.callback(type, data, oldData);
    }
}
