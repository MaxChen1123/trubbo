package com.maxchen.trubbo.remoting.zookeeper;

import com.maxchen.trubbo.common.URL.URL;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.KeeperException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ZookeeperClient {
    private final CuratorFramework client;
    private final URL url;
    private static final Map<String, CuratorCache> CACHE_MAP = new ConcurrentHashMap<>();
    private static final Map<String, ZookeeperListener> LISTENER_MAP = new ConcurrentHashMap<>();

    public ZookeeperClient(URL url) {
        try {
            this.url = url;
            this.client = CuratorFrameworkFactory.newClient(url.getRemoteAddress(), new RetryNTimes(1, 1000));
            this.client.start();
            this.client.blockUntilConnected();
            log.info("zookeeper connect success");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void createPath(String path) {
        try {
            client.create().creatingParentsIfNeeded().forPath(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getData(String path) {
        try {
            return new String(client.getData().forPath(path));
        } catch (KeeperException.NoNodeException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void createPath(String path, String data) {
        try {
            client.create().creatingParentsIfNeeded().forPath(path, data.getBytes());
        } catch (KeeperException.NodeExistsException e) {
            try {
                client.setData().forPath(path, data.getBytes());
            } catch (Exception e1) {
                throw new IllegalStateException(e.getMessage(), e1);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void setData(String path, String data) {
        try {
            client.setData().forPath(path, data.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deletePath(String path) {
        try {
            client.delete().deletingChildrenIfNeeded().forPath(path);
        } catch (KeeperException.NoNodeException e) {
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void watchPath(String path, ZookeeperListener listener) {
        CuratorCache cache = CACHE_MAP.computeIfAbsent(path, k -> CuratorCache.build(client, path));
        LISTENER_MAP.put(path, listener);
        cache.listenable().addListener(listener);
        cache.start();
    }

    public void unwatchPath(String path) {
        CuratorCache cache = CACHE_MAP.remove(path);
        ZookeeperListener listener = LISTENER_MAP.remove(path);
        cache.close();
    }

    public List<String> getChildren(String path) {
        try {
            return client.getChildren().forPath(path);
        } catch (KeeperException.NoNodeException e) {
            return null;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public boolean checkExists(String path) {
        try {
            if (client.checkExists().forPath(path) != null) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public boolean isConnected() {
        return client.getZookeeperClient().isConnected();
    }


}
