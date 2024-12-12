package com.maxchen.trubbo.cluster.api;

import com.maxchen.trubbo.rpc.protocol.api.Invocation;
import com.maxchen.trubbo.rpc.protocol.api.InvocationResult;

import java.util.List;

public interface FailHandlingInvoker {
    InvocationResult invoke(List<String> providersAddr, Invocation invocation, LoadBalance loadBalance);
}
