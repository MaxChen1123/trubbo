package com.maxchen.trubbo.rpc.protocol.provider;

import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.remoting.netty.exchange.Request;
import com.maxchen.trubbo.rpc.protocol.api.Invocation;

public class ProviderInvocation implements Invocation {
    private Request request;
    private boolean isOneWay;

    public ProviderInvocation(Request request) {
        this.request = request;
        this.isOneWay = RpcContext.getContext().isOneWay();
    }

    @Override
    public URL getUrl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getServiceName() {
        return request.getServiceName();
    }

    @Override
    public String getMethodName() {
        return request.getMethodName();
    }

    @Override
    public Class<?>[] getArgsTypes() {
        return request.getArgsTypes();
    }

    @Override
    public Object[] getArgs() {
        return request.getArgs();
    }

    @Override
    public boolean isAsync() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOneWay() {
        return isOneWay;
    }

    @Override
    public Request toRequest() {
        throw new UnsupportedOperationException();
    }

}
