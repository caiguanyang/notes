<meta http-equiv="content-type" content="text/html; charset=UTF-8">

T：2014-06-30

sturts基础
----------------------------------
###1. 总览
&emsp;&emsp;struts在三层开发模式中被视作“表示层”的解决方案，核心作用是帮助我们处理Http请求。  
&emsp;&emsp;struts的运行环境是web容器，而运行于web容器的程序都必须遵循基本的开发表中和规范：Servlet标准和JSP标准等（JSP在Web容器中最终也编译为Servlet代码）；struts通过扩展实现servlet标准来处理Http请求。  
&emsp;&emsp;开发web程序时不一定非得采用SSH/SSM框架，对应那些复杂的，可扩展的应考虑使用框架，但对于一个简单的应用程序，没必要使用此模式，可以考虑Model1，而非MVC/Model2。  
&emsp;&emsp;
