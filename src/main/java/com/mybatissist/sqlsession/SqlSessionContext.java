package com.mybatissist.sqlsession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SqlSession上下文
 * @author gris.wang
 * @create 2017-10-20
 */
public class SqlSessionContext {

    private static final SqlSessionHolder sqlSessionHolder = SqlSessionHolder.create();

    /**
     * 初始化SqlSessionFactory
     */
    public static void buildFactory(){
        sqlSessionHolder.buildFactory();
    }

    /**
     * 获取SqlSessionFactory
     */
    public static SqlSessionFactory getSqlSessionFactory(){
        return sqlSessionHolder.getSqlSessionFactory();
    }

    /**
     * 获取SqlSessionFactory
     */
    public static SqlSession getSqlSession(){
        return sqlSessionHolder.getSqlSession();
    }


    public static void main(String[] args) {
        int loopTimes = 20;

        class Runner implements Runnable{

            private Logger logger = LoggerFactory.getLogger(Runner.class);

            @Override
            public void run() {
                SqlSessionFactory result = SqlSessionContext.getSqlSessionFactory();
                logger.info("result={},currentThread={}",result,Thread.currentThread().getName());
            }
        }

        for(int i=0;i<loopTimes;i++){
            new Thread(new Runner()).start();
        }

    }


}
