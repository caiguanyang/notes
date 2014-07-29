import java.util.concurrent.TimeUnit;
import java.util.Date;

public class Barrier_app_1 {
    public static void main(String[] args) {
        String hostport = "127.0.0.1:2181";
        String root = "/barrier";
        int bsize = 2;
        String nodename = "barrier_app_1";
        try {
            Barrier br = new Barrier(hostport, root, bsize, nodename);
            Date tmp = new Date();
            System.out.println("**"+tmp+" app-1: waiting other app...");
            if (br.enter()) {
                Date date = new Date();
                System.out.println("**"+date+" app-1: start working...");
                TimeUnit.SECONDS.sleep((long)(Math.random()*10));
                date = new Date();
                System.out.println("**"+date+" app-1: finish wrok...");
            }
            if (br.leave()) {
                Date date = new Date();
                System.out.println("**"+date+" app-1: end wrok...");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
