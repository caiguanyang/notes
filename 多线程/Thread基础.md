<meta http-equiv="content-type" content="text/html; charset=UTF-8">

T：2014-06-04  
tomato: 8个

Thread基础
---------------------------
###1. Thread的状态
1）新生状态（New）： 使用new关键字创建的Thread实例，它有自己的内存空间，但是并没有运行，用户可以修改它的属性。  
2）就绪状态（Runnable）:调用线程实例的start()方法启动线程，使其进入就绪状态，它没有被分配到CPU。但线程是活着的。  
3）运行状态（Running）:一旦获取CPU，就进入此状态，直到调用其他方法而终止、等待某种资源而阻塞、完成任务而死亡。  
4）阻塞状态（Blocked）：如调用join()、sleep()、wait()或资源被其他线程使用会当值当前线程阻塞。  
5）死亡状态（Dead）

###2. Thread常用方法说明
1）sleep  
它是一个静态方法，会让当前的线程暂停执行指定时间，但会受系统计时器和调度程序精度和准确性的影响。暂停时仅释放CPU资源，不释放所持的对象锁；  

2) yield  
&emsp;&emsp;也是个静态方法，暂停当前正在执行的线程对象，并执行其他线程，但只会让同等优先级的线程执行，如果没有，则它不起作用；且也不会释放锁标识。  
&emsp;&emsp;线程执行yield方法后，状态转为Runnable，再接受调度，因此如果都是相同优先级的线程在Runnable队列中，则此线程也有可能再次运行。  
&emsp;&emsp;yield()方法对应了如下操作：先检测当前是否有相同优先级的线程处于同可运行状态，如有，则把CPU的占有权交给此线程，否则，继续运行原来的线程。所以yield()方法称为“退让”，它把运行机会让给了同等优先级的其他线程。

3) join(long timeout)   
&emsp;&emsp;等待该线程终止

4) wait(long timeout)  
&emsp;&emsp; 让执行他的线程处于暂停状态；wait是Object类的方法，对此对象调用wait方法导致本线程放弃对象锁，进入等待此对象的等待锁定池，只有针对此对象发出notify方法（或notifyAll）后本线程才进入对象锁定池准备获得对象锁进入运行状态。  
&emsp;&emsp; 由于wait和notify会对对象的“锁标识”进行操作，他们一般需要在synchronized或synchronized block中进行调用。也由于他们要在synchronized块中调用，且wait还依赖与notify的唤醒，所有它要临时释放对象锁，否则notify与wait存在资源竞争，notify拿不到对象锁，永远也不会调用wait.

5) notify/notifyAll  
与wait成对使用，都是对象的方法，也需要在synchronized块中使用。但是唤醒的是哪个线程是不确定的。另外，调用notifyAll后，其他正处于wait状态的线程也需要再次获得此对象锁后才能往下执行，所以每次也只有一个线程继续往下执行。  

**问题：**  
1） sleep和wait、yield的区别？  
  *sleep与yield：*  
   a. sleep释放CPU后，低优先级的线程可能获得CPU；但yield()只是退让一下，让那些高优先级的先运行。  
   b. sleep可以指定时间，但yield不能指定。  

   *sleep与wait:*   
   a. sleep属于Threed, 而wait则属于Object；  
   b. sleep不会释放占有的锁，但wait会释放当前的对象锁。
   c. wait需要在synchronized或synchronized block中进行调用，而sleep没此限制


2） sleep与线程挂起的区别？  
   严格来说，两者是不一样的，sleep只对调用它的线程有影响，它是没办法让另一个线程进入睡眠状态的；按时一个线程可以挂起另一个线程。

3） join应用场景？  
   由于join的作用是等待调用它的线程终止，因此当需要多个线程顺序执行时可以采用此机制（一个线程需要使用另一个线程的执行结果）。更多的控制则可以使用条件变量，信号量，闭锁，栅栏等。


4） stop(), suspend(), resume()被废弃的原因？  



###3. wait-notify模式
####3.1 用法
解决的问题：


    private boolean done = true;
    public synchronized void run() {
      while(true){
        if (done) {
            wait();
        } else {
            dosomething();
            // 注意：此处不能用sleep,由于sleep不会释放this
            // 对象锁，导致setDone不能执行
            wait(100);
        }
      }
    }

    public synchronized void setDone(boolean d) {
        done = d;
        if (!done)
            notify();
    }

或者声明一个对象，使用同步快：  

    private Object doneLock = new Object()
    private boolean done = true;
    public void run() {
        synchronized(doneLock) {
            while(true){
                if (done) {
                    doneLock.wait();
                } else {
                    dosomething();
                    // 注意：此处不能用sleep,由于sleep不会释放this
                    // 对象锁，导致setDone不能执行
                    wait(100);
                }
            }
        }
    }

    public void setDone(boolean d) {
        synchronized(doneLock) {
            done = d;
            if (!done)
                doneLock.notify();
        }
    }

    public synchronized void test() {
        ....
    }

0. wait/notify一般需要和一个状态变量同时使用，由于wait/notify只是提供一个等待和唤醒的功能，而等待的具体条件和应用相关的。为了保证testing和wait()与setting和notify之间的原子性（竟态），需要使用锁机制；
1. wait/notify强制用户使用synchronized；  
2. 一般情况下wait是在一个循环中的，并且每次都要检测状态变量的值，决定是否再次进入wait操作（多线程环境下，其他线程有修改状态变量的可能）。

####3.2 问题
1. 为什么要强制使用synchronized?
   由于wait/notify机制存在竟态，我们需要使用锁机制来解决此问题。  
   wait()语意要求在执行后释放所申请的对象锁，而在接收到通知时需要再次获得此对象锁才行，由于java中没有API支持这种先释放而后某个时刻再次获得此锁，它是和synchronized关键字整合在一块的。这也决定了我们是不能够重写wait()函数的。  
   <font color=ff0000> 编码测试当使用Lock时会有什么效果 </font>

2. 使用循环，每次都检测状态值的原因？  
   wait()操作在释放锁和再次获得锁之间不能保证在进入wait()时有效的值，在返回wait()后依然有效。多个线程处于一个对象的等待池时，当一个线程调用notify后，首先收到此消息的线程是不确定的，因此wait()线程不能确定在它收到notification之前没有其他线程已经返回到wait()了。
 
3. wait()如何下响应线程中断？

  
###参考资料
[1]. Java Threads(3ed)
[2]. http://docs.oracle.com/javase/1.5.0/docs/guide/misc/threadPrimitiveDeprecation.html

