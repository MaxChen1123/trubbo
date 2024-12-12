package com.maxchen.trubbo.cluster;

import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.common.URL.UrlConstant;
import com.maxchen.trubbo.registry.RegistryProtocol;
import com.maxchen.trubbo.rpc.protocol.TrubboProtocol;
import com.maxchen.trubbo.rpc.protocol.api.Invoker;
import com.maxchen.trubbo.rpc.protocol.provider.ProviderInvoker;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClusterProtocol {
    private final RegistryProtocol registryProtocol;
    public static final Map<String, Invoker> INVOKER_MAP = new ConcurrentHashMap<>();
    @Getter
    private static UUID clusterProtocolId = UUID.randomUUID();
    private final URL url;

    public ClusterProtocol(URL url) {
        this.registryProtocol = new RegistryProtocol(url);
        this.url = url;
    }

    public Invoker refer(String serviceName) {
        registryProtocol.subscribe(serviceName);
        return new ClusterInvoker(serviceName, this);
    }

    public void export(URL url) {
        String serviceName = url.getParameter(UrlConstant.SERVICE_KEY);
        String impl = url.getParameter("impl") == null ? serviceName + "Impl" : url.getParameter("impl");
        TrubboProtocol.export(url, new ProviderInvoker(serviceName, impl));
        registryProtocol.register(url);
    }

}
