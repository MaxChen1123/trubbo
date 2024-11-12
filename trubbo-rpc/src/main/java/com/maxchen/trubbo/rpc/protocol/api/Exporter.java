package com.maxchen.trubbo.rpc.protocol.api;

import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.remoting.netty.api.Server;

public interface Exporter {
    Invoker getInvoker();

    URL getUrl();

    Server getServer();

    InvocationResult invoke(Invocation invocation);

}
