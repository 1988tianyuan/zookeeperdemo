package servicediscovery;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;


public class ServerTest {
    private static final String zkConnectionString
            = "192.168.142.128:2181,192.168.142.131:2181,192.168.142.132:2181";
    private static final RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
    private static final CuratorFramework zkClient =  CuratorFrameworkFactory.newClient(zkConnectionString, retryPolicy);
    private static final String serverPath = "/testServer";

    public static void main(String[] args) throws Exception{
        zkClient.start();
        System.out.println("开始连接到zookeeper");
        String hostName = "server3";
        Thread.sleep(2000);
        zkClient.create().withMode(CreateMode.EPHEMERAL).forPath(serverPath+"/"+hostName, hostName.getBytes());
        System.out.println("创建临时节点完毕：" + new String(zkClient.getData().forPath(serverPath+"/"+hostName)));
        Thread.sleep(Long.MAX_VALUE);
    }
}
