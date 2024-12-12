package com.maxchen.trubbo.cluster.api;

import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.common.URL.UrlConstant;
import com.maxchen.trubbo.rpc.protocol.TrubboProtocol;
import com.maxchen.trubbo.rpc.protocol.api.Invocation;
import com.maxchen.trubbo.rpc.protocol.api.InvocationResult;
import com.maxchen.trubbo.rpc.protocol.api.Invoker;

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

    default InvocationResult doInvoke(String providerAddr, Invocation invocation) {
        RpcContext context = RpcContext.getContext();
        String serviceName = context.getServiceName();
        String invokerKey = getInvokerKey(serviceName, providerAddr);
        Invoker invoker = TrubboProtocol.getINVOKER_MAP().get(invokerKey);
        if (invoker == null) {
            URL serviceUrl = getServiceUrl(serviceName, providerAddr);
            invoker = TrubboProtocol.refer(serviceUrl);
        }
        return invoker.invoke(invocation);
    }
}
