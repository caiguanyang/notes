import java.util.concurrent.CountDownLatch;
import java.lang.Thread;
import java.lang.Runnable;

public class LatchedThread extends Thread{
    private final CountDownLatch startSignal;

    public LatchedThread(CountDownLatch latch, int num) {
        super("worker-" + num);
        this.startSignal = latch;
    }

    public void run() {
        try{
            startSignal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String name = this.getName();
        long timestamp = System.currentTimeMillis();
        System.out.println(name+" : "+timestamp);
    }
}
