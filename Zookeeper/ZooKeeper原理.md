<meta http-equiv="content-type" content="text/html; charset=UTF-8">

T：2014-07-29  
 tomato: 

Zookeeper 原理...
-----------------------------
###1. 会话管理
1. 什么情况下会话会超时？  

2. 由于某种原因Session超时，那么重建连接后，新会话会保存原来的信息吗？


###2. ID管理 
1. 创建节点时添加的序列后缀生成方式：
   如：zk.create(path, data, acl, CreateMode.PERSISTENT_SEQUENTIAL)  

2. SessionID


###3. Watcher机制



###4. 异步API的实现

