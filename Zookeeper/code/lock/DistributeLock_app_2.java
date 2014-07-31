import java.util.concurrent.TimeUnit;
import java.lang.Thread;
import java.lang.InterruptedException;
import org.apache.zookeeper.KeeperException;
import java.util.Date;

public class DistributeLock_app_2{
    public static void main(String[] args)
        throws KeeperException, InterruptedException{
        String hostport = "127.0.0.1:2181";
        DistributeLock dlock = new DistributeLock(hostport);
        String app_name = "app_2";

        try {
            Date date = new Date();
            System.out.println(date+" : app-2 initial...");
            dlock.lock();
            System.out.println(date+" : app-2 start working...");
            TimeUnit.SECONDS.sleep(3);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        finally {
            dlock.unlock();
            Date date = new Date();
            System.out.println(date+" : app-2 end...");
        }
    }
}
