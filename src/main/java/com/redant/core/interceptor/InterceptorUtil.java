package com.redant.core.interceptor;

import com.redant.common.constants.CommonConstants;
import com.xiaoleilu.hutool.lang.ClassScaner;
import io.netty.channel.ChannelHandler;
import org.apache.commons.collections.CollectionUtils;

import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author gris.wang
 * @since 2017/11/15
 **/
public class InterceptorUtil {

    private static ChannelHandler[] preInterceptors;

    private static ChannelHandler[] afterInterceptors;

    private static Lock preLock = new ReentrantLock();

    private static Lock afterLock = new ReentrantLock();

    private static ChannelHandler[] getInterceptors(Class interceptorClass){
        Set<Class<?>> classSet = ClassScaner.scanPackageBySuper(CommonConstants.INTERCEPTOR_SCAN_PACKAGE,interceptorClass);
        if(CollectionUtils.isEmpty(classSet)){
            return new ChannelHandler[]{};
        }
        ChannelHandler[] interceptors = new ChannelHandler[classSet.size()];
        try {
            int i=0;
            for (Class<?> cls : classSet) {
                interceptors[i++]=(ChannelHandler)cls.newInstance();
            }
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return interceptors;
    }


    public static ChannelHandler[] getPreInterceptors(){
        preLock.lock();
        try {
            if(preInterceptors==null){
                preInterceptors = getInterceptors(PreHandleInterceptor.class);
            }
        }finally {
            preLock.unlock();
        }
        return preInterceptors;
    }


    public static ChannelHandler[] getAfterInterceptors(){
        afterLock.lock();
        try {
            if(afterInterceptors==null){
                afterInterceptors = getInterceptors(AfterHandleInterceptor.class);
            }
        }finally {
            afterLock.unlock();
        }
        return afterInterceptors;
    }


}
