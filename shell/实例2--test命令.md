<meta http-equiv="content-type" content="text/html; charset=UTF-8">

T: 2014-05-06

shell实例--test
-------------------------
###1. 问题
&emsp;&emsp;写一个脚本，执行后，打印一行提示“Please input a number:"，要求用户输入数值，然后打印出该数值，然后再次要求用户输入数值。直到用户输入"end"停止

###2. 解法

    #！ /bin/bash
    unset var;
    while [ "$var" != "end" ]
    do
        read -p "Please input a number:" var;
        if [ "$var" = "end" ] 
        then 
            break;
        fi
        echo "$var";
    done

###3. 知识点
####3.1 unset 
unset [-fv] 函数/变量名称  
为shell内建指令，可以删除变量或函数

####3.2 read
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

####3.3 test
test指令的另一种形式是：`[ ... ]`  
它主要用于测试文件的属性，比较字符串和数字  
如：  

    -d  file   file是目录
    -e  file   file是否存在
    s1 = s2   字符串相同
    s1 != s2  字符串不同
    n1 -eq n2 整数n1和整数n2相等
    n1 -ne n2 不同
    n1 -lt n2 小于
    n1 -gt n2 大于
    n1 -le n2 小于等于
    n1 -ge n2 大于等于

注意使用[]时，操作数、操作符和两边的方括号要有空白分开

####3.4 if分支

    if condition
    then
        ...
    elif condition
    then
        ...
    else
        ...
    fi
    
**注意**  
1）‘循环’参考实例1--阶乘  
2）