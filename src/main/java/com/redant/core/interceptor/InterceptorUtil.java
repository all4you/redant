package com.redant.core.interceptor;

import com.redant.common.constants.CommonConstants;
import com.xiaoleilu.hutool.lang.ClassScaner;
import io.netty.channel.ChannelHandler;
import org.apache.commons.collections.CollectionUtils;

import java.util.Set;

/**
 * @author gris.wang
 * @since 2017/11/15
 **/
public class InterceptorUtil {


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
        return getInterceptors(PreHandleInterceptor.class);
    }


    public static ChannelHandler[] getAfterInterceptors(){
        return getInterceptors(AfterHandleInterceptor.class);
    }


}
