<meta http-equiv="content-type" content="text/html; charset=UTF-8">

T：2014-05-15

Java的ForkJoin并行框架
-----------------------------
###1. 介绍
&emsp;&emsp;ForkJoin是适用于多核环境的轻量级并行框架,是为那些能够被递归地拆解成子任务的工作类型量身设计的，充分利用多处理器来提高效率和加速运行。  
&emsp;&emsp;对于并行框架，重要的两个模块是执行任务的资源（线程、分布式中的计算机）和任务的调度。ForkJoin由于是单机模式，采用线程池来执行用户提交的任务，使用work-stealing算法通过有限的线程来调度大量任务的执行。  


###2. 结构及使用
ForkJoinPool本身实现了ExecutorService接口，负责调度执行ForkJoinTask。
ForkJoinTask是提交给ForkJoinPool 执行的任务，本身也实现了Future 接口。

ForkJoinTask有两个子类RecursiveAction和RecursiveTask。 RecursiveAction 没有返回值（只需fork）；RecursiveTask有返回值（需要合并）。类似于Runnable和 Callable一样。

用户的主要工作是实现ForkJoinTask类，重载compute方法细分任务（递归），调用fork方法提交任务，然后调用join方法阻塞等待处理结果。

###3. work-stealing算法
&emsp;&emsp;work-stealing算法提高了应用的吞吐量，主要是因为它的去中心化思想，线程可以从自己线程也可以去别的线程获取任务。避免性能瓶颈的策略是将所有控制变量封装到一个64位的**volatile**变量中（？？？）。

###4. ForkJoinPool实现
&emsp;&emsp;此类用来集中管理工作线程集，关键点有：  
1）(Work-Stealing)每个线程维护了一个任务队列，采用FIFO（当然也可以是LIFO）从自身队列中取任务执行，如果当前队列没有任务可执行，则随机选择一个工作线程，从他们的任务队列中以FIFO顺序窃取一个任务执行（work-stealing）。  
2）(Recording Workers)采用一个数组记录工作线程，采用数组而非其他结构的原因是为了方便采用基于索引的随机获取线程。通过使用“sqlLock”锁老保证并发访问此共享变量的线程安全性。  
3）(Wait Queuing)维护一个等待队列，暂存那些没有任务要执行的线程。但是要处理好唤醒（Java中没有自旋等待，没有暂停/开始操作）；避免ABA影响；避免一个生产线程刚产生了任务，同时一个线程因未发现任务要进入等待队列这种情况的发生。  
4）(Singalling)信号机制。  
5）Trimming Workers(辅助线程)：释放那些一定周期内未使用的资源，一个工作线程会在没有任务执行的情况下进入等待状态，直到**SHRINK_RATE**纳秒后超时中止。  
6）Submissons：采用与工作线程数组一致的队列来保存任务，主要的不同是采用**submissionLock**保证**addSubmission**方法的线程安全性（多个线程可以同时添加任务？？）。  
7）Compensation：此框架的主要作用就是要在一个线程阻塞在join操作上时能盗取其他任务。主要有两个策略：  

    Helping： 主要调用work-stealing算法为此线程分配任务；
    Compensating: 除非已经确定有足够的存活线程，否则会创建一个新线程执行任务，来补偿这个被阻塞的线程。
（问题：如果知道当前存活的线程）  
8）Shutdown and Termination.   

**注意**  
1)ForkJoinPool, ForkJoinWorkerThread,ForkJoinTask类中保存的状态在他们之间是可以互相访问的，所以修改一处可能会导致其他算法的更改，这在实现角度看起来比较丑陋，但是也正是这种紧耦合的实现方式提高了此框架的性能，使他可以每秒处理10亿级的任务。（为什么？？看Jigsaw时，类之间也是也是这种紧耦合）   

*心得*：看源码时要从他们的需求着手，需求的不同也会导致实现上的差异，就像上面提到的三个类之间的状态重复，耦合。  

2）限制并发数为(1<<15)-1，使他们的id, counts, negations限制在16位的字段中。方便其他位操作。   

API文档中说明支持的最大线程数为32767，默认使用CPU核心数量的线程数： 

    Implementation notes: This implementation restricts the maximum number of running threads to 32767. 
    Attempts to create pools with greater than the maximum number result in IllegalArgumentException. 


###参考资料
1）http://han.guokai.blog.163.com/blog/static/13671827120118853143418/  
2）Java1.7 API文档（重要）