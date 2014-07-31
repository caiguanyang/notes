import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.lang.Runnable;
import java.io.IOException;
import java.util.List;
import java.util.Collections;

public class DistributeLock implements Watcher{
    ZooKeeper zk;
    Integer mutex;
    String lockpath;
    String root;

    public DistributeLock(String hostPort) {
        mutex = new Integer(-1);
        root = "/lock";
        if (zk==null) {
            // initial zookeeper
            try {
                System.out.println("**Startting ZK:");
                zk = new ZooKeeper(hostPort, 3000, this);
                System.out.println("**Finish Startting zk...");
            } catch (IOException e) {
                e.printStackTrace();
                zk = null;
            }
        }
        // create root node
        if (zk != null) {
            try {
                Stat st = zk.exists(root, false);
                if (st == null) {
                    zk.create(root, new byte[0], Ids.OPEN_ACL_UNSAFE,
                            CreateMode.PERSISTENT);
                }
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void process(WatchedEvent event) {
        synchronized(mutex) {
            mutex.notify();
        }
    }

    public void lock() throws KeeperException, InterruptedException{
        lockpath = zk.create(root+"/clock-", new byte[0], Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);
        while (true) {
            synchronized(mutex) {
                List<String> children = zk.getChildren(root, false);
                String watchpath = this.getNodeBefore(children);
                if (watchpath.equals(lockpath)) {
                    return;
                } else {
                    // listen the state of front node
                    zk.exists(watchpath, true);
                    mutex.wait();
                }

            }
        }
    }

    public void unlock() throws KeeperException, InterruptedException{
        zk.delete(lockpath, 0);
    }

    public boolean trylock() throws KeeperException, InterruptedException{
        lockpath = zk.create(root+"/clock-", new byte[0], Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);
        while (true) {
            synchronized(mutex) {
                List<String> children = zk.getChildren(root, false);
                String watchpath = this.getNodeBefore(children);
                if (watchpath.equals(lockpath)) {
                    return true;
                } else {
                    return false;
                }

            }
        }

    }
    /**
     * get the node whose seq-num is little the current node
     */
    private String getNodeBefore(List<String> nodes) {
        Collections.sort(nodes);
        String before = "";
        for (String s : nodes) {
            String tmp = root + "/" + s;
            if (tmp.equals(lockpath))
                break;
            before = tmp;
        }
        if (before.equals(""))
            before = lockpath;
        System.out.println("lockpath："+lockpath+"  before:"+before);
        return before;
    }
}
