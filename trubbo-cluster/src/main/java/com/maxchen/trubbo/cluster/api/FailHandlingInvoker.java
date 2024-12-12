package com.maxchen.trubbo.cluster.api;

import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.common.URL.UrlConstant;
import com.maxchen.trubbo.rpc.protocol.api.Invocation;
import com.maxchen.trubbo.rpc.protocol.api.InvocationResult;

import java.net.URISyntaxException;
import java.util.List;

public interface FailHandlingInvoker {
    InvocationResult invoke(List<String> providersAddr, Invocation invocation, LoadBalance loadBalance);

    default String getInvokerKey(String serviceName, String address) {
        return serviceName + ":" + address;
    }

    default URL getServiceUrl(String serviceName, String address) {
        try {
            return new URL("Provider://" + address + "?" + UrlConstant.SERVICE_KEY + "=" + serviceName);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
