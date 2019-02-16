package com.lememo.core.interceptor;

import com.redant.core.interceptor.Interceptor;
import com.redant.core.interceptor.InterceptorProvider;

import java.util.List;

/**
 * @author houyi
 **/
public class InterceptorProviderTest {

    public static void main(String[] args) {
        for(int i=0;i<10;i++){
            new Thread(new Run()).start();
        }
    }

    static class Run implements Runnable {
        @Override
        public void run() {
            List<Interceptor> interceptors = InterceptorProvider.getInterceptors();
            System.out.println("Thread=[" + Thread.currentThread().getName() + "] interceptors size="+interceptors.size());
        }
    }

}
