package com.maxchen.trubbo.rpc.protocol;

import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.common.URL.UrlConstant;
import com.maxchen.trubbo.remoting.exchange.HeaderExchangeClient;
import com.maxchen.trubbo.remoting.exchange.HeaderExchangeServer;
import com.maxchen.trubbo.remoting.exchange.Request;
import com.maxchen.trubbo.remoting.exchange.Response;
import com.maxchen.trubbo.remoting.exchange.api.ExchangeClient;
import com.maxchen.trubbo.remoting.exchange.api.ExchangeServer;
import com.maxchen.trubbo.remoting.netty.api.Channel;
import com.maxchen.trubbo.remoting.netty.api.ChannelHandler;
import com.maxchen.trubbo.rpc.protocol.api.Exporter;
import com.maxchen.trubbo.rpc.protocol.api.InvocationResult;
import com.maxchen.trubbo.rpc.protocol.api.Invoker;
import com.maxchen.trubbo.rpc.protocol.consumer.TrubboConsumerInvoker;
import com.maxchen.trubbo.rpc.protocol.provider.ProviderExporter;
import com.maxchen.trubbo.rpc.protocol.provider.ProviderInvocation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class TrubboProtocol {
    private static final Map<String, List<ExchangeClient>> CLIENT_MAP = new ConcurrentHashMap<>();
    private static final Map<String, Invoker> INVOKER_MAP = new ConcurrentHashMap<>();

    private static final Map<String, Exporter> EXPORTER_MAP = new ConcurrentHashMap<>();
    private static final Map<Integer, ExchangeServer> SERVER_MAP = new ConcurrentHashMap<>(); //key:port

    /**
     * url should have serviceName
     */
    public static Invoker refer(URL url) {
        RpcContext context = RpcContext.getContext();
        context.setUrl(url);
        if (INVOKER_MAP.containsKey(url.getInvokerKey())) {
            return INVOKER_MAP.get(url.getInvokerKey());
        } else {
            List<ExchangeClient> clients = getClients(url);
            TrubboConsumerInvoker trubboConsumerInvoker = new TrubboConsumerInvoker(clients, url);
            INVOKER_MAP.put(url.getInvokerKey(), trubboConsumerInvoker);
            return trubboConsumerInvoker;
        }
    }

    public static Exporter export(URL url, Invoker invoker) {
        RpcContext context = RpcContext.getContext();
        context.setUrl(url);
        openServer();
        ProviderExporter providerExporter = new ProviderExporter(invoker, SERVER_MAP.get(url.getPort()), url);
        String serviceName = url.getParameter(UrlConstant.SERVICE_KEY);
        EXPORTER_MAP.put(serviceName, providerExporter);
        return providerExporter;
    }

    private static List<ExchangeClient> getClients(URL url) {
        List<ExchangeClient> exchangeClients = CLIENT_MAP.get(url.getProviderAddress());
        if (exchangeClients == null) {
            exchangeClients = new CopyOnWriteArrayList<>();
            exchangeClients.add(getExchangeClient(url));
            CLIENT_MAP.put(url.getProviderAddress(), exchangeClients);
        } else {
            // TODO configuration
            if (exchangeClients.size() < 3) {
                exchangeClients.add(getExchangeClient(url));
            }
        }
        return exchangeClients;
    }

    private static void openServer() {
        int port = RpcContext.getContext().getUrl().getPort();
        if (!SERVER_MAP.containsKey(port)) {
            ExchangeServer exchangeServer = new HeaderExchangeServer(new TrubboProviderHandler());
            exchangeServer.bind();
            SERVER_MAP.put(port, exchangeServer);
        }
    }

    private static ExchangeClient getExchangeClient(URL url) {
        return new HeaderExchangeClient(new TrubboConsumerHandler());
    }

    static class TrubboConsumerHandler implements ChannelHandler {
        @Override
        public void connected(Channel channel) {

        }

        @Override
        public void disconnected(Channel channel) {

        }

        @Override
        public void received(Channel channel, Object message) {

        }

        @Override
        public void caught(Channel channel, Throwable exception) {

        }

        @Override
        public void sent(Channel channel, Object message) {

        }
    }

    static class TrubboProviderHandler implements ChannelHandler {
        @Override
        public void connected(Channel channel) {

        }

        @Override
        public void disconnected(Channel channel) {

        }

        @Override
        public void received(Channel channel, Object message) {
            if (message instanceof Request request) {
                Exporter exporter = EXPORTER_MAP.get(request.getServiceName());
                ProviderInvocation providerInvocation = new ProviderInvocation(request);
                InvocationResult result = exporter.invoke(providerInvocation);
                response(result.get(), channel);
            }
        }

        @Override
        public void caught(Channel channel, Throwable exception) {

        }

        @Override
        public void sent(Channel channel, Object message) {

        }

        public void response(Response response, Channel channel) {
            RpcContext context = RpcContext.getContext();
            context.setRequest(false);
            channel.send(response);
        }
    }

}
