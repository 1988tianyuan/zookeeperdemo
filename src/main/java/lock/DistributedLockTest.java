package lock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

public class DistributedLockTest {

    private static final String zkConnectionString
            = "192.168.142.128:2181,192.168.142.131:2181,192.168.142.132:2181";
    private static final RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
    private static final CuratorFramework zkClient =  CuratorFrameworkFactory.newClient(zkConnectionString, retryPolicy);

    public static void main(String[] args) throws Exception{
        zkClient.start();
        String host = args[0];
        InterProcessMutex lock = new InterProcessMutex(zkClient, "/lock");
        System.out.println(host + "开始尝试获取锁...");
        if (lock.acquire(20, TimeUnit.SECONDS)) {
            try {
                System.out.println(host + "获取到锁...");
                System.out.println(host + "开始执行代码...");
                Thread.sleep(20000);
                System.out.println(host + "代码执行完了...");
            } finally {
                lock.release();
                System.out.println(host + "释放了锁");
                zkClient.close();
            }
        } else {
            System.out.println(host + "获取锁失败...");
            zkClient.close();
        }
    }

}
