# 欢迎使用RedAnt


**RedAnt**是一款基于netty的轻量级的http框架。特点概述：
 
- **对象管理** ：通过Bean注解，实现对象的全局、统一管理；
- **自动注入** ：通过Autowired注解，实现Bean对象的自动注入；
- **自由路由** ：使用RouterController、RouterMapping、RouterParam注解实现路由的自定义；
- **参数转换** ：通过TypeConverter接口，实现http请求的参数转换（目前支持将参数转换为：基础类型、Map、List、JavaBean）；
- **结果渲染** ：通过自定义的Render接口，对返回结果进行渲染，目前支持渲染html、xml、plain、json等数据；
- **通用查询** ：内置Mybatissist持久层CRUD通用方法操作助手。

-------------------



## Mybatissist

> Mybatissist 是一个基于Mybatis注解的通用CRUD工具，使用Mybatissist可以仅仅关注数据库表和实体类的映射，而不必关系具体的操作过程，也不需要编写额外的Mapper.xml来指定SQL语句，只需要定义好实体类和xxxMapper接口，且保证该xxxMapper接口继承自通用的Mapper接口，既能使用通用接口中的所有CRUD方法。


### 通用Mapper接口
``` java
public interface Mapper<T> extends InsertMapper<T>,UpdateMapper<T>,DeleteMapper<T>,SelectMapper<T> {

}
```


### 实体类
``` java
@Table(name="user_bean",alias="u")
public class UserBean extends BaseBean {

    private Integer id;

    private String userName;

    private String password;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
```


### 业务Mapper接口
``` java
public interface UserMapper extends Mapper<UserBean> {

}
```



### 测试方法
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
        List<UserBean> result = mapper.selectList(bean,UserBean.class);
        logger.info("result:{}",result);
    }

    @Test
    public void testSelectAll(){
        List<UserBean> result = mapper.selectAll(UserBean.class);
        logger.info("result:{}",result);
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
