import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.AsyncCallback.StatCallback;

import java.lang.Runnable;
import java.util.Arrays;

public class DataMonitor implements Watcher, StatCallback {
    private ZooKeeper zk;
    private String znode;
    public volatile boolean dead;
    private DataMonitorListener listener;
    byte prevData[];

    public DataMonitor(ZooKeeper zk, String znode, DataMonitorListener listener) {
        this.zk = zk;
        this.znode = znode;
        this.listener = listener;

        zk.exists(znode, true, this, null);
    }

    public void process(WatchedEvent event) {
        String path = event.getPath();
        if (event.getType() == Event.EventType.None) {
            switch (event.getState()) {
                case SyncConnected:{
                    System.out.println("**KeepState: SyncConnected");
                    break;
                }
                case Expired: {
                    this.dead = true;
                    System.out.println("**KeeperState: Expired");
                    listener.close(KeeperException.Code.SESSIONEXPIRED.intValue());
                    break;
                }
                case Disconnected: {
                    this.dead = true;
                    System.out.println("**KeeperState: Disconnected");
                    listener.close(KeeperException.Code.CONNECTIONLOSS.intValue());
                    break;
                }
            }
        } else {
            if (path != null && path.equals(znode))  {
                // something has changed on the node, let's find out
                System.out.println("** add watcher");
                // 使用zk构造函数传递的watcher实例监听节点变化
                // （当布尔值watcher为true,使用默认watcher,当为
                // false时，标示不再注册监听，当传递的有watcher实例时，
                // 使用最新的watcher监听节点变化）
                // 注意：watcher是一次性有效的，触发后如果还想监听，则仍需要
                // 注册监听事件
                zk.exists(znode, true, this, null);
            }
        }
    }
    @Override
    /**
     * asynchronis event
     */
    public void processResult(int rc, String path,
            Object ctx, Stat stat) {
        boolean exists = false;
        Code cd = Code.get(rc);
        System.out.println("***processResult");
        switch(cd) {
            case OK: {
                exists = true;
                break;
            }
            case NONODE: {
                exists = false;
                break;
            }
            case SESSIONEXPIRED:
            case NOAUTH: {
                dead = true;
                listener.close(rc);
                return;
            }
            default: {
                // retry errors
                zk.exists(znode, true, this, null);
            }

        }
        byte[] b = null;
        if (exists) {
            try {
                b = zk.getData(znode, false, null);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                return;
            }
        }
        if ((b==null && b!=prevData) ||
                (b!=null && !Arrays.equals(prevData, b))) {
            if (b==null)
                System.out.println("**data: null");
            else
                System.out.println("**data: "+ new String(b));
            prevData = b;
        }
    }

    public static interface DataMonitorListener {
        public void close(int rc);
    }
}
