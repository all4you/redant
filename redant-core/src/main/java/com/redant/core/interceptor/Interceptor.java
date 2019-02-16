package com.redant.core.interceptor;

import java.util.List;
import java.util.Map;

/**
 * @author houyi
 **/
public abstract class Interceptor {

    /**
     * 拦截器的前置处理方法
     */
    public boolean preHandle(Map<String, List<String>> paramMap){
        return true;
    }

    /**
     * 拦截器的后置处理方法
     */
    public abstract void postHandle(Map<String, List<String>> paramMap);

}
