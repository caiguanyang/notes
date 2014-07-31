import java.util.concurrent.TimeUnit;
import java.lang.Thread;
import java.lang.InterruptedException;
import java.util.Date;
import org.apache.zookeeper.KeeperException;

public class DistributeLock_app_1{
    public static void main(String[] args)
        throws KeeperException, InterruptedException{
        String hostport = "127.0.0.1:2181";
        DistributeLock dlock = new DistributeLock(hostport);
        String app_name = "app_1";

        try {
            Date date = new Date();
            System.out.println(date+" : app-1 initial...");
            dlock.lock();
            System.out.println(date+" : app-1 start working...");
            TimeUnit.SECONDS.sleep(6);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            dlock.unlock();
            Date date = new Date();
            System.out.println(date+" : app-1 end...");
        }
    }
}
