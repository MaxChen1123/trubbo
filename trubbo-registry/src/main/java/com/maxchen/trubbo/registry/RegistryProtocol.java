package com.maxchen.trubbo.registry;

import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.registry.api.Registry;
import com.maxchen.trubbo.remoting.zookeeper.ZookeeperListener;
import lombok.Getter;

public class RegistryProtocol {
    @Getter
    private final Registry registry;
    private URL url;

    public RegistryProtocol(URL url) {
        this.registry = new TrubboRegistry(url);
        this.url = url;
    }

    public void register(URL url) {
        registry.register(url);
    }

    public void subscribe(String serviceName) {
        registry.subscribe(serviceName, new ZookeeperListener("", new RegistryCallback()));
        registry.watchServiceConfiguration(serviceName, new ZookeeperListener("", new ServiceConfigurationCallback()));
        registry.watchMethodConfiguration(serviceName, new ZookeeperListener("", new MethodConfigurationCallback()));
    }
}
