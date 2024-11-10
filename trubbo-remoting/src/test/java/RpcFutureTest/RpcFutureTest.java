package RpcFutureTest;

import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.remoting.exchange.HeaderExchangeClient;
import com.maxchen.trubbo.remoting.exchange.HeaderExchangeServer;
import com.maxchen.trubbo.remoting.exchange.Request;
import com.maxchen.trubbo.remoting.exchange.Response;
import com.maxchen.trubbo.remoting.netty.api.ChannelHandler;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Future;

public class RpcFutureTest {

    @Test
    public void rpcTest_client() throws Exception {
        RpcContext context = RpcContext.getContext();
        context.setUrlFromString("Provider://127.0.0.1:8080");
        HeaderExchangeClient headerExchangeClient = new HeaderExchangeClient(new ClientTestHandler());
        headerExchangeClient.connect();

        context.setUrlFromString("Provider://127.0.0.1:8080?timeout=5000");
        context.setRequest(true);
        Request request = Request.builder()
                .requestId(1)
                .serviceName("HelloService")
                .argsTypes(new Class[]{String.class, User.class})
                .args(new Object[]{"hello", new User("maxchen", 18)})
                .build();

        Future<Response> future = headerExchangeClient.request(request);
        Response response = future.get();
        if (response.isException()) {
            System.out.println(response.getException());
        } else {
            System.out.println(response.getResult());
        }
        Thread.sleep(1000 * 60);
    }

    @Test
    public void rpcTest_server() throws Exception {
        RpcContext context = RpcContext.getContext();
        context.setUrlFromString("Provider://127.0.0.1:8080");
        HeaderExchangeServer headerExchangeServer = new HeaderExchangeServer(new ServerTestHandler());
        headerExchangeServer.bind();
        Thread.sleep(1000 * 60);
    }
}

class ClientTestHandler implements ChannelHandler {

    public void received(Channel channel, Object message) {
    }

    @Override
    public void connected(com.maxchen.trubbo.remoting.netty.api.Channel channel) {

    }

    @Override
    public void disconnected(com.maxchen.trubbo.remoting.netty.api.Channel channel) {

    }

    @Override
    public void received(com.maxchen.trubbo.remoting.netty.api.Channel channel, Object message) {

    }

    @Override
    public void caught(com.maxchen.trubbo.remoting.netty.api.Channel channel, Throwable exception) {

    }

    @Override
    public void sent(com.maxchen.trubbo.remoting.netty.api.Channel channel, Object message) {

    }
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
                .requestId(1)
                .result(new User("Tanaka", 81))
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

@AllArgsConstructor
@Data
class User {
    private static final long serialVersionUID = 1L;
    private String name;
    private int age;
}


