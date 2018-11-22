import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;

import static org.apache.zookeeper.Watcher.Event.EventType.NodeChildrenChanged;
import static org.apache.zookeeper.Watcher.Event.EventType.NodeDataChanged;

public class BaseTest {

    private static final String zkConnectionString
            = "192.168.142.128:2181,192.168.142.129:2181,192.168.142.130:2181";
    private final RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
    private final CuratorFramework zkClient =  CuratorFrameworkFactory.newClient(zkConnectionString, retryPolicy);
    private Watcher watcher;

    @Before
    public void setup() {
        zkClient.start();
        watcher = watchedEvent -> {
            System.out.println("事件类型：" + watchedEvent.getType());
            System.out.println("事件路径：" + watchedEvent.getPath());
            if(watchedEvent.getType().equals(NodeDataChanged)) {
                try {
                    System.out.println("修改后的数据是：" + new String(zkClient.getData().forPath(watchedEvent.getPath()), Charset.defaultCharset()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if (watchedEvent.getType().equals(NodeChildrenChanged)) {
                try {
                    System.out.println("修改后的子节点是：" + zkClient.getChildren().forPath("/mytest"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                zkClient.getData().usingWatcher(watcher).forPath("/mytest");
                zkClient.getChildren().usingWatcher(watcher).forPath("/mytest");
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    @Test
    public void getData() throws Exception {
        String myTest = new String(zkClient.getData().forPath("/mytest"), Charset.defaultCharset());
        System.out.println("myTest的data是：" + myTest);
    }

    @Test
    public void setData() throws Exception {
        Stat stat = zkClient.setData().forPath("/mytest", "哼哼".getBytes());
        System.out.println("修改后：" + stat);
    }

    @Test
    public void getDataByWatch() throws Exception {
        zkClient.getData().usingWatcher(watcher).forPath("/mytest");
        zkClient.getChildren().usingWatcher(watcher).forPath("/mytest");
        Thread.sleep(Long.MAX_VALUE);
    }

    @After
    public void finish() {
        zkClient.close();
    }

}
