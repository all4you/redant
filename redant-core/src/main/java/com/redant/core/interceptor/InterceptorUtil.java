package com.redant.core.interceptor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.ClassScaner;
import com.redant.core.common.constants.CommonConstants;
import io.netty.channel.ChannelHandler;

import java.util.Set;

/**
 * @author houyi.wh
 * @date 2017/11/15
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
