package com.maxchen.trubbo.cluster.failhandler;

import com.maxchen.trubbo.cluster.api.FailHandlingInvoker;
import com.maxchen.trubbo.cluster.api.LoadBalance;
import com.maxchen.trubbo.cluster.exception.RpcException;
import com.maxchen.trubbo.common.configuration.ConfigConstants;
import com.maxchen.trubbo.common.configuration.ConfigurationContext;
import com.maxchen.trubbo.rpc.protocol.api.Invocation;
import com.maxchen.trubbo.rpc.protocol.api.InvocationResult;

import java.util.ArrayList;
import java.util.List;

public class FailOverInvoker implements FailHandlingInvoker {
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
            if (providersAddr.isEmpty()) {
                break;
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


}
