package selection;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

import java.io.Closeable;
import java.io.IOException;

public class SelectionClient extends LeaderSelectorListenerAdapter implements Closeable {

    private final LeaderSelector leaderSelector;
    private final String name;
    private static final String path = "/selection";

    public SelectionClient(CuratorFramework zkClient, String name) {
        this.leaderSelector = new LeaderSelector(zkClient, path, this);
        this.name = name;
        this.leaderSelector.autoRequeue(); //放弃leader职位后重新参选
    }

    public void start() {
        leaderSelector.start();
    }

    @Override
    public void close() throws IOException {
        leaderSelector.close();
    }

    @Override
    public void takeLeadership(CuratorFramework zkClient) throws Exception {
        final int waitSeconds = (int)(5 * Math.random()) + 1;
        System.out.println(name + ": 哈哈哈哈我是领导啦！");
        System.out.println(name + ": 休息" + waitSeconds + "秒");
        Thread.sleep(waitSeconds * 1000);
        System.out.println(name + ": 交出领导职位。。。");
    }
}
