package com.maxchen.trubbo.cluster.failhandler;

import com.maxchen.trubbo.cluster.api.FailHandlingInvoker;
import com.maxchen.trubbo.cluster.api.LoadBalance;
import com.maxchen.trubbo.cluster.exception.RpcException;
import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.rpc.protocol.TrubboProtocol;
import com.maxchen.trubbo.rpc.protocol.api.Invocation;
import com.maxchen.trubbo.rpc.protocol.api.InvocationResult;
import com.maxchen.trubbo.rpc.protocol.api.Invoker;

import java.util.List;

public class FailFastInvoker implements FailHandlingInvoker {
    @Override
    public InvocationResult invoke(List<String> providersAddr, Invocation invocation, LoadBalance loadBalance) {
        String providerAddr = loadBalance.select(providersAddr);
        InvocationResult result = null;
        try {
            result = doInvoke(providerAddr, invocation);
            return result;
        } catch (Exception e) {
            throw new RpcException(e.getMessage());
        }
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
}
