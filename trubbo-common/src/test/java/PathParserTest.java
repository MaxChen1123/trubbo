import com.maxchen.trubbo.common.util.ZookeeperPathParser;
import org.junit.jupiter.api.Test;

public class PathParserTest {
    @Test
    public void test() {
        String serviceName = ZookeeperPathParser
                .getServiceName("/trubbo/service/com.maxchen.trubbo.service.HelloService/provider/127.0.0.1:8080");
        String address = ZookeeperPathParser
                .getAddress("/trubbo/service/com.maxchen.trubbo.service.HelloService/provider/127.0.0.1:8080");
        System.out.println(serviceName);
        System.out.println(address);

    }
}
