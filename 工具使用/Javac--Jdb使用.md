<meta http-equiv="content-type" content="text/html; charset=UTF-8">
Java编译调试工具
-----------------------
----
###javac
&emsp; 编译java代码
> javac [option] filename
> &emsp;&emsp; -d directory: 将生成的.class文件放入包目录内，如果包目录不存在，会自动创建
> &emsp;&emsp; -cp  dir: 编译时查看指定目录内的类文件

###java
&emsp; 运行java代码
> java [option] classfile
> &emsp;&emsp; -cp dir: 从指定目录中找class文件，如果类到包时，需要指定包路径。
> &emsp;&emsp;&emsp;&emsp;如&emsp; `java -cp bin/classes com.test.Test`


###jdb
&emsp; 调试java代码

> Written with [StackEdit](https://stackedit.io/).