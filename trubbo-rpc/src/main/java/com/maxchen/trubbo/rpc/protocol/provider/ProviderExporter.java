package com.maxchen.trubbo.rpc.protocol.provider;

import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.remoting.netty.api.Server;
import com.maxchen.trubbo.rpc.protocol.api.Exporter;
import com.maxchen.trubbo.rpc.protocol.api.Invocation;
import com.maxchen.trubbo.rpc.protocol.api.InvocationResult;
import com.maxchen.trubbo.rpc.protocol.api.Invoker;

public class ProviderExporter implements Exporter {
    private Invoker invoker;
    private Server server;
    private URL url;

    public ProviderExporter(Invoker invoker, Server server, URL url) {
        this.invoker = invoker;
        this.server = server;
        this.url = url;
    }

    @Override
    public Invoker getInvoker() {
        return invoker;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public InvocationResult invoke(Invocation invocation) {
        return invoker.invoke(invocation);
    }
}
