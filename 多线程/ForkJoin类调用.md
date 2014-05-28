<meta http-equiv="content-type" content="text/html; charset=UTF-8">

T：2014-05-27

ForkJoin类调用
----------------------
###1. ForkJoinPool
1)ForkJoinWorkerThreadFactory：此工程类用于生成线程池使用的线程的，这样我们也可以自定义自己的ForkJoinWorkThread线程类，并定义工程类实现ForkJoinWorkerThreadFactory接口。  
2）ForkJoinWorkerThread[] workers： 采用数组实现队列，存储工作线程。  

3）ForkJoinTask<?>[] submissionQueue： 存储当前线程内的任务队列。  

4）volatile long ctl： 64位字节码保存线程池的多个状态；通过位移操作来查询他们的值。  

     Main pool control -- a long packed with:
     * AC: Number of active running workers minus target parallelism (16 bits)
     * TC: Number of total workers minus target parallelism (16bits)
     * ST: true if pool is terminating (1 bit)
     * EC: the wait count of top waiting thread (15 bits)
     * ID: ~poolIndex of top of Treiber stack of waiting threads (16 bits)
  

5）执行调用  
Execute(ForkJoinTask<?> task) / submit() -->   
submit() 参数可以是ForkJoinTask、Runnable、Callable,且内部提供了对Runnable、Callable到ForkJoinTask的适配。   
**code-1**    

    /**
     * Unless terminating, forks task if within an ongoing FJ
     * computation in the current pool, else submits as external task.
     */
    private <T> void forkOrSubmit(ForkJoinTask<T> task) {
        ForkJoinWorkerThread w;
        Thread t = Thread.currentThread();
        if (shutdown)
            throw new RejectedExecutionException();
        if ((t instanceof ForkJoinWorkerThread) &&
            (w = (ForkJoinWorkerThread)t).pool == this)
            w.pushTask(task);
        else
            addSubmission(task);
    }
  
注：每个FJWorkerThread都有自己的任务队列，执行任务时先从自己的队列中取任务，如果没有的话会触发Work-Stealing算法（参考‘Java 7 Concurrent Cookbook’第5章，当采用异步方法时，只有用户调用Join和get操作时才会触发，如用户调用fork时就不会触发）  
 **code-2**  

    ForkJoinWorkerThread.pushTask:    
    /**
     * Pushes a task. Call only from this thread.
     *
     * @param t the task. Caller must ensure non-null.
     */
    final void pushTask(ForkJoinTask<?> t) {
        ForkJoinTask<?>[] q; int s, m;
        if ((q = queue) != null) {    // ignore if queue removed
            long u = (((s = queueTop) & (m = q.length - 1)) << ASHIFT) + ABASE;
            UNSAFE.putOrderedObject(q, u, t);
            queueTop = s + 1;         // or use putOrderedInt
            if ((s -= queueBase) <= 2)
                pool.signalWork();
            else if (s == m)
                growQueue();
        }
    }  

从FJWorkerThread的方法中可以看出，最终会调用FJPool的signalWork()方法执行任务，另外*code-1* 中addSubmission()方法也会调用signalWork()方法来执行任务。  

另外需要注意的一点是：源码中多处使用UNSAFE类，这样避免了在程序中添加锁来保证线程安全性，从而提高了框架的执行效率。  

**code-3**  

    /**
     * Wakes up or creates a worker.
     */
    final void signalWork() {
        /*
         * The while condition is true if: (there is are too few total
         * workers OR there is at least one waiter) AND (there are too
         * few active workers OR the pool is terminating).  The value
         * of e distinguishes the remaining cases: zero (no waiters)
         * for create, negative if terminating (in which case do
         * nothing), else release a waiter. The secondary checks for
         * release (non-null array etc) can fail if the pool begins
         * terminating after the test, and don't impose any added cost
         * because JVMs must perform null and bounds checks anyway.
         */
        long c; int e, u;
        while ((((e = (int)(c = ctl)) | (u = (int)(c >>> 32))) &
                (INT_SIGN|SHORT_SIGN)) == (INT_SIGN|SHORT_SIGN) && e >= 0) {
            if (e > 0) {                         // release a waiting worker
                int i; ForkJoinWorkerThread w; ForkJoinWorkerThread[] ws;
                if ((ws = workers) == null ||
                    (i = ~e & SMASK) >= ws.length ||
                    (w = ws[i]) == null)
                    break;
                long nc = (((long)(w.nextWait & E_MASK)) |
                           ((long)(u + UAC_UNIT) << 32));
                if (w.eventCount == e &&
                    UNSAFE.compareAndSwapLong(this, ctlOffset, c, nc)) {
                    w.eventCount = (e + EC_UNIT) & E_MASK;
                    if (w.parked)
                        UNSAFE.unpark(w);
                    break;
                }
            }
            else if (UNSAFE.compareAndSwapLong
                     (this, ctlOffset, c,
                      (long)(((u + UTC_UNIT) & UTC_MASK) |
                             ((u + UAC_UNIT) & UAC_MASK)) << 32)) {
                addWorker();
                break;
            }
        }
    }

注：UNSAFE.unpark(w)，唤醒线程w.  
<font color="#FF0000" size="3">**问题：**</font>
此方法与线程的wait和signal有何区别？  
&emsp;&emsp;多线程通过某个对象进行通信，即常见的生产者消费者模型。在以前的jdk版本中是通过wait,notify方法实现的。该方法也是通过底层在某个信号量上的阻塞队列实现的。而Unsafe类中直接提供操作系统调度命令park,unpark,减少信号量的开销，提高性能。

addWorker()会调用ThreadFactory创建FJWorkerThread，并启动它。线程启动时会完成一些初始化工作，如初始化自身维护的任务队列，然后调用pool.work(this).  
**code-4**  

    /**
     * Top-level loop for worker threads: On each step: if the
     * previous step swept through all queues and found no tasks, or
     * there are excess threads, then possibly blocks. Otherwise,
     * scans for and, if found, executes a task. Returns when pool
     * and/or worker terminate.
     *
     * @param w the worker
     */
    final void work(ForkJoinWorkerThread w) {
        boolean swept = false;                // true on empty scans
        long c;
        while (!w.terminate && (int)(c = ctl) >= 0) {
            int a;                            // active count
            if (!swept && (a = (int)(c >> AC_SHIFT)) <= 0)
                swept = scan(w, a);
            else if (tryAwaitWork(w, c))
                swept = false;
        }
    } 
 
注：线程启动后并非直接从自己的队列去任务执行，因为刚创建，队列内没有任务可以执行。这是线程池会统一给新线程分配一个任务，而后线程回从自己的队列中查找任务。scan操作查找到任务后会调用FJWorkerThread的execTask(task)开始执行任务。线程一旦启动，只要线程池不停止就一直执行：有任务了执行任务，否则阻塞。  
**code-5**  

    /**
     * Scans for and, if found, executes one task. Scans start at a
     * random index of workers array, and randomly select the first
     * (2*#workers)-1 probes, and then, if all empty, resort to 2
     * circular sweeps, which is necessary to check quiescence. and
     * taking a submission only if no stealable tasks were found.  The
     * steal code inside the loop is a specialized form of
     * ForkJoinWorkerThread.deqTask, followed bookkeeping to support
     * helpJoinTask and signal propagation. The code for submission
     * queues is almost identical. On each steal, the worker completes
     * not only the task, but also all local tasks that this task may
     * have generated. On detecting staleness or contention when
     * trying to take a task, this method returns without finishing
     * sweep, which allows global state rechecks before retry.
     *
     * @param w the worker
     * @param a the number of active workers
     * @return true if swept all queues without finding a task
     */
    private boolean scan(ForkJoinWorkerThread w, int a) {
        int g = scanGuard; // mask 0 avoids useless scans if only one active
        int m = (parallelism == 1 - a && blockedCount == 0) ? 0 : g & SMASK;
        ForkJoinWorkerThread[] ws = workers;
        if (ws == null || ws.length <= m)         // staleness check
            return false;
        for (int r = w.seed, k = r, j = -(m + m); j <= m + m; ++j) {
            ForkJoinTask<?> t; ForkJoinTask<?>[] q; int b, i;
            ForkJoinWorkerThread v = ws[k & m];
            if (v != null && (b = v.queueBase) != v.queueTop &&
                (q = v.queue) != null && (i = (q.length - 1) & b) >= 0) {
                long u = (i << ASHIFT) + ABASE;
                if ((t = q[i]) != null && v.queueBase == b &&
                    UNSAFE.compareAndSwapObject(q, u, t, null)) {
                    int d = (v.queueBase = b + 1) - v.queueTop;
                    v.stealHint = w.poolIndex;
                    if (d != 0)
                        signalWork();             // propagate if nonempty
                    w.execTask(t);
                }
                r ^= r << 13; r ^= r >>> 17; w.seed = r ^ (r << 5);
                return false;                     // store next seed
            }
            else if (j < 0) {                     // xorshift
                r ^= r << 13; r ^= r >>> 17; k = r ^= r << 5;
            }
            else
                ++k;
        }
        if (scanGuard != g)                       // staleness check
            return false;
        else {                                    // try to take submission
            ForkJoinTask<?> t; ForkJoinTask<?>[] q; int b, i;
            if ((b = queueBase) != queueTop &&
                (q = submissionQueue) != null &&
                (i = (q.length - 1) & b) >= 0) {
                long u = (i << ASHIFT) + ABASE;
                if ((t = q[i]) != null && queueBase == b &&
                    UNSAFE.compareAndSwapObject(q, u, t, null)) {
                    queueBase = b + 1;
                    w.execTask(t);
                }
                return false;
            }
            return true;                         // all queues empty
        }
    }  
<font color='00999FF' size='3'><b>说明：</b></font>   
任务获取顺序如下：  
1.线程首次运行时从pool的任务队列中获取一个任务（FIFO），调用execTask(FJTask)执行。  
  从*code-2-1* 的代码可知，以后此线程执行任务的过程中可能会导致任务再细分为子任务，添加自身的任务队列中，然后采用LIFO策略（可变）自身线程队列中获取要执行的任务。

2. 任务窃取（FIFO）
3. 窃取失败时从pool的任务队列获取任务


###2. ForkJoinWorkerThread

1）开始执行任务  
**code-2-1**

    /**
     * Runs the given task, plus any local tasks until queue is empty
     */
    final void execTask(ForkJoinTask<?> t) {
        currentSteal = t;
        for (;;) {
            if (t != null)
                t.doExec();
            if (queueTop == queueBase)
                break;
            t = locallyFifo ? locallyDeqTask() : popTask();
        }
        ++stealCount;
        currentSteal = null;
    }  

<font color='FF0000' size='3'>**问题:**</font>  
为什么修改stealCount?  
由于此方法的触发点时pool.scan()方法，当窃取到任务的时候才会执行，所以此处是统计窃取任务次数的最佳时刻。
窃取的任务及分解的子任务执行完成后，stealCount加1.

2）执行任务的过程中，可能会调用fork、join操作，fork操作会将子任务添加到当前线程的任务队列中。从代码中可知，每添加一个子任务，就会调用pool.signalWork()操作，激活那些处于等待状态的线程，或者创建新线程。  
**code-2-2**  

    /**
     * Pushes a task. Call only from this thread.
     *
     * @param t the task. Caller must ensure non-null.
     */
    final void pushTask(ForkJoinTask<?> t) {
        ForkJoinTask<?>[] q; int s, m;
        if ((q = queue) != null) {    // ignore if queue removed
            long u = (((s = queueTop) & (m = q.length - 1)) << ASHIFT) + ABASE;
            UNSAFE.putOrderedObject(q, u, t);
            queueTop = s + 1;         // or use putOrderedInt
            if ((s -= queueBase) <= 2)
                pool.signalWork();
            else if (s == m)
                growQueue();
        }
    }


