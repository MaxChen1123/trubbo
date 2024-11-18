package com.maxchen.trubbo.remoting.zookeeper;

import com.maxchen.trubbo.remoting.zookeeper.api.ListenerCallback;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

public abstract class AbstractCallback implements ListenerCallback {
    private final ListenerCallback next;

    public AbstractCallback(ListenerCallback next) {
        this.next = next;
    }

    @Override
    public void callback(CuratorCacheListener.Type type, ChildData data, ChildData oldData) {
        next.callback(type, data, oldData);
    }
}
