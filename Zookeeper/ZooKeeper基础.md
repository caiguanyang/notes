<meta http-equiv="content-type" content="text/html; charset=UTF-8">

T：2014-07-25  
 tomato: 8+4

Zookeeper 基础...
----------------------------
ZooKeeper is a service for coordinating processes of distributed applications.

![结构][1] 

1. Zookeeper 服务中维护的节点树保存在内存中，这也因此限制了每个znode可以绑定的数据大小（<1M）.
2. client与keeper server建立TCP连接，来发送请求，接收响应，获取监听事件，发送心跳包。且server会为每个连接建立一个session。TCP连接由于某种原因断开后,Client则与其他server建立连接，且需重新建立session.  
3. Zookeeper提供的一致性：  


4. 每一个更新操作都会绑定一个唯一编号（zxid）来标示顺序。
  （要了解Zookeeper的读、写原理，才能明白顺序的重要性，以及对一致性造成的影响）
5. 监听器：为什么监听器只能使用一次，按观察者设计模式，我们其实可以做到重复使用的啊。

6. Leader是如何选取的

------------------
###关键概念：
**1）Time in ZooKeeper:**  
  Zxid:  
  Version numbers:  
  Ticks:  
  Real time:  

**2) Session：**  
状态有：CONNECTING --> CONNECTED  -->  CLOSE  
![状态图][3]  
(图--session状态转换图)  

客户端会循环遍历Zookeeper server列表，直到创建一个连接为止。  

**3）Watch:**  
Zookeeper提供了监听机制，客户端可以监听节点的状态信息，主要特征有：  
a. 触发一次：在节点上设置的监听事件一次有效，当触发后如果用户想再次监听，需要重新设置监听器；  
b. 监听的事件采用异步方式发送给监听者：这样可能出现Client已经收到了更改节点值的结果，但还没有收到节点值被修改的监听事件。由于网络延迟或者其他因素可能导致各个客户端收到信息的时间不一致，但是Zookeeper保证如下一致性：客户端不会看到他监听的节点修改后的值，除非他已经收到了关于此节点的监听事件。server端发出的监听事件会与数据更改顺序一致的。  
c. 监听事件分两类，节点值相关和子节点相关：Zookeeper会分别处理这两类事件，如getData(), exists()注册对值的监听事件，getChildren()注册对子节点的监听事件，但create(),delete()则会同时触发节点值和子节点相关监听事件。

**注意：**  
由于监听一次有效，且获取监听事件信息和再次向节点注册监听是有延迟的，这期间节点的值可能已经被别的节点修改了，Zookeeper不能保证客户端能获得每次的修改之。 在编写多线程代码和锁时要特别注意。 [http://agapple.iteye.com/blog/1184023](http://agapple.iteye.com/blog/1184023 "项目中应用zookeeper的心得") 

**4）ACL:**  
Zookeeper通过ACL控制节点的访问权限。与Unix中文件系统的访问权限相似，但是没有User, Group, Other的概念。且对节点设置的权限，不会应用到子节点上，每个节点的访问权限都是独立的，不具有递归性。  
提供的权限有：  
a. Read:  读节点值，查看子节点列表  
b. Write: 设置节点值  
c. Create: 添加子节点  
d. Delete: 删除子节点  
e. Admin: 可以修改节点的权限  

**5）一致性：**  
a. 更新顺序一致性（单调写）：server端对节点的更新顺序与客户端发起更新操作的顺序一致（TCP连接保证数据包的顺序）；   
b. 原子性：更新操作要么成功，要么失败；  
c. 单一系统视图（单调读的一种情况吧）：对同一个客户端，无论它当前连接的是哪一个Zookeeper中的服务节点，看到的都是同一个节点树状态（像版本控制似的）；  
d. 可靠性：当应用一个更新时，它会将更改持久化到一个节点中。客户端只能通过返回的操作结果码来确定时更新是否成功，且已经成功执行的更新是不可以回滚的。  
e. Timelines: 客户端看到的系统视图在一段时间内是最新的（如从当前算起20秒内某个一致状态），客户端在20s内可能会看到系统的最新状态，也可能会自己检测到视图有点过时，主动更新。  
**注意：**  
Zookeeper不保证同一时间两个客户端看到的Zookeeper data视图是一致的。

-------------------------------------
###应用相关： 
Zookeeper通过Watcher来跟创建zk实例的对象交互；
监听事件可以实现为“链式监听”，即只监听可能与他直接相关的一个节点，来避免羊群效应，使节点多次从等待状态中唤醒，提高性能。

###应用：
####lock:  
 分布式锁：
    
    public void lock() {
        lpath = zk.create("/lock/lock-", CreateMode.EPHEMERAL_SEQUENTIAL);
        while(true) {
            children = zk.getChildren("/lock", false);
            if (lpath.seq is the lowest in children)
                return; // get lock
            if (zk.exists("/lock/lock-before(lpath.id)", true))
                wait();   // 监听第二个小的节点
        }
    }

    测试结果：
    Tue Jul 29 16:41:44 CST 2014 : app-1 initial...
    lockpath：/lock/clock-0000000005  before:/lock/clock-0000000005
    Tue Jul 29 16:41:44 CST 2014 : app-1 start working...
    Tue Jul 29 16:41:50 CST 2014 : app-1 end...    // unlock

    Tue Jul 29 16:41:47 CST 2014 : app-2 initial...
    lockpath：/lock/clock-0000000006  before:/lock/clock-0000000005
    lockpath：/lock/clock-0000000006  before:/lock/clock-0000000006
    Tue Jul 29 16:41:50 CST 2014 : app-2 start working...   // lock
    Tue Jul 29 16:41:53 CST 2014 : app-2 end...

*说明*  
    从结果可以看出应用程序app-2在app-1释放锁后，他才获取锁。
**注意点**  
1）创建的节点时要设置ephemeral和sequential标识，依据ZKserver维护的自增序列号来实现Fair锁，总是先让序号最小的节点获取锁；  
2）如果当前节点不能获得锁，则监听紧邻且序号比他小的节点，避免“羊群效应”；  

参考：http://agapple.iteye.com/blog/1184040
源码：

####两阶段提交
问题：对官方文档中描述的实现方式没有理解呢
http://zookeeper.apache.org/doc/current/recipes.html#sc_recipes_Queues


####领导者选举
与分布式锁的实现相似，都是将序号最小的那个节点选为领导者；非领导节点监听紧邻节点的状态信息；


----------------------------

[1]: https://cwiki.apache.org/confluence/download/attachments/24193436/service.png?version=1&modificationDate=1295027310000&api=v2
[2]: http://d.pcs.baidu.com/thumbnail/c2ad6696c816b70f29874fbf9e7f8950?fid=1796184830-250528-420876292609791&time=1406347200&sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-ULoz%2BT0NLTm8j6PyOdUikQk7P3o%3D&rt=sh&expires=2h&r=781321705&sharesign=unknown&size=c710_u500&quality=100
[3]: http://zookeeper.apache.org/doc/current/images/state_dia.jpg


