package com.maxchen.trubbo.rpc.protocol;

import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.remoting.exchange.HeaderExchangeClient;
import com.maxchen.trubbo.remoting.exchange.api.ExchangeClient;
import com.maxchen.trubbo.remoting.netty.api.Channel;
import com.maxchen.trubbo.remoting.netty.api.ChannelHandler;
import com.maxchen.trubbo.rpc.protocol.api.Invoker;
import com.maxchen.trubbo.rpc.protocol.consumer.TrubboConsumerInvoker;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class TrubboProtocol {
    private static final Map<String, List<ExchangeClient>> CLIENT_MAP = new ConcurrentHashMap<>();
    private static final Map<String, Invoker> INVOKER_MAP = new ConcurrentHashMap<>();

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

}
