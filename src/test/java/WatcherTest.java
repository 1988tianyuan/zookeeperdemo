import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WatcherTest {
    private static final String zkConnectionString
            = "192.168.142.128:2181,192.168.142.129:2181,192.168.142.130:2181";
    private final RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
    private final CuratorFramework zkClient =  CuratorFrameworkFactory.newClient(zkConnectionString, retryPolicy);

    @Before
    public void setUp() {
        zkClient.start();
    }

    @Test
    public void pathChildrenTest() throws Exception {
        String path = "/mytest";
        PathChildrenCache childrenCache = new PathChildrenCache(zkClient, path, true);
        childrenCache.getListenable().addListener((zkClient, event) -> {
            System.out.println("事件类型：" + event.getType());
            System.out.println("孩子们：" + event.getInitialData());
            System.out.println("孩子当前数据：" + new String(event.getData().getData()));
            System.out.println("变化的孩子：" + event.getData().getPath());
        });
        childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        Thread.sleep(Long.MAX_VALUE);
    }

    @Test
    public void TreePathTest() throws Exception {
        String path = "/mytest";
        TreeCache treeCache = new TreeCache(zkClient, path);
        treeCache.getListenable().addListener((zkClient, event) -> {
            System.out.println("事件类型：" + event.getType());
            System.out.println("孩子当前数据：" + new String(event.getData().getData()));
            System.out.println("变化的孩子：" + event.getData().getPath());
        });
        treeCache.start();
        Thread.sleep(Long.MAX_VALUE);
    }



    @After
    public void finish() {
        zkClient.close();
    }

}
