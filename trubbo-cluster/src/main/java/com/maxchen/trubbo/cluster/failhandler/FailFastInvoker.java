package com.maxchen.trubbo.cluster.failhandler;

import com.maxchen.trubbo.cluster.api.FailHandlingInvoker;
import com.maxchen.trubbo.cluster.api.LoadBalance;
import com.maxchen.trubbo.cluster.exception.RpcException;
import com.maxchen.trubbo.rpc.protocol.api.Invocation;
import com.maxchen.trubbo.rpc.protocol.api.InvocationResult;

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


}
