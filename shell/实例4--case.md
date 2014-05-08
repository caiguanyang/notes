<meta http-equiv="content-type" content="text/html; charset=UTF-8">
T: 2014-05-08

shell实例--case
------------------------
###1. 问题
写一个脚本，可以根据参数文件名，以正确的参数调用tar来解压缩tar.gz或tar.bz2文件

###2. 解法

    #! /bin/bash
    case $1 in
    *.tar.gz)
        tar -zxvf $1;
        ;;
    *.tar.bz2)
        tar -jxvf $1;
        ;;
    *)
        echo "未找到合适的方法";
    esac


###3. 知识点
####3.1 case分支

    # case语句
    case var in
    -f)
        ...
        ;;
    -d || --directory)
        ...
        ;;
    *)
        #最后一个情况
        ...
    esac

####3.2 tar命令
命令的用法：  
    
    tar -c: 建立压缩文档  
        -x: 解压
        -t: 查看内容
        -r: 想压缩文档末尾追加文件
        -u: 更新原压缩包中的文件
    这5个是独立的命令，只能使用其中的一个  
    -z: 有gzip属性
    -j: 有bz2属性
    -v: 显示所有过程
    -f: 使用压缩包的名字，这个只能是最后一个参数 
   
    对于*.gz和 *.bz2文件，有专门的压缩工具来处理


参考：http://www.cnblogs.com/eoiioe/archive/2008/09/20/1294681.html  



