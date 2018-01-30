# RedAnt Project


**RedAnt** is a lightweight distributed web container which based on Netty

 **Features:**
 
- **Bean Manager** : all objects can be managed by using Bean annotation 
- **Object Autowired** : object can be injected automatically by using Autowired annotation
- **Customized Route**  : user can self customize their routes with RouterController,RouterMapping,RouterParam annotation
- **Param Convert**  : with TypeConverter interface http parameters can be converted into Object (support PrimitiveType,Map,List,JavaBean)
- **Session Manager**  : include a session manager,a session is a Netty ChannelHandlerContext
- **Cookie Manager**  : include a cookie manager,user should handle their cookies before return a render
- **Result Render**  : a render interface which support html,xml,plain,json
- **Standalone Mode**  : support single node mode
- **Multi nodes Mode**  : support multi slaves and one master mode,which master is used as a proxy

-------------------

## How to Run

### 1.Standalone mode

> Redant is a web container based on Netty,tomcat or weblogic is no longer required to publish a web application,the only thing you should do is start it with java. The main Class is : **com.redant.main.ServerBootstrap**
> 
> >1 : Use IDEA or eclipse to run the Main Class。
> >
> >2 : Use Maven to build Redant into an executable jar, and run with : **java -jar redant-jar-with-dependencies.jar**
>
> After startup the Server, visit  http://127.0.0.1:8888 (the default port can be modified in redant.properties) in a browser.If you get  "Welcome to redant!" returned then the server is started successfully. There are four default Routers included:
>
> GET  /              HTML
>
> GET  /user/count    JSON
>
> GET  /user/list     JSON
>
> GET  /user/info     JSON
>
> \*    \*            HTML


### 2.Multi nodes mode
> The Multi nodes mode is made up by a Master and several Slaves.
>
> Master will accept http request,and send them to slave to handle
>
> The Main Class to start ZooKeeperServer : **com.redant.zk.ZkBootstrap**
>
> The Main Class to start Master : **com.redant.cluster.master.MasterServerBootstrap**,the start process:
>>1 : Start a ZooKeeperServer(you can set to use Standalone or Cluster mode in zkConfig.properties default mode is Standalone)
>>
>>2 : Start a SlaveNode Watcher to watch the state of all Slave
>>
>>3 : Start a Master Server
>
> The Main Class to start Slave : **com.redant.cluster.slave.SlaveServerBootstrap**,the start process:
>>1：Register the current Server into ZooKeeper
>>
>>2：Start a Slave Server



## Bean Manager

> All objects can be managed by using Bean annotation,and object can be injected automatically by using Autowired annotation.It's very easy to use them like you are doing it with spring
> 
> **Tips：** More information please see wiki: [Bean][1]



## Customized Route

> Use RouterController to customize a Controller. RouterMapping will specify the exact method,RouterController+RouterMapping can only match a http request. RouterParam is used to mark the parameters in the method.POJO will be converted automatically while PrimitiveType should be marked with a RouterParam annotation
> 
> **Tips：** More information please see wiki: [Router][2]



## Session Manager

> A Session Manager is included to store custom sessions. A session is a Netty ChannelHandlerContext,and the channelId is used as a sessionId. Each session hold a map to store the properties
> 
> **Tips：** More information please see wiki: [Session][3]



## Cookie Manager

> A Cookie Manager is included to handle custom cookies,is is important to note that`cookies should be set or remove before a render is returned`
> 
> **Tips：** More information please see wiki: [Cookie][4]




  [1]: https://github.com/all4you/redant/wiki/1:Bean
  [2]: https://github.com/all4you/redant/wiki/2:Router
  [3]: https://github.com/all4you/redant/wiki/3:Session
  [4]: https://github.com/all4you/redant/wiki/4:Cookie


