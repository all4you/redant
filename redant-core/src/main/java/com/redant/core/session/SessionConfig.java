package com.redant.core.session;

/**
 * 针对HttpSession的全局配置项
 * @author gris.wang
 * @date 2017/11/6
 */
public class SessionConfig {

    /**
     * 默认超时时间
     */
    private static final Long DEFAULT_SESSION_TIME_OUT = 60*60*1000L;

    private SessionConfig(){

    }

    /**
     * session超时时间
     */
    private Long sessionTimeOut = DEFAULT_SESSION_TIME_OUT;

    /**
     * 单例
     */
    private static SessionConfig config;

    static{
        if(config==null){
            config = new SessionConfig();
        }
    }


    //======================================


    /**
     * 获取实例
     * @return
     */
    public static SessionConfig instance(){
        return config;
    }

    public SessionConfig sessionTimeOut(Long sessionTimeOut){
        config.sessionTimeOut = sessionTimeOut;
        return config;
    }

    public Long sessionTimeOut(){
        return config.sessionTimeOut;
    }


    @Override
    public String toString() {
        return "["+super.toString()+"]:{sessionTimeOut:"+config.sessionTimeOut+"}";
    }


}
