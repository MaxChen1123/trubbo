package com.maxchen.trubbo.rpc.protocol.provider;

import com.maxchen.trubbo.remoting.exchange.Response;
import com.maxchen.trubbo.rpc.protocol.api.InvocationResult;

public class ProviderInvocationResult implements InvocationResult {
    private Response response;

    public ProviderInvocationResult(Response response) {
        this.response = response;
    }

    @Override
    public Response get() {
        return response;
    }
}
