package com.redant.core.interceptor;

import com.redant.common.constants.CommonConstants;
import com.xiaoleilu.hutool.lang.ClassScaner;
import com.xiaoleilu.hutool.util.ArrayUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;
import io.netty.channel.ChannelHandler;

import java.util.Arrays;
import java.util.Set;

/**
 * @author gris.wang
 * @since 2017/11/15
 **/
public class InterceptorUtil {


    public static ChannelHandler[] getPreInterceptors(){
        return InterceptorsHolder.preInterceptors;
    }


    public static ChannelHandler[] getAfterInterceptors(){
        return InterceptorsHolder.afterInterceptors;
    }


    private static class InterceptorsHolder{

        private static ChannelHandler[] preInterceptors;

        private static ChannelHandler[] afterInterceptors;

        static{
            preInterceptors = getInterceptors(PreHandleInterceptor.class);
            afterInterceptors = getInterceptors(AfterHandleInterceptor.class);
        }

        private static ChannelHandler[] getInterceptors(Class interceptorClass){
            Set<Class<?>> classSet = ClassScaner.scanPackageBySuper(CommonConstants.INTERCEPTOR_SCAN_PACKAGE,interceptorClass);
            if(CollectionUtil.isEmpty(classSet)){
                return new ChannelHandler[]{};
            }
            ChannelHandler[] interceptors = new ChannelHandler[classSet.size()];
            try {
                int i=0;
                for (Class<?> cls : classSet) {
                    interceptors[i++]=(ChannelHandler)cls.newInstance();
                }
            }catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            return interceptors;
        }

    }




}
