<meta http-equiv="content-type" content="text/html; charset=UTF-8">
shell积累
-----------------------
##1 杂项
###1.1 文件描述法、重定向
0 1 2分别代表标准输入，输出，错误输出，我们可以重定向他们的位置
exec 1> test.txt  将shell的标准输出重定向到test.txt文件  
如果需要取消标准输出的重定向，一般是这样操作：  
exec 5> &1  将标准输出文件保存到文件描述符5中  
exec 1> test.txt  
exec 1>&5  
exec 5>&-    关闭文件描述符5  
