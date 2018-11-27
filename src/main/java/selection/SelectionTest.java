package selection;

import com.google.common.collect.Lists;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

public class SelectionTest {

    private static final String zkConnectionString
            = "192.168.142.128:2181,192.168.142.131:2181,192.168.142.132:2181";
    private static final RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    private static final int clientNum = 10;


    public static void main(String[] args) throws Exception {
        System.out.println("开始选举：");
        List<CuratorFramework> zkClients = Lists.newArrayList();
        List<SelectionClient> selectionClients = Lists.newArrayList();

        try {
            for(int i = 0; i < clientNum; i++) {
                CuratorFramework zkClient =  CuratorFrameworkFactory.newClient(zkConnectionString, retryPolicy);
                zkClients.add(zkClient);
                SelectionClient selectionClient = new SelectionClient(zkClient, "server"+i);
                selectionClients.add(selectionClient);
                zkClient.start();
                selectionClient.start();
            }

            System.out.println("选举完成...");

            System.out.println("Press enter/return to quit\n");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } finally {
            System.out.println("Shutting down...");
            for (SelectionClient exampleClient : selectionClients) {
                CloseableUtils.closeQuietly(exampleClient);
            }
            for (CuratorFramework client : zkClients) {
                CloseableUtils.closeQuietly(client);
            }
        }
    }
}
