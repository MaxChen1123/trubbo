package com.maxchen.trubbo.cluster;

import com.maxchen.trubbo.cluster.api.LoadBalance;
import com.maxchen.trubbo.cluster.exception.NoProviderException;
import com.maxchen.trubbo.cluster.failhandler.FailOverInvoker;
import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.common.URL.UrlConstant;
import com.maxchen.trubbo.common.configuration.ConfigConstants;
import com.maxchen.trubbo.common.configuration.ConfigurationContext;
import com.maxchen.trubbo.common.spi.ExtensionLoader;
import com.maxchen.trubbo.registry.TrubboRegistry;
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

    private static final String DEFAULT_LOAD_BALANCE = "com.maxchen.trubbo.cluster.loadbalance.RandomLoadBalance";
    public static final String DEFAULT_FAIL_HANDLING = "com.maxchen.trubbo.cluster.failhandler.FailoverInvoker";

    public ClusterInvoker(String serviceName, ClusterProtocol ClusterProtocol) {
        this.serviceName = serviceName;
        this.ClusterProtocol = ClusterProtocol;
    }

    // TODO retry
    @Override
    public InvocationResult invoke(Invocation invocation) {
        Set<String> providersAddr = TrubboRegistry.PROVIDER_MAP.get(serviceName);
        if (providersAddr == null || providersAddr.isEmpty()) {
            throw new NoProviderException("No provider available for service " + serviceName);
        }
        //set context
        RpcContext context = RpcContext.getContext();
        context.setOneWay(invocation.isOneWay());
        context.setAsync(invocation.isAsync());
        context.setServiceName(serviceName);
        context.setMethodName(invocation.getMethodName());
        //timeout
        String timeout = ConfigurationContext.getServiceConfigProperty(serviceName, ConfigConstants.TIMEOUT_KEY, "5000");
        context.getAttachments().put("timeout", timeout);
        //LoadBalance
        String loadBalanceName = ConfigurationContext.getProperty(ConfigConstants.LOADBALANCE_KEY, DEFAULT_LOAD_BALANCE);
        LoadBalance loadBalance = ExtensionLoader.getExtension(LoadBalance.class, loadBalanceName);
        assert loadBalance != null;
        String address = loadBalance.select(providersAddr.stream().toList());

//        String invokerKey = getInvokerKey(serviceName, address);
//        Invoker invoker = TrubboProtocol.getINVOKER_MAP().get(invokerKey);
//        if (invoker == null) {
//            URL serviceUrl = getServiceUrl(serviceName, address);
//            invoker = TrubboProtocol.refer(serviceUrl);
//        }
//        return invoker.invoke(invocation);

        // FailHandling
        String failHandling = ConfigurationContext.getProperty(ConfigConstants.FAIL_HANDLING_KEY, DEFAULT_FAIL_HANDLING);
        FailOverInvoker invoker = ExtensionLoader.getExtension(FailOverInvoker.class, failHandling);
        return invoker.invoke(providersAddr.stream().toList(), invocation, loadBalance);

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
