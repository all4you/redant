package com.mybatissist.sqlsession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

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
     * @param autoCommit
     */
    public static SqlSession getSqlSession(boolean autoCommit){
        return sqlSessionHolder.getSqlSession(autoCommit);
    }


    public static void main(String[] args) {
        int loopTimes = 200;

        class Runner implements Runnable{

            private Logger logger = LoggerFactory.getLogger(Runner.class);

            @Override
            public void run() {
                SqlSession result = SqlSessionContext.getSqlSession(true);
                logger.info("result={},currentThread={}",result,Thread.currentThread().getName());
            }
        }

        for(int i=0;i<loopTimes;i++){
            new Thread(new Runner()).start();
        }

    }


}
