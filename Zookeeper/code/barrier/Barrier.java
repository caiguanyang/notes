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

public class Barrier implements Watcher{
    static ZooKeeper zk;
    static Integer mutex;
    int bsize;
    String root;
    String name;

    public Barrier(String hostPort, String root, int size, String nodename) {
        this.bsize = size;
        this.root = root;
        this.name = nodename;

        if (zk==null) {
            // initial zookeeper
            try {
                System.out.println("**Startting ZK:");
                zk = new ZooKeeper(hostPort, 3000, this);
                mutex = new Integer(-1);
                System.out.println("**Finish Startting...");
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
                    zk.create(root, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
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
        // there are a change on root node
        synchronized(mutex) {
            mutex.notify();
        }
    }
    /**
     * enter the barrier
     */
    public boolean enter() throws KeeperException, InterruptedException {
        // create a child node of root
        zk.create(root+"/"+name, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        while (true) {
            synchronized(mutex) {
                List<String> children = zk.getChildren(root, true);
                if (children.size() < bsize) {
                    mutex.wait();
                } else {
                    return true;
                }
            }
        }
    }
    /**
     * leave the barrier
     */
    public boolean leave() throws KeeperException, InterruptedException{
        zk.delete(root+"/"+name, 0);
        while (true) {
            synchronized(mutex) {
                List<String> children = zk.getChildren(root, true);
                if (children.size() > 0) {
                    mutex.wait();
                } else {
                    return true;
                }
            }
        }
    }
}
