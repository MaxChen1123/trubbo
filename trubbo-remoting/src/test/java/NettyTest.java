import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.remoting.exchange.Request;
import com.maxchen.trubbo.remoting.netty.NettyClient;
import com.maxchen.trubbo.remoting.netty.NettyServer;
import com.maxchen.trubbo.remoting.netty.api.Channel;
import com.maxchen.trubbo.remoting.netty.api.ChannelHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;

public class NettyTest {
    @Test
    public void server_test() throws InterruptedException {
        NettyServer nettyServer = new NettyServer(8080, new TestHandler());
        nettyServer.bind();
        Thread.sleep(1000 * 60);
    }

    @Test
    public void client_test() throws NoSuchMethodException {
        NettyClient nettyClient = new NettyClient("localhost", 8080, new TestHandler());
        nettyClient.connect();
        int age = 18;
        while (true) {
            RpcContext context = RpcContext.getContext();
            context.setRequest(true);
            context.setRequestId(1);
            nettyClient.send(
                    Request.builder()
                            .requestId(1)
                            .args(new Object[]{new User("maxchen", age++)})
                            .build());
            System.out.println("send");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}

class TestHandler implements ChannelHandler {
    @Override
    public void connected(Channel channel) {

    }

    @Override
    public void disconnected(Channel channel) {

    }

    @Override
    public void received(Channel channel, Object message) {
        System.out.println("----------received :" + message.toString());
    }

    @Override
    public void caught(Channel channel, Throwable exception) {

    }

    @Override
    public void sent(Channel channel, Object message) {

    }
}

@AllArgsConstructor
@Data
class User {
    private static final long serialVersionUID = 1L;
    private String name;
    private int age;
}
