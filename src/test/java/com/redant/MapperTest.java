package com.redant;

import com.redant.mappers.UserMapper;
import com.redant.mvc.user.UserBean;
import com.mybatissist.sqlsession.SqlSessionContext;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MapperTest {

    private static Logger logger = LoggerFactory.getLogger(MapperTest.class);

    private SqlSession sqlSession;

    private UserMapper mapper;

    @Before
    public void beforeTest(){
        sqlSession = SqlSessionContext.getSqlSession();
        mapper = sqlSession.getMapper(UserMapper.class);
    }

    @After
    public void afterTest(){
        sqlSession.close();
        mapper = null;
    }

    @Test
    public void testSelectCount(){
        UserBean bean = new UserBean();
        bean.setUserName("wh");
        int result = mapper.selectCount(null,UserBean.class);
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


}