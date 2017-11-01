package com.mybatissist.config;

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
     * 是否对各个SQLProvider生成的sql语句进行缓存
     */
    private boolean cacheSql;

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

    public Config cacheSql(boolean cacheSql){
        config.cacheSql = cacheSql;
        return config;
    }

    public boolean cacheSql(){
        return config.cacheSql;
    }

    public Config cacheSqlSession(boolean cacheSqlSession){
        config.cacheSqlSession = cacheSqlSession;
        return config;
    }

    public boolean cacheSqlSession(){
        return config.cacheSqlSession;
    }


}
