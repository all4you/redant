package com.redant.core.interceptor;

import cn.hutool.core.collection.CollectionUtil;

import java.util.*;

/**
 * @author houyi.wh
 * @date 2017/11/15
 **/
public class InterceptorHandler {

    public static boolean preHandle(Map<String, List<String>> paramMap){
        List<Interceptor> interceptors = InterceptorProvider.getInterceptors();
        if(CollectionUtil.isEmpty(interceptors)){
            return true;
        }
        for(Interceptor interceptor : interceptors){
            if(!interceptor.preHandle(paramMap)){
                return false;
            }
        }
        return true;
    }

    public static void afterHandle(Map<String, List<String>> paramMap){
        List<Interceptor> interceptors = InterceptorProvider.getInterceptors();
        if(CollectionUtil.isEmpty(interceptors)){
            return;
        }
        for(Interceptor interceptor : interceptors){
            interceptor.afterHandle(paramMap);
        }
    }




}
