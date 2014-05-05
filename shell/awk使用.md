<meta http-equiv="content-type" content="text/html; charset=UTF-8">

awk使用
-----------------
###1 文本操作相关示例
####1.1 取字段  
**awk '{ print $1, $2}' /etc/passwd**  
显示每一行记录中的第一个和第二个记录  
每条记录中字段数存储在内建变量NF中，如取最后一个字段可用$NF  
通过选项 *-F*设定低端的分隔符:  
awk -F: '{ print $1, $2}' /etc/passwd  
awk -F: -v 'OFS=\*' '{ print $1, $2} ' /etc/passwd  
变量OFS指定输出的字段之间的分隔符  
