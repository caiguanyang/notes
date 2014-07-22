<meta http-equiv="content-type" content="text/html; charset=UTF-8">

T：2014-07-22  
 tomato: 2+

从CountDownLatch学到...
-----------------------------------
[TOC]

###1 CountDownLatch基础
####1.1 概述


####1.2 如何实现多个线程同时启动
**代码：**  
*1.LatchedThread*  

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
*test_countdown*  

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
*Result:*  
*format: thread-name : timestamp*  

    worker-0 : 1405872106781  
    worker-3 : 1405872106783
    worker-2 : 1405872106783
    worker-1 : 1405872106783

**说明：**  

####1.3 如何实现等待多个线程终止
**代码：**  



*GitHub:*  

**说明：**  


采用其他多线程工具实现上述功能
-----------------------------------
###2 

