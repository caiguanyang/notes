<meta http-equiv="content-type" content="text/html; charset=UTF-8">

T：2014-07-22  
 tomato: 2+1+

从CountDownLatch学到...
-----------------------------------
[TOC]

###1 CountDownLatch
####1.1 概述
倒计数门闩（闭锁），它是一个同步辅助类，可以让一个或者多个线程等待；典型场景有可以使多个线程在同一时间启动（让多个线程等待），也可将一个任务分成N份（N个线程），当N个子任务都运行完了主任务继续执行(让一个线程等待)。  

“闭锁”：

####1.2 如何实现多个线程同时启动
**场景：**  
N个人赛跑，他们同时开始起跑...  

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
*2. controller*  

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
countdownLatch初始化为1， 主线程执行countDown()操作将计数减1到0，那些由于执行await()操作而阻塞的线程会继续向下执行；  
但是计数值不能重置为1， 此后一直都为0，因此其他要执行await()操作的线程将不会被阻塞，直接往下执行，这也是countDownLatch不会丢失信号的原因。  
如果想重置计数，考虑使用CyclicBarrier;

####1.3 如何实现等待多个线程终止
**代码：**  




**说明：**  
1）在一个程序中使用两个CountDownLatch则也可以将这两个例子合并起来：N个线程同时启动，等待他们都运行完成后结束。

####1.4 源码实现重要知识点


采用其他多线程工具实现上述功能
-----------------------------------
###2 CyclicBarrier
####2.1 概述

####2.2 实例
**场景说明：**  
N个线程互相等待，当都准备好后一块执行（模仿1.2）；  
当这N个线程都执行完后则汇总结果（模仿1.3）;  
开始下一轮的运算（特有）；  
**代码**  



**说明：**  
1）相比countDownLatch, barrier可以循环使用；例子中使用了同一个barrier控制了两次线程等待；  
2）1.2中是由主线程控制开始的时间的，但是主线程并不知道每个线程的初始化时间，因此按自己设定的一个时间开始的，就像“赛跑”，一开令枪，大家都开始跑，准备不好使运动员的事情了，呵呵，有点残酷。但是barrier则有点不同，线程之间互相等待，大家都准备好了，咱们再一起走。

<font color=ff0000>**问题：**</font>  
1) CyclicBarrier是否可以替代CountDownLatch?  
按说明2的描述，有不同的场景，没有谁替代谁之说，根据应用需求采用即可。  
区别可参考：http://xumingming.sinaapp.com/215/countdownlatch-vs-cyclicbarrier/  

2) 两个工具性能上有差别吗？  

####2.3 源码实现  


###3. Semaphore 
####3.1 概述
信号量，是一个线程同步工具，用于在多个线程间传递信号。

####3.2 示例


####3.3 源码实现
参考： http://ifeve.com/semaphore/#more-4831  
