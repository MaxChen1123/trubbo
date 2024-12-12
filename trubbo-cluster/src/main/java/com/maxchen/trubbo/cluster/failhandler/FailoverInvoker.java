package com.maxchen.trubbo.cluster.failhandler;

import com.maxchen.trubbo.cluster.api.FailHandlingInvoker;
import com.maxchen.trubbo.cluster.api.LoadBalance;
import com.maxchen.trubbo.cluster.exception.RpcException;
import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.common.URL.UrlConstant;
import com.maxchen.trubbo.common.configuration.ConfigConstants;
import com.maxchen.trubbo.common.configuration.ConfigurationContext;
import com.maxchen.trubbo.rpc.protocol.TrubboProtocol;
import com.maxchen.trubbo.rpc.protocol.api.Invocation;
import com.maxchen.trubbo.rpc.protocol.api.InvocationResult;
import com.maxchen.trubbo.rpc.protocol.api.Invoker;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class FailoverInvoker implements FailHandlingInvoker {
    @Override
    public InvocationResult invoke(List<String> providersAddr, Invocation invocation, LoadBalance loadBalance) {
        String retry = ConfigurationContext.getProperty(ConfigConstants.RETRY_KEY, "3");
        int retryNum = Integer.parseInt(retry);
        RpcException exception = null;

        ArrayList<String> invoked = new ArrayList<>(providersAddr.size());
        for (int i = 0; i < retryNum; i++) {
            if (i != 0) {
                providersAddr.removeAll(invoked);
            }
            String providerAddr = loadBalance.select(providersAddr);
            InvocationResult result = null;
            try {
                result = doInvoke(providerAddr, invocation);
                return result;
            } catch (Exception e) {
                exception = new RpcException(e.getMessage());
            } finally {
                invoked.add(providerAddr);
            }
        }
        assert exception != null;
        throw exception;
    }

    private InvocationResult doInvoke(String providerAddr, Invocation invocation) {
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
