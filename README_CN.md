# RedAnt 项目


**RedAnt** 是一个基于 Netty 的轻量级 Web 容器

 **特性:**
 
- **对象管理** : 像 Spring 一样管理所有的对象，通过加 @Bean 注解
- **对象注入** : 像 Spring 一样自动注入对象，通过加 @Autowired 注解
- **自定义路由**  : 通过 @RouterController @@RouterMapping @RouterParam 注解可以自定义路由
- **参数转换**  : 通过 TypeConverter 接口， http 参数会被转成对象(基本参数,Map,List,JavaBean)
- **Session管理r**  : 内置一个 Session 管理器,通过 Netty 的 ChannelHandlerContext 作为一个Session
- **Cookie管理**  : 内置一个 Cookie 管理器, **用户需要在返回 Render 之前处理 Cookies**
- **结果渲染**  : 内置一个 Render 接口，支持 html,xml,plain,json
- **单机模式**  : 支持单机模式
- **多节点模式**  : 支持多节点模式，一个主节点多个从节点，主节点作为代理

-------------------

## 怎么运行

### 1.单机模式

Redant 是一个基于 Netty 的 Web 容器，类似 Tomcat 和 WebLogic 等容器

你需要做的就是通过启动它，让他来工作 

- 1 : 通过 IDEA 或者 eclipse 等工具来运行下面的 Main 方法:

``` sh
com.redant.core.ServerBootstrap
```

- 2 : 通过 Maven 将 Redant 打包成一个可执行的 jar 包, 然后运行它:

``` sh
java -jar redant-jar-with-dependencies.jar
```


### 2.多节点模式

多节点模式是由主节点和若干个从节点构成的。

主节点接收到请求后，将请求转发给从节点来处理。


#### 启动主节点

- 1 : 启动一个 ZooKeeper 服务端

你可以在 zk.cfg 中设置启动的模式，默认的是 `cluster` 模式

但是这并不是必须的，如果你已经有一个正在运行的 Zk 的服务端，那么你可以直接使用它

如果你没有可用的 Zk 服务端的话，那你可以通过运行下面的 Main 方法来启动一个：

``` sh
com.redant.cluster.bootstrap.ZkBootstrap
```

- 2 : 启动一个 Master 服务端

要启动一个 Master 服务端，只要运行下面的 Main 方法：

``` sh
com.redant.cluster.bootstrap.MasterServerBootstrap
```

#### 启动从节点

- 1：启动一个 Slave 服务端

要启动一个 Slave 服务端，只要运行下面的 Main 方法： 

``` sh
com.redant.cluster.bootstrap.SlaveServerBootstrap
```

## 例子

你可以运行 `redant-example` 模块中提供的例子来体验一下

启动的方式和上面讲述的完全一样，区别只是主类是在 `redant-example` 中

启动完之后，你可以在浏览器中访问 http://127.0.0.1:8888 来查看具体的效果 (默认的端口可以在 redant.properties 中修改)

如果你看到了这样的消息："Welcome to redant!" 这就意味着你已经启动成功了. 

在 `redant-example` 模块中，内置了以下几个默认的路由:

| 方法类型           | URL                          | 响应类型                       |
| ----------------- | ---------------------------- | ----------------------------- |
| GET               | /                            | HTML                          |
| \*                | \*                           | HTML                          |
| GET               | /user/count                  | JSON                          |
| GET               | /user/list                   | JSON                          |
| GET               | /user/info                   | JSON                          |




## Bean 管理器

跟 Spring 一样，你可以通过 @Bean 注解来管理所有的对象，通过 @Autowired 来自动注入

**Tips：** 更多信息请查看wiki: [Bean][1]



## 自定义路由

跟 Spring 一样，你可以通过 @RouterController 来自定义一个 Controller.
 
@RouterMapping 注解用在方法级别，@RouterController + @RouterMapping 唯一定义一个 http 请求。

@RouterParam 注解用在方法的参数上。通过该注解可以自动将基本类型转成 POJO 对象。

**Tips：** 更多信息请查看wiki: [Router][2]



## Session 管理器

Session 管理器可以管理用户自定义的 Session. 

Session 是通过 Netty的 ChannelHandlerContext 来体现的，channelId 作为 sessionId。
 
每个 Session 中都保持着一个 Map 来存储各种信息

**Tips：** 更多信息请查看wiki: [Session][3]



## Cookie 管理器

Cookie 管理器可以管理用户自定义的 Cookie。

重要提醒：`cookies 需要在返回 render 结果之前设置`

**Tips：** 更多信息请查看wiki: [Cookie][4]




  [1]: https://github.com/all4you/redant/wiki/1:Bean
  [2]: https://github.com/all4you/redant/wiki/2:Router
  [3]: https://github.com/all4you/redant/wiki/3:Session
  [4]: https://github.com/all4you/redant/wiki/4:Cookie


