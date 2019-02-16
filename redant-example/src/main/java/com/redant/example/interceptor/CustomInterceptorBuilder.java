package com.redant.example.interceptor;

import com.redant.core.interceptor.Interceptor;
import com.redant.core.interceptor.InterceptorBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author houyi
 **/
public class CustomInterceptorBuilder implements InterceptorBuilder {

    private volatile boolean loaded = false;

    private List<Interceptor> interceptors = null;

    @Override
    public List<Interceptor> build() {
        if(!loaded){
            synchronized (CustomInterceptorBuilder.class) {
                if(!loaded){
                    interceptors = new ArrayList<>();
                    if (activeBlock()) {
                        interceptors.add(new BlockInterceptor());
                    }
                    if (activePerf()) {
                        interceptors.add(new PerformanceInterceptor());
                    }
                    loaded = true;
                }
            }
        }
        return interceptors;
    }

    private boolean activeBlock(){
        return false;
    }

    private boolean activePerf(){
        return true;
    }

}
