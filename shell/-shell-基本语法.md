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
readonly [-afp] name   设置变量或函数为只读，然后用户就不能再修改/删除此变量  
export name  将变量name放置到环境变量中  
&emsp;&emsp;如果只想将变量针对接下来的命令有作用，以后的shell不起作用，那么可以将变量的复制放置到命令的前面：  
path=$path:/caigy awk ...  

4)展开运算符  
  param=${name:-def}  &emsp;&emsp;name为null,则返回def  
param=${name:?message} &emsp;&emsp;name非null,返回它的值，否则显示name:message;像捕获异常
param=${name:+def} &emsp;&emsp;name非null，返回def  

5)内置变量  
？：  上条指令的退出状态  
$：   当前进程编号  
PWD：   当前工作目录  
HOME：  根目录  
PATH:   命令查找路径

6)位置参数  
${dig}：通过数字来引用参数  
$#：参数个数  
$\* / $@：一次将所有参数传递给函数或脚本的程序  
"$\*"：将所有命令作为一个字符串传递，如"$1 $2 ..."  
"$@": 将命令行参数视为单独的字符串，如"$1" "$2" 。最佳传参方式   

set -- a b c: 初始化参数列表（清空原来的参数）

##2 函数
与其他语言中的函数一样，使用之前先定义，也可以调用其他文件中定义的函数；  
使用位置参数来引用函数参数，如$1表示传递过来的第一个参数  


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

###4.3 unset
从当前shell删除变量或函数  
unset [-v] name  &emsp;&emsp;删除变量  
unset -f funname  &emsp;&emsp;删除函数

##5 附
1）使用分号（;）分隔同一行中的多个命令，如果使用的是（&),shell会在后台运行符号前面的命令，且不用等命令运行结束就可以执行下一条命令。  
2）




