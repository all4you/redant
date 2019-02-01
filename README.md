# RedAnt 项目

**RedAnt** 是一个基于 Netty 的轻量级 Web 容器

 **特性:**

- [x] **IOC容器** : 通过 @Bean 注解可以管理所有对象，通过 @Autowired 注解进行对象注入
- [x] **自定义路由**  : 通过 @Controller @Mapping @Param 注解可以自定义路由
- [x] **自动参数转换**  : 通过 TypeConverter 接口，http 参数会被转成对象(支持基本类型,Map,List,JavaBean)
- [x] **结果渲染**  : 支持对结果进行渲染，支持 html, xml, plain, json 格式
- [x] **Cookie管理**  : 内置一个 Cookie 管理器
- [x] **前置后置拦截器** ：支持前置拦截器与后置拦截器
- [x] **单机模式**  : 支持单机模式
- [x] **集群模式**  : 支持集群模式
- [x] **服务注册与发现** ：实现了一个基于 Zk 的服务注册与发现，来支持多节点模式
- [ ] **Session管理**  : 因为涉及到多节点模式，分布式 session 暂未实现





## 快速启动

### 1.单机模式

Redant 是一个基于 Netty 的 Web 容器，类似 Tomcat 和 WebLogic 等容器

只需要启动一个 Server，默认的实现类是 NettyHttpServer 就能快速启动一个 web 容器了，如下所示：

``` java
public final class ServerBootstrap {
    public static void main(String[] args) {
        Server nettyServer = new NettyHttpServer();
        // 各种初始化工作
        nettyServer.preStart();
        // 启动服务器
        nettyServer.start();
    }
}
```



### 2.集群模式

到目前为止，我描述的都是单节点模式，如果哪一天单节点的性能无法满足了，那就需要使用集群了，所以我也实现了集群模式。

集群模式是由一个主节点和若干个从节点构成的。主节点接收到请求后，将请求转发给从节点来处理，从节点把处理好的结果返回给主节点，由主节点把结果响应给请求。

要想实现集群模式需要有一个服务注册和发现的功能，目前是借助于 Zk 来做的服务注册与发现。


#### 准备一个 Zk 服务端

因为主节点需要把请求转发给从节点，所以主节点需要知道目前有哪些从节点，我通过 ZooKeeper 来实现服务注册与发现。

如果你没有可用的 Zk 服务端的话，那你可以通过运行下面的 Main 方法来启动一个 ZooKeeper 服务端：

``` java
public final class ZkBootstrap {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkBootstrap.class);

    public static void main(String[] args) {
        try {
            ZkServer zkServer = new ZkServer();
            zkServer.startStandalone(ZkConfig.DEFAULT);
        }catch (Exception e){
            LOGGER.error("ZkBootstrap start failed,cause:",e);
            System.exit(1);
        }
    }
}
```

这样你就可以在后面启动主从节点的时候使用这个 Zk 了。但是这并不是必须的，如果你已经有一个正在运行的 Zk 的服务端，那么你可以在启动主从节点的时候直接使用它，通过在 main 方法的参数中指定 Zk 的地址即可。



#### 启动主节点

只需要运行下面的代码，就可以启动一个主节点了：

``` java
public class MasterServerBootstrap {
    public static void main(String[] args) {
        String zkAddress = ZkServer.getZkAddressArgs(args,ZkConfig.DEFAULT);

        // 启动MasterServer
        Server masterServer = new MasterServer(zkAddress);
        masterServer.preStart();
        masterServer.start();
    }
}
```

如果在 main 方法的参数中指定了 Zk 的地址，就通过该地址去进行服务发现，否则会使用默认的 Zk 地址



#### 启动从节点

只需要运行下面的代码，就可以启动一个从节点了：

``` java
public class SlaveServerBootstrap {

    public static void main(String[] args) {
        String zkAddress = ZkServer.getZkAddressArgs(args,ZkConfig.DEFAULT);
        Node node = Node.getNodeWithArgs(args);

        // 启动SlaveServer
        Server slaveServer = new SlaveServer(zkAddress,node);
        slaveServer.preStart();
        slaveServer.start();
    }

}
```

如果在 main 方法的参数中指定了 Zk 的地址，就通过该地址去进行服务注册，否则会使用默认的 Zk 地址



## 例子

你可以运行 redant-example 模块中提供的例子来体验一下，example 模块中内置了两个 Controller 。

启动完之后，你可以在浏览器中访问 http://127.0.0.1:8888 来查看具体的效果 (默认的端口可以在 redant.properties 中修改)

如果你看到了这样的消息："Welcome to redant!" 这就意味着你已经启动成功了。

在 redant-example 模块中，内置了以下几个默认的路由:

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

跟 Spring 一样，你可以通过 @Controller 来自定义一个 Controller.

@Mapping 注解用在方法级别，@Controller + @Mapping 唯一定义一个 http 请求。

@Param 注解用在方法的参数上。通过该注解可以自动将基本类型转成 POJO 对象。

**Tips：** 更多信息请查看wiki: [Router][2]



## Cookie 管理器

Cookie 管理器可以管理用户自定义的 Cookie。

**Tips：** 更多信息请查看wiki: [Cookie][4]



## 联系我

> wh_all4you#hotmail.com

![contact-me](./logo.jpg)




[1]: https://github.com/all4you/redant/wiki/1:Bean
[2]: https://github.com/all4you/redant/wiki/2:Router
[3]: https://github.com/all4you/redant/wiki/3:Session
[4]: https://github.com/all4you/redant/wiki/4:Cookie


