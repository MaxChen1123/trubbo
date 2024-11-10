package protocolTest;

import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.remoting.exchange.HeaderExchangeServer;
import com.maxchen.trubbo.remoting.exchange.Response;
import com.maxchen.trubbo.remoting.netty.api.ChannelHandler;
import com.maxchen.trubbo.rpc.protocol.TrubboProtocol;
import com.maxchen.trubbo.rpc.protocol.api.InvocationResult;
import com.maxchen.trubbo.rpc.protocol.api.Invoker;
import com.maxchen.trubbo.rpc.protocol.consumer.ConsumerInvocation;
import org.junit.jupiter.api.Test;

public class ProtocolConsumerTest {
    @Test
    public void consumerInvoker_test() throws Exception {
        Invoker refer = TrubboProtocol.refer(new URL("http://localhost:8080/testService"));
        ConsumerInvocation inv = ConsumerInvocation.builder()
                .isOneWay(true)
                .args(new Object[]{"test", 1})
                .argsTypes(new Class[]{String.class, int.class})
                .serviceName("testService")
                .methodName("testMethod")
                .build();
        InvocationResult invoke = refer.invoke(inv);
        Thread.sleep(60 * 1000);
    }

    @Test
    public void rpcTest_server() throws Exception {
        RpcContext context = RpcContext.getContext();
        context.setUrlFromString("Provider://127.0.0.1:8080");
        HeaderExchangeServer headerExchangeServer = new HeaderExchangeServer(new ServerTestHandler());
        headerExchangeServer.bind();
        Thread.sleep(1000 * 60);
    }

    class ServerTestHandler implements ChannelHandler {

        @Override
        public void connected(com.maxchen.trubbo.remoting.netty.api.Channel channel) {

        }

        @Override
        public void disconnected(com.maxchen.trubbo.remoting.netty.api.Channel channel) {

        }

        @Override
        public void received(com.maxchen.trubbo.remoting.netty.api.Channel channel, Object message) {
            Response response = Response.builder()
                    .build();
            RpcContext context = RpcContext.getContext();
            context.setRequest(false);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            channel.send(response);
        }

        @Override
        public void caught(com.maxchen.trubbo.remoting.netty.api.Channel channel, Throwable exception) {

        }

        @Override
        public void sent(com.maxchen.trubbo.remoting.netty.api.Channel channel, Object message) {

        }
    }
}
