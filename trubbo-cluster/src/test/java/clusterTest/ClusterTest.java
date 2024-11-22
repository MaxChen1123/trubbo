package clusterTest;

import clusterTest.ttt.TestService;
import com.maxchen.trubbo.cluster.ClusterProtocol;
import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.rpc.protocol.api.Invoker;
import com.maxchen.trubbo.rpc.proxy.JdkProxyFactory;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

public class ClusterTest {
    @Test
    public void export_test() throws URISyntaxException, InterruptedException {
        ClusterProtocol clusterProtocol = new ClusterProtocol(new URL("zookeeper://127.0.0.1:2181"));
        clusterProtocol.export(new URL("Provider://127.0.0.1:8080?service=clusterTest.ttt.TestService"));
        clusterProtocol.export(new URL("Provider://127.0.0.1:8081?service=clusterTest.ttt.TestService"));
        clusterProtocol.export(new URL("Provider://127.0.0.1:8082?service=clusterTest.ttt.TestService"));
        clusterProtocol.export(new URL("Provider://127.0.0.1:8084?service=clusterTest.ttt.TestService"));
        Thread.sleep(60 * 1000);
    }

    @Test
    public void refer_test() throws URISyntaxException {
        ClusterProtocol clusterProtocol = new ClusterProtocol(new URL("zookeeper://127.0.0.1:2181"));
        Invoker refer = clusterProtocol.refer("clusterTest.ttt.TestService");
        TestService proxy = JdkProxyFactory.getProxy(TestService.class, refer);
        System.out.println(proxy.testMethod("hello", 1));
        proxy.testMethodAsync("helloAsync", 2).thenAccept(System.out::println);
        System.out.println(proxy.testUser(1));
        proxy.testUserAsync(2).thenAccept(System.out::println);
    }
}
