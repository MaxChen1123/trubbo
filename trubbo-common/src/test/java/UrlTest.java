import com.maxchen.trubbo.common.URL.URL;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

public class UrlTest {
    @Test
    void url_path_test() throws URISyntaxException {
        String urlString = "trubbo://127.0.0.1:8080/com.maxchen.trubbo.service.HelloService/hello?version=1.0.0";
        URL url = new URL(urlString);
        System.out.println(url.getPath());
    }
}
