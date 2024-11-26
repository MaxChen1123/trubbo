package com.maxchen.trubbo.cluster;

import com.maxchen.trubbo.cluster.api.Configuration;
import com.maxchen.trubbo.cluster.api.LoadBalance;
import com.maxchen.trubbo.cluster.exception.NoProviderException;
import com.maxchen.trubbo.cluster.loadbalance.RandomLoadBalance;
import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.common.URL.UrlConstant;
import com.maxchen.trubbo.registry.TrubboRegistry;
import com.maxchen.trubbo.rpc.protocol.TrubboProtocol;
import com.maxchen.trubbo.rpc.protocol.api.Invocation;
import com.maxchen.trubbo.rpc.protocol.api.InvocationResult;
import com.maxchen.trubbo.rpc.protocol.api.Invoker;
import lombok.Getter;

import java.net.URISyntaxException;
import java.util.Set;

public class ClusterInvoker implements Invoker {
    @Getter
    private String serviceName;
    private final ClusterProtocol ClusterProtocol;

    private final Configuration configuration;
    // TODO config
    private static final LoadBalance loadBalance = new RandomLoadBalance();

    public ClusterInvoker(String serviceName, ClusterProtocol ClusterProtocol) {
        this.serviceName = serviceName;
        this.ClusterProtocol = ClusterProtocol;
        this.configuration = new ServiceConfiguration(serviceName);
    }

    // TODO retry
    @Override
    public InvocationResult invoke(Invocation invocation) {
        Set<String> providersAddr = TrubboRegistry.PROVIDER_MAP.get(serviceName);
        if (providersAddr == null || providersAddr.isEmpty()) {
            throw new NoProviderException("No provider available for service " + serviceName);
        }
        // TODO config
        String address = loadBalance.select(providersAddr.stream().toList());
        String invokerKey = getInvokerKey(serviceName, address);
        Invoker invoker = TrubboProtocol.getINVOKER_MAP().get(invokerKey);
        if (invoker == null) {
            URL serviceUrl = getServiceUrl(serviceName, address);
            invoker = TrubboProtocol.refer(serviceUrl);
        }

        RpcContext context = RpcContext.getContext();
        String timeout = configuration.getProperty(ConfigConstants.TIMEOUT_KEY, "5000");
        context.getAttachments().put("timeout", timeout);
        return invoker.invoke(invocation);
    }

    private static String getInvokerKey(String serviceName, String address) {
        return serviceName + ":" + address;
    }

    private static URL getServiceUrl(String serviceName, String address) {
        try {
            return new URL("Provider://" + address + "?" + UrlConstant.SERVICE_KEY + "=" + serviceName);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
