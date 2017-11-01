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
    private boolean useSqlCache;

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

    public Config useSqlCache(boolean useSqlCache){
        config.useSqlCache = useSqlCache;
        return config;
    }

    public boolean useSqlCache(){
        return config.useSqlCache;
    }


}
