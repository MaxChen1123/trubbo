package com.maxchen.trubbo.registry;

import com.maxchen.trubbo.common.util.ZookeeperPathParser;
import com.maxchen.trubbo.remoting.zookeeper.api.ListenerCallback;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

public class ConfigurationCallback implements ListenerCallback {
    @Override
    public void callback(CuratorCacheListener.Type type, ChildData data, ChildData oldData) {
        if (type == CuratorCacheListener.Type.NODE_CHANGED || type == CuratorCacheListener.Type.NODE_CREATED) {
            String path = data.getPath();
            String serviceName = ZookeeperPathParser.getServiceName(path);
            TrubboRegistry.CONFIGURATION_MAP.put(serviceName, new String(data.getData()));
        } else if (type == CuratorCacheListener.Type.NODE_DELETED) {
            String path = oldData.getPath();
            String serviceName = ZookeeperPathParser.getServiceName(path);
            TrubboRegistry.CONFIGURATION_MAP.remove(serviceName);
        }
    }
}
