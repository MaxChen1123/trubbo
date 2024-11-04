import com.maxchen.trubbo.remoting.NettyClient;
import com.maxchen.trubbo.remoting.NettyServer;
import com.maxchen.trubbo.remoting.api.Channel;
import com.maxchen.trubbo.remoting.api.ChannelHandler;
import com.maxchen.trubbo.remoting.codec.protocol.TrubboHeader;
import com.maxchen.trubbo.remoting.codec.protocol.TrubboMessage;
import com.maxchen.trubbo.remoting.codec.serialization.Serialization;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

public class NettyTest {
    @Test
    public void server_test() throws InterruptedException {
        NettyServer nettyServer = new NettyServer(8080, new TestHandler());
        nettyServer.bind();
        Thread.sleep(1000*60);
    }
    @Test
    public void client_test(){
        NettyClient nettyClient = new NettyClient("localhost", 8080, new TestHandler());
        nettyClient.connect();
        int age=18;
        while(true){
            nettyClient.send(new TrubboMessage(new TrubboHeader((byte) 0, 1, 0)
                    ,new User("maxchen", age++)));
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
        System.out.println("----------received :"+message);
    }

    @Override
    public void caught(Channel channel, Throwable exception) {

    }
}
@AllArgsConstructor
@Data
class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private int age;
}
