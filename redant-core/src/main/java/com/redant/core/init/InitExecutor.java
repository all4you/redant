package com.redant.core.init;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.ClassScaner;
import com.redant.core.common.constants.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author houyi.wh
 * @date 2019-01-14
 */
public final class InitExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitExecutor.class);
    
    private static AtomicBoolean initialized = new AtomicBoolean(false);

    public static void init() {
        if (!initialized.compareAndSet(false, true)) {
            return;
        }
        try {
            Set<Class<?>> classSet = ClassScaner.scanPackageBySuper(CommonConstants.BEAN_SCAN_PACKAGE,InitFunc.class);
            if (CollectionUtil.isNotEmpty(classSet)) {
                List<OrderWrapper> initList = new ArrayList<>();
                for (Class<?> cls : classSet) {
                    // 如果cls是InitFunc的实现类
                    if(!cls.isInterface() && InitFunc.class.isAssignableFrom(cls)){
                        Constructor<?> constructor = cls.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        InitFunc initFunc = (InitFunc)constructor.newInstance();
                        LOGGER.info("[InitExecutor] Found init func: " + initFunc.getClass().getCanonicalName());
                        insertSorted(initList, initFunc);
                    }
                }
                for (OrderWrapper w : initList) {
                    w.func.init();
                    LOGGER.info("[InitExecutor] Initialized: {} with order {}", w.func.getClass().getCanonicalName(), w.order);
                }
            }
        } catch (Exception ex) {
            LOGGER.warn("[InitExecutor] Init failed", ex);
            ex.printStackTrace();
        } catch (Error error) {
            LOGGER.warn("[InitExecutor] Init failed with fatal error", error);
            error.printStackTrace();
            throw error;
        }
    }

    private static void insertSorted(List<OrderWrapper> list, InitFunc func) {
        int order = resolveOrder(func);
        int idx = 0;
        for (; idx < list.size(); idx++) {
            // 将func插入到order值比他大的第一个func前面
            if (list.get(idx).getOrder() > order) {
                break;
            }
        }
        list.add(idx, new OrderWrapper(order, func));
    }

    private static int resolveOrder(InitFunc func) {
        if (!func.getClass().isAnnotationPresent(InitOrder.class)) {
            return InitOrder.LOWEST_PRECEDENCE;
        } else {
            return func.getClass().getAnnotation(InitOrder.class).value();
        }
    }

    private InitExecutor() {}

    private static class OrderWrapper {
        private final int order;
        private final InitFunc func;

        OrderWrapper(int order, InitFunc func) {
            this.order = order;
            this.func = func;
        }

        int getOrder() {
            return order;
        }

        InitFunc getFunc() {
            return func;
        }
    }

}
