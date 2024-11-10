package com.maxchen.trubbo.rpc.protocol.consumer;

import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.common.URL.UrlConstant;
import com.maxchen.trubbo.remoting.exchange.Request;
import com.maxchen.trubbo.remoting.exchange.Response;
import com.maxchen.trubbo.remoting.exchange.api.ExchangeClient;
import com.maxchen.trubbo.rpc.protocol.api.Invocation;
import com.maxchen.trubbo.rpc.protocol.api.InvocationResult;
import com.maxchen.trubbo.rpc.protocol.api.Invoker;
import lombok.Data;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

@Data
public class TrubboConsumerInvoker implements Invoker {
    private String serviceName;
    private List<ExchangeClient> clients;
    private URL url;

    public TrubboConsumerInvoker(List<ExchangeClient> clients, URL url) {
        this.serviceName = url.getParameter(UrlConstant.SERVICE_KEY);
        this.clients = clients;
        this.url = url;
    }

    @Override
    public InvocationResult invoke(Invocation invocation) {
        RpcContext context = RpcContext.getContext();
        context.setUrl(invocation.getUrl());
        context.setOneWay(invocation.isOneWay());
        context.setAsync(invocation.isAsync());

        Request request = invocation.toRequest();
        context.setRequest(true);
        context.setRequestId(request.getRequestId());

        ExchangeClient client = choseClient(clients);
        if (context.isOneWay()) {
            client.send(request);
            return null;
        }
        Future<Response> resultFuture = client.request(request);
        return new ConsumerInvocationResult(resultFuture);
    }

    private ExchangeClient choseClient(List<ExchangeClient> clients) {
        int i = ThreadLocalRandom.current().nextInt(clients.size());
        return clients.get(i);
    }


}
