package com.maxchen.trubbo.rpc.protocol.api;

import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.remoting.netty.exchange.Request;

public interface Invocation {
    URL getUrl();

    String getServiceName();

    String getMethodName();

    Class<?>[] getArgsTypes();

    Object[] getArgs();

    boolean isAsync();

    boolean isOneWay();

    Request toRequest();
}
