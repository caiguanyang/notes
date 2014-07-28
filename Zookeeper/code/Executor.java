import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.lang.Runnable;
import java.io.IOException;

public class Executor
    implements Watcher, Runnable, DataMonitor.DataMonitorListener {
    private ZooKeeper zk;
    private String znode;
    private DataMonitor dm;

    public Executor(String hostPort, String znode)
        throws KeeperException, IOException {
        zk = new ZooKeeper(hostPort, 3000, this);
        dm = new DataMonitor(zk, znode, this);
    }
    /**
     * test
     */
    public static void main(String[] args) {
        String hostport = args[0];
        String znode = args[1];

        try {
            new Executor(hostport, znode).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try{
            synchronized(this) {
                while (!dm.dead) {
                    wait();
                }
            }
            System.out.println("**Clossed**");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void process(WatchedEvent event) {
        System.out.println("**Executor Trigger Event:"+event.getType().toString());
        dm.process(event);
    }
    @Override
    public void close(int rc) {
        synchronized(this) {
            notifyAll();
        }
        System.out.println("**Clossing");
    }
}
