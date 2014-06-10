<meta http-equiv="content-type" content="text/html; charset=UTF-8">
ThreadLocal原理介绍
--------------------
[TOC]

##1. 功能介绍
&emsp;&emsp;ThreadLocal变量指线程本地变量，通过get和set来获取和设定；每个线程在使用时都会创建一个Map来保存本地变量值的引用，访问时从当前线程独有的Map中查找此变量的值。当然如果为ThreadLocal变量设定的为一个共享对象(如一个静态的Vector)，那么多个线程间也将出现争夺资源访问的情况。

##2. 原理说明
&emsp;&emsp;为了达到使变量局限到线程内部，我们可以为每个线程创建一个独有的Map，保存此线程所有的本地线程变量；如果采用Map，那么使用什么值作为Key呢？我们看如下几个关键类的实现：

###ThreadLocal
此类中定义了变量threadLocalHashCode作为它在Map中的唯一标识：<br>
`private final int threadLocalHashCode = nextHashCode();`<br>
为了保证同线程中多个实例的标识不一致，采用静态方法nextHashCode生成不同的值。由于一个线程中多个ThreadLocal实例是放在一个Map中的，为了使ThreadLocal变量能均匀的分部在Map中，通过跳跃式的方式生成HashCode；增量则采用了<!--- 0x61c88647 --->(黄金分割数)。
>**问题**：为什么非要是它，通过测试，生成的每个值间隔为7，那么我直接指定增量为7可以吗？

    /** The difference between successively generated hash codes - turns
     * implicit sequential thread-local IDs into near-optimally spread
     * multiplicative hash values for power-of-two-sized tables.
     */
    private static final int HASH_INCREMENT = 0x61c88647;

    /**
     * Returns the next hash code.
     */
    private static int nextHashCode() {
	    return nextHashCode.getAndAdd(HASH_INCREMENT); 
    }
说明：生成的hashcode只需要保证在当前线程中唯一即可，因此计算时不需要进行同步
###ThreadLocalMap
此类具有包访问权限，是专为ThreadLocal创建的。
此哈希表处理冲突的方式是**线性探测法**;
问题的关键是如何根据ThreadLocal中的threadLocalHashCode获取变量的值呢？也即如何根据threadLocalHashCode生成HashTabel中的索引。

	private Entry getEntry(ThreadLocal key) {
        int i = key.threadLocalHashCode & (table.length - 1);
        Entry e = table[i];
        if (e != null && e.get() == key)
            return e;
        else
           return getEntryAfterMiss(key, i, e);
    }

从源码可以看出它根据哈希表的大小（2^N）来取threadLocalHashCode的低N位作为哈希表的索引的。这也要求哈希表的初始大小必须为2的N次方才行。

从set()方法的源码我们可以看出它是如何处理冲突的：

	    private void set(ThreadLocal key, Object value) {
            // We don't use a fast path as with get() because it is at
            // least as common to use set() to create new entries as
            // it is to replace existing ones, in which case, a fast
            // path would fail more often than not.

            Entry[] tab = table;
            int len = tab.length;
            int i = key.threadLocalHashCode & (len-1);

            for (Entry e = tab[i]; e != null; e = tab[i = nextIndex(i, len)]) {
                ThreadLocal k = e.get();

                if (k == key) {
                    e.value = value;
                    return;
                }

                if (k == null) {
                    replaceStaleEntry(key, value, i);
                    return;
                }
            }

            tab[i] = new Entry(key, value);
            int sz = ++size;
            if (!cleanSomeSlots(i, sz) && sz >= threshold)
                rehash();
        }

###Thread
有一个数据成员threadLocals保存了此线程中所有的ThreadLocal变量。但是此变量没有随Thread的初始化而初始化，当定义ThreadLocal变量时它会检测当前线程的Map是否为null，如果为null，则完成Map的初始化工作，并将自身保存到线程的Map中。
说明：独有的Map，保证访问变量时不需要与别的线程争用资源，加快访问速度；

###Entry
此类继承了**WeakReference**类，那么当检测到ThreadLocal变量不用时，JVM会及时回收此实例。
java中提出的弱引用，当第一次执行垃圾回收时，JVM就将不用的弱引用实例回收掉，释放他们所占的内存。

![类图][1]
##3. 用法
引用别人的总结：<br>
ThreadLocal不是用来解决对象共享访问问题的，而主要是提供了保持对象的方法和避免参数传递的方便的对象访问方式。归纳了两点： <br>
1。每个线程中都有一个自己的ThreadLocalMap类对象，可以将线程自己的对象保持到其中，各管各的，线程可以正确的访问到自己的对象。 <br>
2。将一个共用的ThreadLocal**静态实例**作为key，将不同对象的引用保存到不同线程的ThreadLocalMap中，然后在线程执行的各处通过这个静态ThreadLocal实例的get()方法取得自己线程保存的那个对象，避免了将这个对象作为参数传递的麻烦。 

*Java并发编程中描述：*<br>
&emsp;&emsp;ThreadLocal通常用于防止对可变的单实例变量或全局变量的共享；如单线程中我们通过维护一个全局的数据库连接对象来避免在多个方法中传递它，如果移植到多线程程序中，那么可以将此全局变量转换为一个静态的ThreadLocal变量来维护线程安全性，这样每个线程都会创建自己的数据库连接，通过这个静态符号就能访问，避免参数传递。<br>
&emsp;&emsp;但是ThreadLocal变量就想是全局变量，他将降低代码的可重用行，并在类之间引入隐含的耦合性。
![实例代码][2]
**实例代码**

	private static ThreadLocal<Integer> number = new ThreadLocal<Integer>(){
    	@Override
    	protected Integer initialValue() {
      		return 1;
    	}
  	};
  
  	private static void addNum() {
    	int a=number.get().intValue();
    	a++;
    	number.set(a);
  	}
 	public static void main(String[] args) {
    
	    Runnable t = new Runnable() {
	      @Override
	      public void run() {
	        // TODO Auto-generated method stub
	        for (int i=0; i<1000; i++) {
	          addNum();
	        }
	        System.out.println(number.get());
	      }
	    };
	    for (int k=0; k<10; k++) {
	      Thread thread = new Thread(t);
	      thread.start();
	      try {
	        Thread.currentThread().sleep(100);
	      } catch (InterruptedException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	      }
	    }
	}
输出结果为：
1001
1001
1001
1001
1001
1001
1001
1001
1001
1001

从结果我们可以看出，虽然定义的为一个静态变量number，但是每个线程访问的还是自己线程内部保存的number值，所以执行多次后，各个线程返回的number值都为1001，不存在多个线程对静态变量number的争用。

##参考文献
1. http://www.iteye.com/topic/103804
2. http://jerrypeng.me/category/tech/java/

[1]: http://d.pcs.baidu.com/thumbnail/ecd50ced4a85b8a6a70699864052081b?fid=1796184830-250528-718778888237345&time=1397006664&rt=pr&sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-HMKp5HEidkmKI91Fw7FVvDCv5b8%3D&expires=8h&prisign=RK9dhfZlTqV5TuwkO5ihMWbo+3WoNzZ8UiEpHysoGOZoiSpQQ120oC8YcXBYRQj53lHk5sJixd1+XDsEuZpjhO2nWnxzosPlyZRa306vBW3xisYh+duuFB32F+3CHRP8VcUWDxuyGTDEYDyJmYKFsprDuQdOp7Ohs9WGVHbM3v9Nn/FIe6YItZcmJusFmHJ7vZiP2DKAzsUINN6/mK0bei3svHuIb6ytq7Vtdmnfexo8fpvaWeOfHQ==&r=725698076&size=c850_u580&quality=100
[2]: http://d.pcs.baidu.com/thumbnail/6fdbf8b97f6c2930300d80af553f31f6?fid=1796184830-250528-628502349757625&time=1397092347&rt=pr&sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-ys5lWGPyZNg5UhkoKgq37Xi%2BIOw%3D&expires=8h&prisign=RK9dhfZlTqV5TuwkO5ihMWbo+3WoNzZ8UiEpHysoGOZoiSpQQ120oC8YcXBYRQj53lHk5sJixd1+XDsEuZpjhO2nWnxzosPlyZRa306vBW3xisYh+duuFB32F+3CHRP8VcUWDxuyGTDEYDyJmYKFsprDuQdOp7Ohs9WGVHbM3v9Nn/FIe6YItZcmJusFmHJ7vZiP2DKAzsUINN6/mK0bei3svHuIb6ytJ7G4wQaxETErmXMeJBz53A==&r=307908620&size=c850_u580&quality=100

> Written with [StackEdit](https://stackedit.io/).