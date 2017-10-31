# 欢迎使用RedAnt


**RedAnt**是一款基于netty、支持http协议的web框架。拥有如下特点：
 
- **对象管理** ：通过Bean注解，实现对象的全局、统一管理；
- **自动注入** ：通过Autowired注解，实现Bean对象的自动注入；
- **自由路由** ：使用RouterController、RouterMapping、RouterParam注解实现路由的自定义；
- **参数转换** ：通过TypeConverter接口，实现http请求的参数转换（目前支持：基础类型、Map、List、JavaBean）；
- **结果渲染** ：通过自定义的Render接口，对返回结果进行渲染，目前支持渲染html、xml、plain、json等数据；
- **通用查询** ：内置Mybatissist持久层CRUD通用方法操作助手，使用PageHelper插件处理分页。

-------------------

## 如何运行

> 该web框架是基于netty，内部使用TCP协议，向上支持了http协议，不需要使用tomcat或者weblogic等中间件，直接使用Java运行即可，服务端的入口是：com.redant.main.ServerBootstrap。
> >1：可以将代码下载后导入IDEA或者eclipse，然后通过IDE工具运行。
> >
> >2：可以将代码下载后用maven打成jar包，使用java命令运行。

> 启动后直接在浏览器中访问 http://127.0.0.1:8888(默认端口可以在redant.properties文件中修改)，如果可以正常返回 “Welcome to redant!”即说明项目启动成功。目前项目中内置了三个Controller，服务器启动时会将所有的Controller打印出来：
 
> GET  /                               HTML
>
> GET  /UserController/getUserCount    JSON
>
> GET  /UserController/getUserList     JSON
>
> GET  /UserController/getUserInfo     JSON



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


## 自由路由

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
        object.put("user",userService.selectUserInfo(34));
        object.put("pid",pid);
        return new BaseRender(RenderType.JSON,object);
    }
}
```

## 参数转换

> 通过TypeConverter接口，实现http请求的参数转换，目前支持将parameter参数转换为：基础类型、Map、List、JavaBean。





## 通用查询助手-Mybatissist

> Mybatissist 是一个基于Mybatis注解的通用CRUD工具，使用Mybatissist可以仅仅关注数据库表和实体类的映射，而不必关系具体的操作过程，也不需要编写额外的Mapper.xml来指定SQL语句，只需要定义好实体类和xxxMapper接口，且保证该xxxMapper接口继承自通用的Mapper接口，既能使用通用接口中的所有CRUD方法。


##### 通用Mapper接口
``` java
public interface Mapper<T> extends InsertMapper<T>,UpdateMapper<T>,DeleteMapper<T>,SelectMapper<T> {

}
```


##### 实体类
``` java
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



##### 测试方法
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
        Integer result = mapper.selectCount(null,UserBean.class);
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
        List<UserBean> result = mapper.selectList(null,pageNum,pageSize,UserBean.class);
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
