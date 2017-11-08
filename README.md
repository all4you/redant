# 欢迎使用RedAnt


**RedAnt**是一款基于netty、支持http协议的web框架。拥有如下特点：
 
- **对象管理** ：通过Bean注解，实现对象的全局、统一管理；
- **自动注入** ：通过Autowired注解，实现Bean对象的自动注入；
- **自由路由** ：使用RouterController、RouterMapping、RouterParam注解实现路由的自定义；
- **参数转换** ：通过TypeConverter接口，实现http请求的参数转换（目前支持：基础类型、Map、List、JavaBean）；
- **Session** ：实现了自定义的Session管理，一个session就是一个ChannelHandlerContext；
- **Cookie** ：实现了自定义的Cookie管理，cookie需要在writeResponse之前写入response的header中；
- **结果渲染** ：通过自定义的Render接口，对返回结果进行渲染，目前支持渲染html、xml、plain、json等数据；
- **数据助手** ：内置Mybatissist持久层CRUD通用方法操作助手，使用PageHelper插件处理分页。

-------------------

## 如何运行

> 该web框架是基于netty，内部使用TCP协议，向上支持了http协议，不需要使用tomcat或者weblogic等中间件，直接使用Java运行即可，服务端的入口是：com.redant.main.ServerBootstrap。
> >1：可以将代码下载后导入IDEA或者eclipse，然后通过IDE工具运行。
> >
> >2：可以将代码下载后用maven打成jar包，使用java命令运行。

> 启动后直接在浏览器中访问 http://127.0.0.1:8888 (默认端口可以在redant.properties文件中修改)，如果可以正常返回 “Welcome to redant!”即说明项目启动成功。目前项目中内置了四个Router，服务器启动时会将所有的Router打印出来：

> GET  /                               HTML
>
> GET  /UserController/getUserCount    JSON
>
> GET  /UserController/getUserList     JSON
>
> GET  /UserController/getUserInfo     JSON
>
> \*    \*                             HTML



## 对象管理

> 对象可以通过Bean注解进行管理，并可以通过Autowired注解实现对象的自动注入，避免了重复创建对象的烦恼，使用上和Spring保持一致，学习成本非常低。
> 
> **提示：**想了解更多，请查看**wiki文档：**[Bean][1]



## 路由管理

> 使用RouterController来定义一个路由Controller类，RouterMapping用以指定Controller中每个具体的方法，RouterController+RouterMapping唯一匹配一个http请求的路由，RouterParam用以标识方法的参数，用以实现http请求参数的转换，基础类型的参数必须使用RouterParam注解进行标识，POJO对象可以不使用RouterParam标识。
> 
> **提示：**想了解更多，请查看**wiki文档：**[Router][2]



## 参数转换

> 通过TypeConverter接口，实现http请求的参数转换，目前支持将parameter参数转换为：基础类型、Map、List、JavaBean。



## Session管理

> 实现了自定义的Session管理，Session是基于Netty的ChannelHandlerContext（以下简称context）实现的，使用context中通道的channelId作为sessionId。每个session使用一个map来存储需要保存的属性值。
> 
> **提示：**想了解更多，请查看**wiki文档：**[Session][3]



## Cookie管理

> 实现了自定义的Cookie管理，需要注意的是`cookie需要在writeResponse之前写入response的header中`。
> 
> **提示：**想了解更多，请查看**wiki文档：**[Cookie][4]




## Mybatissist--通用CRUD工具

> Mybatissist 是一个基于Mybatis注解的通用CRUD工具，使用Mybatissist可以仅仅关注数据库表和实体类的映射，而不必关系具体的操作过程，也不需要编写额外的Mapper.xml来指定SQL语句，只需要定义好实体类和xxxMapper接口，且保证该xxxMapper接口继承自通用的Mapper接口，既能使用通用接口中的所有CRUD方法。
> 
> **提示：**想了解更多，请查看**wiki文档：**[Mybatissist][5]





[1]: https://github.com/all4you/redant/wiki/1:Bean?_blank {:target="_blank"}
[2]: https://github.com/all4you/redant/wiki/2:Router?_blank {:target="_blank"}
[3]: https://github.com/all4you/redant/wiki/3:Session?_blank {:target="_blank"}
[4]: https://github.com/all4you/redant/wiki/4:Cookie?_blank {:target="_blank"}
[5]: https://github.com/all4you/redant/wiki/5:Mybatissist {:target="_blank"}
