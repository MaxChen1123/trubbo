package protocolTest;

import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.remoting.exchange.HeaderExchangeServer;
import com.maxchen.trubbo.remoting.exchange.Response;
import com.maxchen.trubbo.remoting.netty.api.Channel;
import com.maxchen.trubbo.remoting.netty.api.ChannelHandler;
import com.maxchen.trubbo.rpc.protocol.TrubboProtocol;
import com.maxchen.trubbo.rpc.protocol.api.Invocation;
import com.maxchen.trubbo.rpc.protocol.api.InvocationResult;
import com.maxchen.trubbo.rpc.protocol.api.Invoker;
import com.maxchen.trubbo.rpc.protocol.consumer.ConsumerInvocation;
import com.maxchen.trubbo.rpc.protocol.provider.ProviderInvocation;
import com.maxchen.trubbo.rpc.protocol.provider.ProviderInvocationResult;
import com.maxchen.trubbo.rpc.proxy.JdkProxyFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import ttt.TestService;
import ttt.TestServiceImpl;
import ttt.User;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ProtocolConsumerTest {
    @Test
    public void consumerInvoker_test() throws Exception {
        Invoker refer = TrubboProtocol.refer(new URL("http://localhost:8080?service=ttt.TestService"));
        ConsumerInvocation inv = ConsumerInvocation.builder()
                .isOneWay(false)
                .args(new Object[]{"test", 1})
                .argsTypes(new Class[]{String.class, int.class})
                .serviceName("ttt.TestService")
                .methodName("testMethod")
                .url(new URL("http://localhost:8080?service=ttt.TestService"))
                .build();
        ArrayList<InvocationResult> invocationResults = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            inv.setArgs(new Object[]{"test" + i, i});
            invocationResults.add(refer.invoke(inv));
        }
        for (InvocationResult invocationResult : invocationResults) {
            log.info("result: {}", invocationResult.get());
        }
        Thread.sleep(60 * 1000);
    }

    @Test
    public void Proxy_test() throws Exception {
        Invoker refer = TrubboProtocol.refer(new URL("http://localhost:8080?service=ttt.TestService"));
        TestService proxy = JdkProxyFactory.getProxy(TestService.class, refer);
        String m1 = proxy.testMethod("This message is from testMethod", 1);
        log.info("testMethod result: {}", m1);
        CompletableFuture<String> m2 = proxy.testMethodAsync("This message is from testMethodAsync", 2);
        m2.thenAccept(result -> log.info("testMethodAsync result: {}", result));
        User user = proxy.testUser(1);
        log.info("testUser result: {}", user);
        CompletableFuture<User> user2 = proxy.testUserAsync(2);
        user2.thenAccept(result -> log.info("testUserAsync result: {}", result));
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

    @Test
    public void rpcTest_exporter() throws Exception {
        TrubboProtocol.export(new URL("Provider://127.0.0.1:8080?service=ttt.TestService"), new TestInvoker());
        Thread.sleep(1000 * 60);
    }

    class ServerTestHandler implements ChannelHandler {

        @Override
        public void connected(Channel channel) {

        }

        @Override
        public void disconnected(Channel channel) {

        }

        @Override
        public void received(Channel channel, Object message) {
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
        public void caught(Channel channel, Throwable exception) {

        }

        @Override
        public void sent(Channel channel, Object message) {

        }
    }

    static class TestInvoker implements Invoker {
        private static final TestServiceImpl impl = new TestServiceImpl();

        @Override
        public InvocationResult invoke(Invocation invocation) {
            if (invocation instanceof ProviderInvocation providerInvocation) {
                String serviceName = providerInvocation.getServiceName();
                try {
                    Class<?> aClass = Class.forName(serviceName + "Impl");
                    Method method = aClass.getMethod(providerInvocation.getMethodName(), providerInvocation.getArgsTypes());
                    Object invoke = method.invoke(impl, providerInvocation.getArgs());
                    if (invoke instanceof CompletableFuture<?> c) {
                        invoke = c.get();
                    }

                    Response build = Response.builder()
                            .result(invoke)
                            .returnType(invoke == null ? null : invoke.getClass())
                            .requestId(RpcContext.getContext().getRequestId())
                            .build();
                    log.info("provider response: {}", build);
                    return new ProviderInvocationResult(build);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            log.warn("not support invocation type");
            return null;
        }

        @Override
        public String getServiceName() {
            return "";
        }
    }
}
