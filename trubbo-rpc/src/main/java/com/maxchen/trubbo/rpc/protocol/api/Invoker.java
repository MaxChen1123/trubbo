package com.maxchen.trubbo.rpc.protocol.api;

public interface Invoker {
    InvocationResult invoke(Invocation invocation);

    String getServiceName();
}
