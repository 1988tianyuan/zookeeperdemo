import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

public class MainTest {

    private static final String zkConnectionString
            = "192.168.142.128:2181,192.168.142.131:2181,192.168.142.132:2181";
    private static final RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
    private static final CuratorFramework zkClient =  CuratorFrameworkFactory.newClient(zkConnectionString, retryPolicy);
    private static final String serverPath = "/testServer";
    private static ConcurrentSkipListSet<String> serverIpSet = new ConcurrentSkipListSet<>();

    public static void main(String[] args) throws Exception{
        zkClient.start();
        System.out.println("开始监听");
        PathChildrenCache childrenCache = new PathChildrenCache(zkClient, serverPath, true);
        childrenCache.getListenable().addListener((zkClient, event) -> {
            System.out.println("事件类型：" + event.getType());
            Type eventType = event.getType();
            handleEvent(eventType);
        });
        childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        Thread.sleep(Long.MAX_VALUE);
    }

    private static void handleEvent(Type eventType) throws Exception{
        List<String> serverIpList = zkClient.getChildren().forPath(serverPath);
        switch (eventType){
            case CHILD_ADDED:
                System.out.println("当前注册host：" + serverIpList);
                break;
            case CHILD_REMOVED:
                System.out.println("当前注册host：" + serverIpList);
                break;
            case CHILD_UPDATED:
                System.out.println("当前注册host：" + serverIpList);
                break;
            case CONNECTION_LOST:
                break;
            case CONNECTION_RECONNECTED:
                break;
        }
    }
}
