package com.redant;

import com.mybatissist.sqlsession.SqlSessionContext;
import com.redant.mappers.UserMapper;
import com.redant.mvc.user.UserBean;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

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