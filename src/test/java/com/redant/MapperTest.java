package com.redant;

import com.redant.mappers.UserMapper;
import com.redant.mvc.user.UserBean;
import com.redant.mybatissist.mapper.Mapper;
import com.redant.mybatissist.sqlsession.SqlSessionContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapperTest {

    private static Logger logger = LoggerFactory.getLogger(MapperTest.class);

    private Mapper mapper;

    @Before
    public void beforeTest(){
        mapper = SqlSessionContext.getSqlSession().getMapper(UserMapper.class);
    }

    @After
    public void afterTest(){
        mapper = null;
    }

    @Test
    public void testSelectCount(){
        UserBean bean = new UserBean();
        bean.setUserName("wh");
        int result = mapper.selectCount(UserBean.class,null);
        logger.info("result:{}",result);
    }

    @Test
    public void testSelectOne(){
        UserBean bean = new UserBean();
        bean.setUserName("wh");
        Object result = mapper.selectOne(UserBean.class,bean);
        logger.info("result:{}",result);
    }


}