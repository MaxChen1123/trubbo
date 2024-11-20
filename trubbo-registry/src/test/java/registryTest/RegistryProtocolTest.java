package registryTest;

import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.registry.RegistryProtocol;
import com.maxchen.trubbo.registry.TrubboRegistry;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

public class RegistryProtocolTest {
    @Test
    public void register_test() throws URISyntaxException, InterruptedException {
        RegistryProtocol registryProtocol = new RegistryProtocol(new URL("zookeeper://127.0.0.1:2181"));
        registryProtocol.register(new URL("zookeeper://127.0.0.1:8080?service=registryTest.ttt.TestService"));
        Thread.sleep(10 * 1000);
        registryProtocol.register(new URL("zookeeper://127.0.0.1:8081?service=registryTest.ttt.TestService"));
    }

    @Test
    public void subscribe_test() throws URISyntaxException, InterruptedException {
        RegistryProtocol registryProtocol = new RegistryProtocol(new URL("zookeeper://127.0.0.1:2181"));
        TrubboRegistry.PROVIDER_MAP.forEach((key, value) -> {
            System.out.println("----------------");
            System.out.println("serviceName:" + key);
            value.forEach(System.out::println);
        });
        registryProtocol.subscribe("registryTest.ttt.TestService");
        Thread.sleep(10 * 1000);
        TrubboRegistry.PROVIDER_MAP.forEach((key, value) -> {
            System.out.println("----------------");
            System.out.println("serviceName:" + key);
            value.forEach(System.out::println);
        });
    }
}
