package com.maxchen.trubbo.registry;

import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.common.URL.UrlConstant;
import com.maxchen.trubbo.registry.api.Registry;
import com.maxchen.trubbo.remoting.zookeeper.ZookeeperListener;
import com.maxchen.trubbo.rpc.protocol.TrubboProtocol;
import com.maxchen.trubbo.rpc.protocol.provider.ProviderInvoker;

public class RegistryProtocol {
    private final Registry registry;
    private URL url;

    public RegistryProtocol(URL url) {
        this.registry = new TrubboRegistry(url);
        this.url = url;
    }

    // TODO TrubboProtocol operation should be in Cluster layer
    public void register(URL url) {
        TrubboProtocol.export(url, new ProviderInvoker(url.getParameter(UrlConstant.SERVICE_KEY)));
        registry.register(url);
    }

    public void subscribe(String serviceName) {
        registry.subscribe(serviceName, new ZookeeperListener("", new RegistryCallback()));
    }
}
