<meta http-equiv="content-type" content="text/html; charset=UTF-8">

T: 2014-05-07

shell实例--shift
-------------------------------
###1. 问题
写一个函数，利用shift计算所有参数乘积，假设参数均为整数

###2. 解法

    #! /bin/bash
    multiple () {
        result=1;
        echo "参数有：" $*;
        until [ $# -eq 0 ]
        do
            result=`expr $result \* $1`;
            shift;
        done
        return $result;
    }

    multiple 1 2 3 4 5 6
    # 函数的返回状态即为结果
    echo $?

###3. 知识点
####3.1 函数
**语法**  

    funcname () {
        ....
    }
    #调用
    funcname [parameters...]

函数在使用之前必须先定义  
在函数体中使用位置参数（$1, $2..., ${10},..$@,$#,$* )来访问函数参数，其中$0标示父脚本的名字  
函数通过return返回一个退出值给调用者，与exit相似，但是函数中使用exit会退出整个shell脚本 

**调用其他文件中的函数**  
> . funcfile.sh     加载定义函数的文件，这样在shell中我们可以直接调用funcfile.sh中定义的函数  

其他shell文件中叶可以通过上述方式直接引用别的文件中定义的函数，如：

    #! /bin/bash
    #m_test.sh

    . multiple.sh

    multiple 1 2 3
    echo $?

    #输出为： 6

通过`set | grep "multiple"` 我们可以查看已加载的函数


####3.2 shift
每运行一次shift命令，则销毁一个参数，后面的参数前移，如上面的例子。Bash定义了9个位置参数，但是并不意味着我们只能访问9个参数，借助shift命令，我们可以访问多个参数。

####3.4 参数取值
1）替换字符  
**${varname:-word}**  变量未定义，返回默认值word  
**${varname:=word}**  变量未定义，设置默认值word  
**${varname:?message}**  变量未定义，显示消息message  
**${varname:+word}**  变量存在且非null,返回word，否则返回null，用于测试变量的存在性

2）位置参数说明  
$1, $2 .. ${10}...  
$#  参数的个数  
$*  $@   所有参数信息   
" $* " 将所有参数作为一个字符串
" $@ " 将每个参数作为单独的字符串，它可以很好的保留参数内的空白。  
    
    set -- cai "guan yang"
    for i in $*
    do
        echo $i
    done 

    ##输出为
        cai
        guan
        yang
    
    for i in "$@"
    do 
        echo $i
    done

    ##输出为：
        cai
        guan yang

