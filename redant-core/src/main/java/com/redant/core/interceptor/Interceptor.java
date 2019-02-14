package com.redant.core.interceptor;

import java.util.List;
import java.util.Map;

/**
 * @author houyi
 **/
public abstract class Interceptor {

    /**
     * 前置拦截器拦截的方法
     */
    public boolean preHandle(Map<String, List<String>> paramMap){
        return true;
    }

    /**
     * 后置拦截器拦截的方法
     */
    public abstract void afterHandle(Map<String, List<String>> paramMap);

}
