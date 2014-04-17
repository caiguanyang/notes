<meta http-equiv="content-type" content="text/html; charset=UTF-8">
shell基本语法
----------------
##1 变量
1)赋值时变量名和值之间的“=”两边不能出现空格，且如果值内含空格，需加引号  
test="a b c"  
var2=a  
2）将多个变量连接起来时需要使用引号  
var="$test $var2"  
3）readonly export  

4)

##2 函数


##3 分支、循环


##4 辅助命令
###4.1 read
1)读标准输入到变量name  
  read name  
2）显示提示  
  read -p "tip info" name  
3)计时输入  
  read -t 4 -p "tip" name  
  等待4秒若用户没有输入，则返回非零值退出  
4）默读（不显示输入，如密码）
  read -s passwd  
5)指定读取字符数  
  read -n1 -p "Do you want to continue [Y|N]?" state  
6)读文件  
  cat file | read line  
  一次读取输入中的一行  

**参考：**http://www.cnblogs.com/iloveyoucc/archive/2012/04/16/2451328.html

###4.2 test



##5 附
1）使用分号（;）分隔同一行中的多个命令，如果使用的是（&),shell会在后台运行符号前面的命令，且不用等命令运行结束就可以执行下一条命令。  
2）




