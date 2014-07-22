import java.util.concurrent.CountDownLatch;
import java.lang.Thread;
import java.lang.Runnable;
/**
 * make multi threads start at the same time
 */
public class t_countdownlatch{
    public static void main(String[] args) {
        CountDownLatch startLatch = new CountDownLatch(1);
        for (int i=0; i<4; i++) {
            Thread t = new LatchedThread(startLatch, i);
            t.start();
        }
        try{
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startLatch.countDown();
    }
}