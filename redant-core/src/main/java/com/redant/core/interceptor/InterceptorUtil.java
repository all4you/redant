package com.redant.core.interceptor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.ClassScaner;
import com.redant.core.anno.Order;
import com.redant.core.common.constants.CommonConstants;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author houyi.wh
 * @date 2017/11/15
 **/
public class InterceptorUtil {

    public static boolean preHandle(Map<String, List<String>> paramMap){
        List<Interceptor> interceptors = InterceptorsHolder.interceptors;
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
        List<Interceptor> interceptors = InterceptorsHolder.interceptors;
        if(CollectionUtil.isEmpty(interceptors)){
            return;
        }
        for(Interceptor interceptor : interceptors){
            interceptor.afterHandle(paramMap);
        }
    }

    private static class InterceptorsHolder {

        private static List<Interceptor> interceptors;

        static{
            interceptors = getInterceptors();
        }

        private static List<Interceptor> getInterceptors(){
            Set<Class<?>> classSet = ClassScaner.scanPackageBySuper(CommonConstants.INTERCEPTOR_SCAN_PACKAGE,Interceptor.class);
            if(CollectionUtil.isEmpty(classSet)){
                return Collections.emptyList();
            }
            List<InterceptorWrapper> wrappers = new ArrayList<>(classSet.size());
            try {
                for (Class<?> cls : classSet) {
                    Interceptor interceptor =(Interceptor)cls.newInstance();
                    insertSorted(wrappers,interceptor);
                }
            }catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            return wrappers.stream()
                    .map(InterceptorWrapper::getInterceptor)
                    .collect(Collectors.toList());
        }

        private static void insertSorted(List<InterceptorWrapper> list, Interceptor interceptor) {
            int order = resolveOrder(interceptor);
            int idx = 0;
            for (; idx < list.size(); idx++) {
                // 将当前interceptor插入到order值比他大的第一个interceptor前面
                if (list.get(idx).getOrder() > order) {
                    break;
                }
            }
            list.add(idx, new InterceptorWrapper(order, interceptor));
        }

        private static int resolveOrder(Interceptor interceptor) {
            if (!interceptor.getClass().isAnnotationPresent(Order.class)) {
                return Order.LOWEST_PRECEDENCE;
            } else {
                return interceptor.getClass().getAnnotation(Order.class).value();
            }
        }

        private static class InterceptorWrapper {
            private final int order;
            private final Interceptor interceptor;

            InterceptorWrapper(int order, Interceptor interceptor) {
                this.order = order;
                this.interceptor = interceptor;
            }

            int getOrder() {
                return order;
            }

            Interceptor getInterceptor() {
                return interceptor;
            }
        }

    }




}
