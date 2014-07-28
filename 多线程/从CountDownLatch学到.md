<meta http-equiv="content-type" content="text/html; charset=UTF-8">

T：2014-07-22  
 tomato: 2+1+3+

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

    public class Runner implements Runnable{
        private final CountDownLatch startSignal;
        private String name;
    
        public Runner(CountDownLatch latch, int num) {
            name = "runner-"+num;
            this.startSignal = latch;
        }
        public void run() {
            // ready to run
            int readyTime = (int)(Math.random()*1000);
            try {
                Thread.sleep(readyTime);
                System.out.println(name+": ready time - "+readyTime);
                // waitting
                startSignal.await();
                System.out.println("**"+name+" start run ....");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
*2. controller*  

    CountDownLatch startLatch = new CountDownLatch(1); // countDownLatch初始化为1

    for (int i=0; i<4; i++) {
        Thread t = new Thread(new Runner(startLatch, i));
        t.start();
    }
    try{
       Thread.sleep(800);   // 主线程等待800ms，就将计数器减1
    } catch (InterruptedException e) {
       e.printStackTrace();
    }
    startLatch.countDown();
*Result:*  
*format: thread-name : timestamp*  

    ****result-1****
    runner-2: ready time - 63
    runner-3: ready time - 390
    runner-0: ready time - 538
    runner-1: ready time - 771
    **runner-2 start run ....
    **runner-0 start run ....
    **runner-3 start run ....
    **runner-1 start run ....

    ****result-2****
    runner-3: ready time - 295
    **runner-3 start run ....
    runner-2: ready time - 804
    **runner-2 start run ....
    runner-1: ready time - 976
    **runner-1 start run ....
    runner-0: ready time - 993
    **runner-0 start run ....

**说明：**  
1. countdownLatch初始化为1， 主线程执行countDown()操作将计数减1到0，那些由于执行await()操作而阻塞的线程会继续向下执行；  
但是计数值不能重置为1， 此后一直都为0，因此其他要执行await()操作的线程将不会被阻塞，直接往下执行，这也是countDownLatch不会丢失信号的原因。  
如果想重置计数，考虑使用CyclicBarrier;  
2. 主线程等待一定时间将计数器减1， 所有阻塞的线程都被释放（参考result-1, 如果大家的准备时间比800ms短，那么他们会一起开始往下执行）；此后等待此闭锁的线程不会被阻塞，直接往下执行（参考result-2）  
3. 通过结果可以看出，闭锁只是让一个或多个线程等待，要想让他们同时启动，控制线程要将子线程初始化资源所花费的最长时间设定为等待时间，当然也可以将耗时的操作放置到*countdownlatch.await()*之后;

####1.3 如何实现等待多个线程终止
**代码：**  
*1. TaskExecutor*

    public class TaskExecutor implements Runnable{
        private final CountDownLatch endLatch;
        private String name;
    
        public TaskExecutor(CountDownLatch latch, int num) {
            name = "subtask-"+num;
            this.endLatch = latch;
        }
    
        public void run() {
            try {
                int runTime = (int) (Math.random()*2000);
                Thread.sleep(runTime);
                System.out.println(name+": run time - "+runTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                endLatch.countDown();
            }
        }
    }

*2. controller*  

    int subtaskNum = 5;
    CountDownLatch endLatch = new CountDownLatch(subtaskNum); // 初始化为子线程的个数
    System.out.println("**start working**");
    for (int i=0; i<subtaskNum; i++) {
        Thread t = new Thread(new TaskExecutor(endLatch, i));
        t.start();
    }
    try {
        endLatch.await();   // 等待所有子线程执行完子任务
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    System.out.println("**finish task**");

*3. result*  

    **start working**
    subtask-3: run time - 44
    subtask-0: run time - 1035
    subtask-1: run time - 1038
    subtask-2: run time - 1409
    subtask-4: run time - 1919
    **finish task**

**说明：**  
1）在一个程序中使用两个CountDownLatch则也可以将这两个例子合并起来：N个线程同时启动，等待他们都运行完成后结束。  
2）与使用join()操作等待多个线程终止的区别？  
  join()操作是指等待调用它的线程的中止，因此我们需要保存子任务线程的引用，以便在最后循环调用每个子线程的join()操作，等待所有线程终止。  
  使用闭锁的话则不需要，闭锁做个一个状态，监控子线程的完成情况，我们只需要检测闭锁的状态就能知道是否所有子线程都已执行完成。

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
*1. subtask*  

*2. controller*  


*3. result*
    
    result-1: use cyclicBarrier
    Mon Jul 21 03:47:24 CST 2014: person-0 arive!
    Mon Jul 21 03:47:25 CST 2014: person-1 arive!
    Mon Jul 21 03:47:25 CST 2014: person-2 arive!
    Mon Jul 21 03:47:25 CST 2014: person-2 > let's go to picnic...
    Mon Jul 21 03:47:25 CST 2014: person-1 > let's go to picnic...
    Mon Jul 21 03:47:25 CST 2014: person-0 > let's go to picnic...
    Mon Jul 21 03:47:25 CST 2014: person-0 > I am waiting them...
    Mon Jul 21 03:47:26 CST 2014: person-1 > I am waiting them...
    Mon Jul 21 03:47:26 CST 2014: person-2 > I am waiting them...
    Mon Jul 21 03:47:26 CST 2014: person-2 > let's go to home...
    Mon Jul 21 03:47:26 CST 2014: person-1 > let's go to home...
    Mon Jul 21 03:47:26 CST 2014: person-0 > let's go to home...

    result-2: not use cyclicBarrier
    Mon Jul 21 03:53:21 CST 2014: person-2 arive!
    Mon Jul 21 03:53:21 CST 2014: person-2 > let's go to picnic...
    Mon Jul 21 03:53:22 CST 2014: person-1 arive!
    Mon Jul 21 03:53:22 CST 2014: person-1 > let's go to picnic...
    Mon Jul 21 03:53:22 CST 2014: person-2 > I am waiting them...
    Mon Jul 21 03:53:22 CST 2014: person-2 > let's go to home...
    Mon Jul 21 03:53:22 CST 2014: person-0 arive!
    Mon Jul 21 03:53:22 CST 2014: person-0 > let's go to picnic...
    Mon Jul 21 03:53:23 CST 2014: person-0 > I am waiting them...
    Mon Jul 21 03:53:23 CST 2014: person-0 > let's go to home...
    Mon Jul 21 03:53:24 CST 2014: person-1 > I am waiting them...
    Mon Jul 21 03:53:24 CST 2014: person-1 > let's go to home...

**说明：**  
1）相比countDownLatch, barrier可以循环使用；例子中使用了同一个barrier控制了两次线程等待；  
2）1.2中是由主线程控制开始的时间的，但是主线程并不知道每个线程的初始化时间，因此按自己设定的一个时间开始的，就像“赛跑”，一开令枪，大家都开始跑，准备不好是运动员的事情了，执行完自己的任务会花费更长的时间。但是barrier则有点不同，线程之间互相等待，大家都准备好了，咱们再一起走。从结果图中可以看出从集合，聚餐，到回家，大家都是一块行动的（例子只是为了说明barrier可以循环使用，实际的应用场景我还并不清楚呢）。

<font color=ff0000>**问题：**</font>  
1) CyclicBarrier是否可以替代CountDownLatch?  
按说明2的描述，有不同的场景，没有谁替代谁之说，根据应用需求采用即可。  
区别可参考：http://xumingming.sinaapp.com/215/countdownlatch-vs-cyclicbarrier/  

2) 两个工具性能上有差别吗？  

####2.3 源码实现  


