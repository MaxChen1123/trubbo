package ZookeeperTest;

import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.remoting.zookeeper.ZookeeperClient;
import com.maxchen.trubbo.remoting.zookeeper.ZookeeperListener;
import com.maxchen.trubbo.remoting.zookeeper.api.ListenerCallback;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.junit.jupiter.api.Test;

public class ZookeeperTest {

    @Test
    public void client_test() throws Throwable {
        ZookeeperClient zookeeperClient = new ZookeeperClient(new URL("zookeeper://127.0.0.1:2181"));
        zookeeperClient.createPath("/trubbo/test/127.0.0.1");
    }

    @Test
    public void listener_test() throws Throwable {
        ZookeeperClient zookeeperClient = new ZookeeperClient(new URL("zookeeper://127.0.0.1:2181"));
        zookeeperClient.watchPath("/test/trubbo", new ZookeeperListener("/test/trubbo", new ListenerCallbackImpl()));
        Thread.sleep(1000);
        zookeeperClient.createPath("/test/trubbo/test", "test");
        Thread.sleep(1000);
        zookeeperClient.setData("/test/trubbo/test", "test changed");
        Thread.sleep(1000);
        zookeeperClient.deletePath("/test/trubbo/test");
        Thread.sleep(1000);
        zookeeperClient.deletePath("/test/trubbo");
    }

    static class ListenerCallbackImpl implements ListenerCallback {
        @Override
        public void callback(CuratorCacheListener.Type type, ChildData data, ChildData oldData) {
            if (type == CuratorCacheListener.Type.NODE_DELETED) {
                System.out.println("type: " + type + ", oldData's path: " + oldData.getPath() + " oldData's data" + new String(oldData.getData()));
            }
            if (data == null) {
                System.out.println("type: " + type);
                return;
            }
            System.out.println("type: " + type + ", data's path: " + data.getPath() + ", data's data: " + new String(data.getData()));
        }
    }
}
