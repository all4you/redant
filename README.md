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

##### Bean的使用示例
``` java
@Bean(name="userService") // 如果不指定name，则使用类名作为bean的名称
public class UserServiceImpl implements IUserService{

}

@Bean()  // 如果需要使用Autowired，则该类自身需要使用Bean注解标注
@RouterController(path="/UserController")
public class UserController {

    @Autowired(name="userService")
    private IUserService userService;
}
```


## 路由管理

> 使用RouterController来定义一个路由Controller类，RouterMapping用以指定Controller中每个具体的方法，RouterController+RouterMapping唯一匹配一个http请求的路由，RouterParam用以标识方法的参数，用以实现http请求参数的转换，基础类型的参数必须使用RouterParam注解进行标识，POJO对象可以不使用RouterParam标识。

##### 路由的使用示例
``` java
@Bean()  // 如果需要使用Autowired，则该类自身需要使用Bean注解标注
@RouterController(path="/UserController")
public class UserController {

    @Autowired(name="userService")
    private IUserService userService;

    @RouterMapping(path="/getUserInfo",requestMethod=RequestMethod.GET,renderType=RenderType.JSON)
    public BaseRender getUserInfo(UserBean userBean,@RouterParam(key="pid") Integer pid){
        JSONObject object = new JSONObject();
        object.put("user",userService.selectUserInfo(userBean.getId()));
        object.put("pid",pid);
        return new BaseRender(RenderType.JSON,object);
    }
}
```

## 参数转换

> 通过TypeConverter接口，实现http请求的参数转换，目前支持将parameter参数转换为：基础类型、Map、List、JavaBean。



## Session管理

> 实现了自定义的Session管理，Session是基于Netty的ChannelHandlerContext（以下简称context）实现的，使用context中通道的channelId作为sessionId。每个session使用一个map来存储需要保存的属性值。

##### HttpSession
``` java
/**
 * HttpSession
 * @author gris.wang
 * @since 2017/11/6
 */
public class HttpSession {

    /**
     * 会话id
     */
    private ChannelId id;

    /**
     * 会话保存的ChannelHandlerContext
     */
    private ChannelHandlerContext context;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 过期时间
     * 每次请求时都更新过期时间
     */
    private Long expireTime;

    /**
     * Session中存储的数据
     */
    private Map<String,Object> sessionMap;

    private void assertCookieMapNotNull(){
        if(sessionMap==null){
            sessionMap= new HashMap<String,Object>();
        }
    }


    private HttpSession(){

    }


    //=====================================


    public HttpSession(ChannelHandlerContext context){
        this(context.channel().id(),context);
    }

    public HttpSession(ChannelId id,ChannelHandlerContext context){
        this(id,context,System.currentTimeMillis());
    }

    public HttpSession(ChannelId id,ChannelHandlerContext context,Long createTime){
        this(id,context,createTime,createTime + SessionConfig.instance().sessionTimeOut());
    }

    public HttpSession(ChannelId id,ChannelHandlerContext context,Long createTime,Long expireTime){
        this.id = id;
        this.context = context;
        this.createTime = createTime;
        this.expireTime = expireTime;
        assertCookieMapNotNull();
    }

    public getXXX(){}

    public setXXX(){}
    
    /**
     * 是否过期
     * @return
     */
    public boolean isExpire(){
        return this.expireTime>=System.currentTimeMillis();
    }

    /**
     * 设置attribute
     * @param key
     * @param val
     */
    public void setAttribute(String key,Object val){
        sessionMap.put(key,val);
    }

    /**
     * 获取key的值
     * @param key
     */
    public void getAttribute(String key){
        sessionMap.get(key);
    }

    /**
     * 是否存在key
     * @param key
     */
    public boolean containsAttribute(String key){
        return sessionMap.containsKey(key);
    }
}
```


## Cookie管理

> 实现了自定义的Cookie管理，需要注意的是`cookie需要在writeResponse之前写入response的header中`。

##### CookieHelper
``` java
/**
 * 操作Cookie的辅助类
 * @author gris.wang
 * @since 2017/11/6
 */
public class CookieHelper {

    /**
     * 获取HttpRequest中的Cookies
     * @param request
     * @return
     */
    public static Set<Cookie> getCookies(HttpRequest request){
        Set<Cookie> cookies;
        String value = request.headers().get(HttpHeaderNames.COOKIE);
        if (value == null) {
            cookies = Collections.emptySet();
        } else {
            cookies = ServerCookieDecoder.STRICT.decode(value);
        }
        return cookies;
    }

    /**
     * 设置Cookie
     * @param response
     * @param cookie
     */
    public static void setCookie(HttpResponse response,Cookie cookie){
        response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
    }

    /**
     * 设置所有的Cookie
     * @param request
     * @param response
     */
    public static void setCookies(HttpRequest request,HttpResponse response){
        Set<Cookie> cookies = getCookies(request);
        if (!cookies.isEmpty()) {
            for (Cookie cookie : cookies) {
                setCookie(response,cookie);
            }
        }
    }


    /**
     * 添加一个Cookie
     * @param response response
     * @param name  cookie名字
     * @param value cookie值
     */
    public static void addCookie(HttpResponse response,String name,String value){
        CookieHelper.addCookie(response,name,value,null);
    }

    /**
     * 添加一个Cookie
     * @param response response
     * @param name  cookie名字
     * @param value cookie值
     * @param domain cookie所在域
     */
    public static void addCookie(HttpResponse response,String name,String value,String domain){
        CookieHelper.addCookie(response,name,value,domain,0);
    }


    /**
     * 添加一个Cookie
     * @param response response
     * @param name  cookie名字
     * @param value cookie值
     * @param maxAge cookie生命周期  以秒为单位
     */
    public static void addCookie(HttpResponse response,String name,String value,long maxAge){
        CookieHelper.addCookie(response,name,value,null,maxAge);
    }

    /**
     * 添加一个Cookie
     * @param response response
     * @param name  cookie名字
     * @param value cookie值
     * @param domain cookie所在域
     * @param maxAge cookie生命周期  以秒为单位
     */
    public static void addCookie(HttpResponse response,String name,String value,String domain,long maxAge){
        Cookie cookie = new DefaultCookie(name,value);
        cookie.setPath("/");
        if(domain!=null && domain.trim().length()>0) {
            cookie.setDomain(domain);
        }
        if(maxAge>0){
            cookie.setMaxAge(maxAge);
        }
        setCookie(response,cookie);
    }

    /**
     * 将cookie封装到Map里面
     * @param request HttpRequest
     * @return
     */
    public static Map<String,Cookie> getCookieMap(HttpRequest request){
        Map<String,Cookie> cookieMap = new HashMap<String,Cookie>();
        Set<Cookie> cookies = getCookies(request);
        if(null!=cookies && !cookies.isEmpty()){
            for(Cookie cookie : cookies){
                cookieMap.put(cookie.name(), cookie);
            }
        }
        return cookieMap;
    }

    /**
     * 根据名字获取Cookie
     * @param request HttpRequest
     * @param name cookie名字
     * @return
     */
    public static Cookie getCookie(HttpRequest request,String name){
        Map<String,Cookie> cookieMap = getCookieMap(request);
        return cookieMap.containsKey(name)?cookieMap.get(name):null;
    }

    /**
     * 获取Cookie的值
     * @param request HttpRequest
     * @param name cookie名字
     * @return
     */
    public static String getCookieValue(HttpRequest request,String name){
        Cookie cookie = getCookie(request,name);
        return cookie.value();
    }

    /**
     * 删除一个Cookie
     * @param request
     * @param response
     * @param name
     * @return
     */
    public static boolean deleteCookie(HttpRequest request,HttpResponse response,String name) {
        Cookie cookie = getCookie(request,name);
        if(cookie!=null){
            cookie.setMaxAge(0);
            cookie.setPath("/");
            setCookie(response,cookie);
            return true;
        }
        return false;
    }
}
```



## Mybatissist--通用CRUD工具

> Mybatissist 是一个基于Mybatis注解的通用CRUD工具，使用Mybatissist可以仅仅关注数据库表和实体类的映射，而不必关系具体的操作过程，也不需要编写额外的Mapper.xml来指定SQL语句，只需要定义好实体类和xxxMapper接口，且保证该xxxMapper接口继承自通用的Mapper接口，既能使用通用接口中的所有CRUD方法。


##### 通用Mapper接口
``` java
// 所有需要使用通用CRUD操作的接口都需要继承该Mapper接口
public interface Mapper<T> extends InsertMapper<T>,UpdateMapper<T>,DeleteMapper<T>,SelectMapper<T> {

}
```


##### 实体类
``` java
// 使用@Table注解指定表名和别名，如果不指定会将类名根据驼峰转换为下划线的结果作为表名
@Table(name="user_bean",alias="u") 
public class UserBean extends BaseBean {

    private Integer id;
    private String userName;
    private String password;

    public getXXX();
    public setXXX();
}
```


##### 业务Mapper接口
``` java
public interface UserMapper extends Mapper<UserBean> {

}
```



##### 所有通用的CRUD方法
``` java
public class MapperTest {

    private static Logger logger = LoggerFactory.getLogger(MapperTest.class);

    private boolean autoCommit = true;

    private SqlSession sqlSession;

    private UserMapper mapper;

    @Before
    public void beforeTest(){
        sqlSession = SqlSessionContext.getSqlSession(autoCommit);
        mapper = sqlSession.getMapper(UserMapper.class);
        // 也可以通过MapperUtil来获取mapper对象
        // mapper = MapperUtil.getMapper(UserMapper.class);
    }

    @After
    public void afterTest(){
        sqlSession.close();
        mapper = null;
    }

    //================ Select

    @Test
    public void testSelectCount(){
        UserBean bean = new UserBean();
        bean.setUserName("wh");
        Integer result = mapper.selectCount(bean,UserBean.class);
        logger.info("result:{}",result);
    }

    @Test
    public void testSelectOne(){
        UserBean bean = new UserBean();
        bean.setUserName("wh");
        bean.setId(1);
        UserBean result = mapper.selectOne(bean,UserBean.class);
        logger.info("result:{}",result);
    }

    @Test
    public void testSelectList(){
        UserBean bean = new UserBean();
        bean.setUserName("wh");
        bean.setId(1);
        int pageNum = 1;
        int pageSize = 3;
        // 使用PageHelper插件进行分页操作，实际返回的结果是Page类型
        List<UserBean> result = mapper.selectList(bean,pageNum,pageSize,UserBean.class);
        logger.info("result:{}",Arrays.toString(result.toArray()));
    }

    @Test
    public void testSelectAll(){
        List<UserBean> result = mapper.selectAll(UserBean.class);
        logger.info("result:{}",Arrays.toString(result.toArray()));
    }

    //================ Insert

    @Test
    public void testInsert(){
        UserBean bean = new UserBean();
        bean.setUserName("tx3");
        bean.setPassword("werwe");
        int result = mapper.insert(bean,UserBean.class);
        logger.info("result:{}",result);
    }

    @Test
    public void testInsertWithId(){
        UserBean bean = new UserBean();
        bean.setId(3);
        bean.setUserName("tx5");
        bean.setPassword("werwe");
        int result = mapper.insertWithId(bean,UserBean.class);
        logger.info("result:{}",result);
    }

    @Test
    public void testInsertSelective(){
        UserBean bean = new UserBean();
        bean.setUserName("tx5");
        int result = mapper.insertSelective(bean,UserBean.class);
        logger.info("result:{}",result);
    }

    //================ Update

    @Test
    public void testUpdateByPrimaryKey(){
        UserBean bean = new UserBean();
        bean.setId(12);
        bean.setUserName("tx5");
        bean.setPassword("3553er");
        int result = mapper.updateByPrimaryKey(bean,UserBean.class);
        logger.info("result:{}",result);
    }

    @Test
    public void testUpdateByPrimaryKeySelective(){
        UserBean bean = new UserBean();
        bean.setId(12);
        bean.setUserName("tx5464");
        int result = mapper.updateByPrimaryKeySelective(bean,UserBean.class);
        logger.info("result:{}",result);
    }

    @Test
    public void testUpdateByKey(){
        UserBean bean = new UserBean();
        bean.setId(12);
        bean.setUserName("tx12");
        List<String> keys = Arrays.asList(new String[]{"id"});
        int result = mapper.updateByKey(bean,UserBean.class,keys);
        logger.info("result:{}",result);
    }


    @Test
    public void testUpdateByKeySelective(){
        UserBean bean = new UserBean();
        bean.setUserName("tx12");
        bean.setPassword("ttt");
        List<String> keys = Arrays.asList(new String[]{"userName"});
        int result = mapper.updateByKeySelective(bean,UserBean.class,keys);
        logger.info("result:{}",result);
    }


    //================ Delete

    @Test
    public void testDeleteByPrimaryKey(){
        UserBean bean = new UserBean();
        bean.setId(1);
        int result = mapper.deleteByPrimaryKey(bean,UserBean.class);
        logger.info("result:{}",result);
    }

    @Test
    public void testDeleteBySelective(){
        UserBean bean = new UserBean();
        bean.setUserName("tx3");
        bean.setPassword("werwe");
        int result = mapper.deleteBySelective(bean,UserBean.class);
        logger.info("result:{}",result);
    }

}
```


