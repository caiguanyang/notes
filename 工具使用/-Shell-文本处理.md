<meta http-equiv="content-type" content="text/html; charset=UTF-8">
Shell脚本指南--文件处理
-------------
##1 查找与替换
###1.1 正则表达式
POSIX标准中分两种正则表达式：
**BRE(basic regular expression)：**
**ERE (extended regular expression):**
他们支持的符合有所区别：

>  .  &emsp;&emsp; Both  &emsp;匹配任何单个字符
   *  &emsp;&emsp; Both  &emsp;匹配在它之前的任何数目单个字符
   \  &emsp;&emsp; Both  &emsp;转义字符
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
>   grep [options ...] pattern-spec [ files ...]
    *option:*
    -E  使用扩展正则表达式
    -F  使用固定字符串进行匹配
    -i  模式匹配时忽略字母大小写差异

**sed** 
>

##2 文本处理常用命令
###2.1 cut

###2.2 join

###2.3 awk

###2.4 sort

###2.5 uniq

###2.6 wc

###2.7 printf




> Written with [StackEdit](https://stackedit.io/).