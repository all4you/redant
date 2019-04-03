package com.redant.core.interceptor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.ClassScaner;
import com.redant.core.anno.Order;
import com.redant.core.common.constants.CommonConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author houyi
 **/
public class InterceptorProvider {

    private static volatile boolean loaded = false;

    private static volatile InterceptorBuilder builder = null;

    public static List<Interceptor> getInterceptors(){
        // 优先获取用户自定义的 InterceptorBuilder 构造的 Interceptor
        if(!loaded){
            synchronized (InterceptorProvider.class) {
                if(!loaded) {
                    Set<Class<?>> builders = ClassScaner.scanPackageBySuper(CommonConstants.INTERCEPTOR_SCAN_PACKAGE, InterceptorBuilder.class);
                    if (CollectionUtil.isNotEmpty(builders)) {
                        try {
                            for (Class<?> cls : builders) {
                                builder = (InterceptorBuilder) cls.newInstance();
                                break;
                            }
                        } catch (IllegalAccessException | InstantiationException e) {
                            e.printStackTrace();
                        }
                    }
                    loaded = true;
                }
            }
        }
        if(builder!=null){
            return builder.build();
        }
        // 获取不到时，再扫描所有指定目录下的 Interceptor
        return InterceptorsHolder.interceptors;
    }

    static class InterceptorsHolder {

        static List<Interceptor> interceptors;

        static {
            interceptors = scanInterceptors();
        }

        private static List<Interceptor> scanInterceptors() {
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
