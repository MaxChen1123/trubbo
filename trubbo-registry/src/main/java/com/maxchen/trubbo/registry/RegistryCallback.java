package com.maxchen.trubbo.registry;

import com.maxchen.trubbo.common.util.ZookeeperPathParser;
import com.maxchen.trubbo.remoting.zookeeper.api.ListenerCallback;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

import java.util.Set;

@Slf4j
public class RegistryCallback implements ListenerCallback {
    @Override
    public void callback(CuratorCacheListener.Type type, ChildData data, ChildData oldData) {
        if (type == CuratorCacheListener.Type.NODE_DELETED) {
            String path = oldData.getPath();
            String serviceName;
            String address;
            try {
                serviceName = ZookeeperPathParser.getServiceName(path);
                address = ZookeeperPathParser.getAddress(path);
            } catch (IllegalArgumentException e) {
                return;
            }
            Set<String> strings = TrubboRegistry.PROVIDER_MAP.get(serviceName);
            if (strings != null) {
                strings.remove(address);
            }
        } else if (type == CuratorCacheListener.Type.NODE_CREATED) {
            String path = data.getPath();
            String serviceName;
            String address;
            try {
                serviceName = ZookeeperPathParser.getServiceName(path);
                address = ZookeeperPathParser.getAddress(path);
            } catch (IllegalArgumentException e) {
                return;
            }
            Set<String> strings = TrubboRegistry.PROVIDER_MAP.get(serviceName);
            if (strings != null) {
                strings.add(address);
            }
        }
    }

}
