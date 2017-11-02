package com.mybatissist.config;

import com.mybatissist.sqlsession.SqlSessionContext;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 针对mybatissist的全局配置项
 * @author gris.wang
 * @since 2017/11/1
 */
public class Config {

    private Config(){

    }

    /**
     * 是否打印sql语句
     */
    private boolean printSql;

    /**
     * 是否对SelectSQLProvider生成的sql语句进行缓存
     */
    private boolean cacheSelectSql;

    /**
     * 是否对SqlSession进行缓存
     */
    private boolean cacheSqlSession;

    /**
     * 单例
     */
    private static Config config;

    static{
        if(config==null){
            config = new Config();
        }
    }


    //======================================


    /**
     * 获取实例
     * @return
     */
    public static Config instance(){
        return config;
    }

    public Config printSql(boolean printSql){
        config.printSql = printSql;
        return config;
    }

    public boolean printSql(){
        return config.printSql;
    }

    public Config cacheSelectSql(boolean cacheSelectSql){
        config.cacheSelectSql = cacheSelectSql;
        return config;
    }

    public boolean cacheSelectSql(){
        return config.cacheSelectSql;
    }

    public Config cacheSqlSession(boolean cacheSqlSession){
        config.cacheSqlSession = cacheSqlSession;
        return config;
    }

    public boolean cacheSqlSession(){
        return config.cacheSqlSession;
    }

    @Override
    public String toString() {
        return "["+super.toString()+"]:{printSql:"+config.printSql+",cacheSelectSql:"+config.cacheSelectSql+",cacheSqlSession:"+config.cacheSqlSession+"}";
    }

    public static void main(String[] args) {
        int loopTimes = 200;

        class Runner implements Runnable{

            private Logger logger = LoggerFactory.getLogger(Runner.class);

            @Override
            public void run() {
                Config result = Config.instance();
                logger.info("result={},currentThread={}",result,Thread.currentThread().getName());
            }
        }

        for(int i=0;i<loopTimes;i++){
            new Thread(new Runner()).start();
        }

    }

}
