package com.mybatissist.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 针对mybatissist的全局配置项
 * @author gris.wang
 * @since 2017/11/1
 */
public class MybatissistConfig {

    private MybatissistConfig(){

    }

    /**
     * 默认的mybatis配置文件
     */
    private String mybatisConfig = "mybatis-config.xml";

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
    private static MybatissistConfig config;

    static{
        if(config==null){
            config = new MybatissistConfig();
        }
    }


    //======================================


    /**
     * 获取实例
     * @return
     */
    public static MybatissistConfig instance(){
        return config;
    }

    public String mybatisConfig(){
        return config.mybatisConfig;
    }

    public MybatissistConfig printSql(boolean printSql){
        config.printSql = printSql;
        return config;
    }

    public boolean printSql(){
        return config.printSql;
    }

    public MybatissistConfig cacheSelectSql(boolean cacheSelectSql){
        config.cacheSelectSql = cacheSelectSql;
        return config;
    }

    public boolean cacheSelectSql(){
        return config.cacheSelectSql;
    }

    public MybatissistConfig cacheSqlSession(boolean cacheSqlSession){
        config.cacheSqlSession = cacheSqlSession;
        return config;
    }

    public boolean cacheSqlSession(){
        return config.cacheSqlSession;
    }

    @Override
    public String toString() {
        return "["+super.toString()+"]:{mybatisConfig:"+config.mybatisConfig+",printSql:"+config.printSql+",cacheSelectSql:"+config.cacheSelectSql+",cacheSqlSession:"+config.cacheSqlSession+"}";
    }

    public static void main(String[] args) {
        int loopTimes = 200;

        class Runner implements Runnable{

            private Logger logger = LoggerFactory.getLogger(Runner.class);

            @Override
            public void run() {
                MybatissistConfig result = MybatissistConfig.instance();
                logger.info("result={},currentThread={}",result,Thread.currentThread().getName());
            }
        }

        for(int i=0;i<loopTimes;i++){
            new Thread(new Runner()).start();
        }

    }

}
