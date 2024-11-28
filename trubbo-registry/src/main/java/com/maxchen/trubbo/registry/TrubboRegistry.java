package com.maxchen.trubbo.registry;

import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.common.URL.UrlConstant;
import com.maxchen.trubbo.common.configuration.ConfigurationContext;
import com.maxchen.trubbo.registry.api.Registry;
import com.maxchen.trubbo.remoting.zookeeper.ZookeeperClient;
import com.maxchen.trubbo.remoting.zookeeper.ZookeeperListener;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class TrubboRegistry implements Registry {
    private ZookeeperClient zookeeperClient;
    private final URL url;

    // serviceName -> provider's remoteAddress list, it's for consumer's use
    public static final Map<String, Set<String>> PROVIDER_MAP = new ConcurrentHashMap<>();
    public static final Map<String, String> CONFIGURATION_MAP = new ConcurrentHashMap<>();

    public TrubboRegistry(URL url) {
        this.url = url;
        zookeeperClient = new ZookeeperClient(url);
        List<String> children = zookeeperClient.getChildren(RegistryConstants.SERVICE_PATH);
        if (children != null) {
            children.forEach(serviceName -> {
                List<String> providerAddr = zookeeperClient
                        .getChildren(RegistryConstants.SERVICE_PATH + "/" + serviceName
                                + RegistryConstants.PROVIDER_KEY);
                PROVIDER_MAP.putIfAbsent(serviceName, new CopyOnWriteArraySet<>(providerAddr));

                String config = zookeeperClient.getData(RegistryConstants.SERVICE_PATH + "/" + serviceName
                        + RegistryConstants.CONFIGURATION_KEY);
                if (config != null) ConfigurationContext.REGISTRY_CONFIGURATION_MAP.put(serviceName, config);

                List<String> methodConfig = zookeeperClient.getChildren(RegistryConstants.SERVICE_PATH + "/" + serviceName
                        + RegistryConstants.METHOD_KEY);
                methodConfig.forEach(methodName -> {
                    String data = zookeeperClient.getData(RegistryConstants.SERVICE_PATH + "/" +
                            serviceName + RegistryConstants.METHOD_KEY + "/" + methodName);
                    ConfigurationContext.REGISTRY_CONFIGURATION_MAP.put(serviceName + ":" + methodName, data);
                });

            });
        }

    }

    @Override
    public void register(URL url) {
        String serviceName = url.getParameter(UrlConstant.SERVICE_KEY);
        zookeeperClient.createPath(RegistryConstants.SERVICE_PATH + "/" + serviceName
                + RegistryConstants.PROVIDER_KEY + "/" + url.getRemoteAddress());
    }

    @Override
    public void unregister(URL url) {

    }

    @Override
    public void subscribe(String serviceName, ZookeeperListener listener) {
        zookeeperClient.watchPath(RegistryConstants.SERVICE_PATH + "/"
                + serviceName + RegistryConstants.PROVIDER_KEY, listener);
    }

    @Override
    public void unsubscribe(URL url) {

    }

    @Override
    public void watchServiceConfiguration(String serviceName, ZookeeperListener listener) {
        zookeeperClient.watchPath(RegistryConstants.SERVICE_PATH + "/" +
                serviceName + RegistryConstants.CONFIGURATION_KEY, listener);
    }

    @Override
    public void watchMethodConfiguration(String serviceName, ZookeeperListener listener) {
        zookeeperClient.watchPath(RegistryConstants.SERVICE_PATH + "/" +
                serviceName + RegistryConstants.METHOD_KEY, listener);
    }
}
