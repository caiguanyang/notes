JS基础
---------------------
###1. 数据类型
####1.1 Boolean
var b = new Boolean(false);  此时b的逻辑量为true;  
var b = new Boolean(false).valueOf(); b的逻辑量为false;  
var b = Boolean(false);   b的逻辑原始值为false;  
var b = Boolean('false')  b的逻辑原始值为true;  
java调用JS脚本是，最好直接用字符串来处理逻辑判断，免得出什么问题。（曾出现错误，可能是java的对象和JS对象映射时出现错误）

####1.2 String
JS中提供了length属性来获得字符串的长度，但Java中的String提供的为length()函数，使用过程中要注意，另外由于引擎的不稳定性，导致可能调用length()时出错，所以最好不要使用JS变量中转Java对象：  
如  

    var test=javamethod.returnStr();  
    var nlen = test.length;   // nlen是一个对象
    // 由于test仍为一个java对象，需调用length()方法
    
    var nlen = test.length();  // 返回长度（可能出错）
    var nlen = javamethod.returnStr().length();  //推荐使用



