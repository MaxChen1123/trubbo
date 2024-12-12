package com.maxchen.trubbo.cluster.failhandler;

import com.maxchen.trubbo.cluster.api.FailHandlingInvoker;
import com.maxchen.trubbo.cluster.api.LoadBalance;
import com.maxchen.trubbo.rpc.protocol.api.Invocation;
import com.maxchen.trubbo.rpc.protocol.api.InvocationResult;
import com.maxchen.trubbo.rpc.protocol.consumer.ConsumerInvocationResult;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class FailSafeInvoker implements FailHandlingInvoker {
    @Override
    public InvocationResult invoke(List<String> providersAddr, Invocation invocation, LoadBalance loadBalance) {
        String providerAddr = loadBalance.select(providersAddr);
        InvocationResult result = null;
        try {
            result = doInvoke(providerAddr, invocation);
            return result;
        } catch (Exception e) {
            log.error("FailSafeInvoker fail", e);
            return ConsumerInvocationResult.nullResult();
        }
    }
}
