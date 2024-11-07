import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.remoting.codec.protocol.TrubboMessage;
import com.maxchen.trubbo.remoting.exchange.HeaderExchangeClient;
import com.maxchen.trubbo.remoting.exchange.HeaderExchangeServer;
import com.maxchen.trubbo.remoting.exchange.Request;
import com.maxchen.trubbo.remoting.netty.NettyServer;
import com.maxchen.trubbo.remoting.netty.api.Channel;
import com.maxchen.trubbo.remoting.netty.api.ChannelHandler;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.Arrays;


public class HeaderExchangeTest {
    @Test
    public void request_test() throws Exception {
        RpcContext context = RpcContext.getContext();
        context.setUrlFromString("Provider://127.0.0.1:8080");
        HeaderExchangeClient headerExchangeClient = new HeaderExchangeClient(new HeaderExchangeTestHandler());
        headerExchangeClient.connect();

        Request request = new Request();
        request.setRequestId(1);
        request.setServiceName("HelloService");
        request.setArgsTypes(new Class[]{String.class, User.class});
        request.setArgs(new Object[]{"hello", new User("maxchen", 18)});
        headerExchangeClient.request(request);
        Thread.sleep(1000 * 60);
    }

    @Test
    public void server() throws InterruptedException {
        NettyServer nettyServer = new NettyServer(8080, new HeaderExchangeTestHandler());
        nettyServer.bind();
        Thread.sleep(1000 * 60);
    }

    @Test
    public void exchangeServer_test() throws URISyntaxException, InterruptedException {
        RpcContext context = RpcContext.getContext();
        context.setUrlFromString("Provider://127.0.0.1:8080");
        HeaderExchangeServer headerExchangeServer = new HeaderExchangeServer(new HeaderExchangeTestHandler());
        headerExchangeServer.bind();
        Thread.sleep(1000 * 60);
    }


}

class HeaderExchangeTestHandler implements ChannelHandler {

    @Override
    public void connected(Channel channel) {

    }

    @Override
    public void disconnected(Channel channel) {

    }

    @Override
    public void received(Channel channel, Object message) {
        System.out.println("-----------------receive");
        if (message instanceof TrubboMessage mes) {
            Request request = (Request) mes.getBody();
            System.out.println(request.getServiceName());
            System.out.println(request.getRequestId());
            System.out.println(Arrays.toString(request.getArgsTypes()));
            System.out.println(Arrays.toString(request.getArgs()));
        }
    }

    @Override
    public void caught(Channel channel, Throwable exception) {

    }

    @Override
    public void sent(Channel channel, Object message) {

    }
}
