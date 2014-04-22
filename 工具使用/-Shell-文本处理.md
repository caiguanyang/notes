<meta http-equiv="content-type" content="text/html; charset=UTF-8">
Shell脚本指南--文件处理
-------------
##1 查找与替换
###1.1 正则表达式
POSIX标准中分两种正则表达式：
**BRE(basic regular expression)：**
**ERE (extended regular expression):**
他们支持的符合有所区别：

>>  .  &emsp;&emsp; Both  &emsp;匹配任何单个字符
>>  *  &emsp;&emsp; Both  &emsp;匹配在它之前的任何数目单个字符
>>   \  &emsp;&emsp; Both  &emsp;转义字符
   ^  &emsp;&emsp; Both  &emsp;从字符串的行首开始匹配
   $  &emsp;&emsp; Both  &emsp;从字符串的行尾开始匹配前面的字符串
   [...]  &emsp;&emsp; Both  &emsp;匹配方括号内的任一字符
   +  &emsp;&emsp; ERE  &emsp;匹配前面正则表达式一个或多个实例
   ?  &emsp;&emsp; ERE  &emsp;匹配前面正则表达式零个或一个实例
   |  &emsp;&emsp; ERE  &emsp;匹配与|符号前或后的正则表达式
   {m,n}&emsp;&emsp; ERE  &emsp;匹配前面单个字符的重复出现区间次数
    
POSIX标准中还提供了一些字符集，排序符号和等价字符集。

**后向引用**

###1.2 主要命令 
 **grep**  &emsp;查找命令
>   `grep [options ...] pattern-spec [ files ...]`  
>    *option:*  
>    -E  使用扩展正则表达式  
>    -F  使用固定字符串进行匹配  
>    -i  模式匹配时忽略字母大小写差异

**sed** 
>   `sed [-nif] 'command' inputtxt`  
>   -i  表示将修改直接应用到文件中  
>   -f  从文件中读取sed命令  
>   -e  直接在命令行模式下编辑sed指令，也即可以在命令行下执行多条指令，  
>   &emsp;&emsp;如sed -e 'command' -e 'command' ... files  
>   
>   command:  
>   a:添加  
>   c:取代  后跟字符串，取代n1,n2行之间的内容--sed '1,3c hi'  
>   d:删除  
>   i:插入  
>   s:取代，可以使用正则表达式，这个是常用的用法  
>   &emsp;&emsp;sed 's/要替换的字符/替换的字符/g'  g标示要替换文本中所有要替换的字符  


**注意**  
1)sed中的分隔符为/，如果文本内容中也出现/，则可以使用其他分隔符，如  
sed 's+/test+ab+' 此命令中使用的是+作为分隔符，替换/test为ab  
2)更改不会直接作用于输入文件，除非使用-i选项。  


示例：  
sed 's/^192.168.0.1/&localhost/'  &符号表示找到的要替换的字符串；  

参考：http://www.cnblogs.com/dong008259/archive/2011/12/07/2279897.html

##2 文本处理常用命令
###2.1 cut  
主要用于剪下文本文件中的数据，可以分字段/字符  
**cut -c list [file...]**  
以字符模式剪辑文本，list为字符编号，从1开始  
**cut -f list [-d delim] [file ...]**  
分段模式剪辑文本，默认的定界符为tab, 也可以通过选项-d 来指定。  

###2.2 join

###2.3 find

###2.4 sort

###2.5 uniq

###2.6 wc

###2.7 printf

###2.8 head
显示文件的某一部分  
head -n 10 file   显示文件的前10行  
head -c 20 file   显示文件前的20字节内容


##3 读文件

**问题：** <br>
(1)大文件是如何读取的？如4G  


##4 其他
###4.1 /dev/null


###4.2 文本分隔符
$IFS  设定输入文本的字段分隔符  
awk中可以设置变量OFS的值，指定输出的多个字段之间的分隔符



> Written with [StackEdit](https://stackedit.io/).