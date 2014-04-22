<meta http-equiv="content-type" content="text/html; charset=UTF-8">
Dom4j解析XML
----------------------------
###1. 基本用法
&emsp;参考[dom4j使用][1].
###2. 解析方式
####2.1 DOM
 >  DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = fact.newDocumentBuilder();
    Document doc_xml = builder.parse(new File("test.xml"));
    DOMReader reader_d = new DOMReader(`new DOMDocumentFactory()`);
    org.dom4j.dom.DOMDocument doc = (DOMDocument) reader_d.read(doc_xml);

####2.2 SAX
> SAXReader reader = new SAXReader();
  org.dom4j.Document doc = reader.read(new File("test.xml"));
  
&emsp;&emsp;SAX解析器不像DOM那样建立一个完整的文档树，而是在读取文档时激活一系列事件，这些事件被推给事件处理器，然后由事件处理器提供对文档内容的访问。那些只需要单遍读取内容的应用程序可以从SAX解析中大大受益。而如果要修改和操作XML，则很少使用SAX。
**注意**
SAXReade r sr = new SAXReader();
sr.setDocumentFactory(new DOMDocumentFactory());
try {
DOMDocument doc = (DOMDocument) sr.read(xmlstring); 
}
通过SAXReader也可以创建出DOMDocument，从上面的代码我们可以看出创建dom4j文档树跟采用的DocumentFactory有关

###3. DocumentFactory
源码中的介绍：
```
/*   
* <code>DocumentFactory</code> is a collection of factory methods to allow
* easy custom building of DOM4J trees. The default tree that is built uses a
* doubly linked tree.
* The tree built allows full XPath expressions from anywhere on the tree.
*/
```
&emsp;&emsp;当使用的是DOMDocumentFactory时，创建的节点是DOMElement实例，使用默认的DocumentFactory时，则创建的Dom4j节点树节点都是AbstractElement实例。从Dom4j API中知DOMElement是 org.dom4j.tree.AbstractElement的子类，但是当创建SAXReader或者DOMReader不指定DocumentFactory时，不能将Document转换为DOMDocument.

由于采用的DocumentFactory不同，将直接影响XML元素在内存中的存储结构，导致检索节点是性能有所差异，可以参看http://www.iteye.com/topic/1117606做的测试。

Dom4j中支持的DocumentFactory类型有：
![类图][2]

> Written with [StackEdit](https://stackedit.io/).


  [1]: http://87029274.iteye.com/blog/1183454
  [2]: http://d.pcs.baidu.com/thumbnail/a343b822ffff06f1b2f5b1e62b400853?fid=1796184830-250528-112407723986274&time=1395834431&rt=pr&sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-HOh72GJnAQqdk%2bYBwtacFX1Xq5I=&expires=8h&prisign=RK9dhfZlTqV5TuwkO5ihMd5RMl20gNxXUiEpHysoGObDdOsz01yQszraTroqGzzhWrBLCU/QJ7hEXM0pnG7XVmHbh9zGO8SQFyU7wWgSpO7oYkwdXfci8C5VHrJoODzY3jZt2m8Eff6zfFgeX56amqv4j1gayjms5pfy3XbwwwsuVR6yaDg82FxWN2Rc/6zVpuni65KOFcKwg54nfuMzCq35YclbRjxpaxj0NNGWNJYtUuCKorR3NH5uQdH5qPqjb1RB%20f%20SyT2CSsDq5LTecmiY8zBQMRNrCSle4vOI5hAN8HxOtJBtNYshL%20A9fxSRyMy/FL0pZxgRDzo2H0k8KYyPKN93h16w%20io4gRRLnAk=&r=608709378&size=c850_u580&quality=100