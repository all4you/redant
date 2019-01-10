package com.redant.core.router;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.ClassScaner;
import cn.hutool.core.util.StrUtil;
import com.redant.core.common.constants.CommonConstants;
import com.redant.core.common.enums.RequestMethod;
import com.redant.core.common.util.ThreadUtil;
import com.redant.core.bean.BeanContext;
import com.redant.core.bean.annotation.Bean;
import com.redant.core.invocation.ControllerProxy;
import com.redant.core.render.RenderType;
import com.redant.core.router.annotation.RouterController;
import com.redant.core.router.annotation.RouterMapping;
import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 路由Holder
 * @author gris.wang
 * @date 2017-10-20
 */
public class RouterHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouterHolder.class);

    /**
     * 保存所有的RouterController的代理类
     */
    private Map<String,ControllerProxy> proxyMap;

    /**
     * 所有的路由信息
     */
    private Router<RenderType> router;

    /**
     * Router加载完毕的标志
     */
    private volatile boolean routerLoaded;

    /**
     * RouterHolder的实例(单例)
     */
    private volatile static RouterHolder holder;


    private RouterHolder(){

    }


    /**
     * Router是否加载完毕
     */
    private boolean routerLoaded(){
        while(!routerLoaded){
            initRouters();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return routerLoaded;
    }


    //===========================================================

    /**
     * 创建实例
     */
    public static RouterHolder create(){
        if(holder==null) {
            synchronized (RouterHolder.class) {
                if (holder==null) {
                    holder = new RouterHolder();
                }
            }
        }
        return holder;
    }

    /**
     * 初始化Router
     */
    public void initRouters() {
        synchronized(RouterHolder.class){
            if(proxyMap==null || router==null) {
                LOGGER.info("[RouterHolder] Start init routers... currentThread={}", ThreadUtil.currentThreadName());
                try {
                    // 获取所有RouterController
                    Set<Class<?>> classSet = ClassScaner.scanPackageByAnnotation(CommonConstants.BEAN_SCAN_PACKAGE, RouterController.class);
                    proxyMap = new HashMap<>(classSet.size()+1);
                    router = new Router<>();

                    if (CollectionUtil.isNotEmpty(classSet)) {
                        for (Class<?> cls : classSet) {
                            RouterController routerController = cls.getAnnotation(RouterController.class);
                            // 获取Controller中所有的方法
                            Method[] methods = cls.getMethods();
                            for (Method method : methods) {
                                RouterMapping routerMapping = method.getAnnotation(RouterMapping.class);
                                if (routerMapping != null) {
                                    ControllerProxy proxy = new ControllerProxy();
                                    String path = routerController.path() + routerMapping.path();
                                    proxy.setTarget(routerMapping.renderType());
                                    proxy.setRequestMethod(routerMapping.requestMethod());
                                    Object controller;
                                    // 如果该controller也使用了Bean注解，则从BeanContext中获取该controller的实现类
                                    Bean bean = cls.getAnnotation(Bean.class);
                                    if (bean != null) {
                                        // 如果该controller设置了bean的名字则以该名称从BeanHolder中获取bean，否则以
                                        String beanName = StrUtil.isNotBlank(bean.name()) ? bean.name() : cls.getName();
                                        controller = BeanContext.getBean(beanName);
                                    } else {
                                        controller = cls.newInstance();
                                    }
                                    proxy.setController(controller);
                                    proxy.setMethod(method);
                                    proxy.setMethodName(method.getName());
                                    LOGGER.info("[RouterHolder] Mapping path={} to master={}", path, proxy);
                                    proxyMap.put(path, proxy);

                                    // 添加路由
                                    router.addRoute(RequestMethod.getHttpMethod(proxy.getRequestMethod()), path, proxy.getTarget());
                                }
                            }
                        }

                        router.notFound(RenderType.HTML);
                        LOGGER.info("[RouterHolder] Init routers success! routers are listed blow:\n*************************************\n{}*************************************\n", router);
                    } else {
                        LOGGER.warn("[RouterHolder] No RouterController Scanned!");
                    }
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    LOGGER.error("[RouterHolder] Init controller error,cause:", e);
                }
            }

            // 加载完毕
            routerLoaded = true;
        }
    }

    /**
     * 获取路由结果
     */
    public RouteResult<RenderType> getRouteResult(HttpMethod method, String uri){
        if(routerLoaded()){
            RouteResult<RenderType> routeResult = router.route(method, uri);
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("getRouteResult with method={},uri={},routeResult={}", method, uri, routeResult);
            }
            return routeResult;
        }
        return null;
    }

    /**
     * 根据routeResult获取ControllerProxy
     */
    public ControllerProxy getControllerProxy(RouteResult<?> routeResult){
        if(routerLoaded()) {
            ControllerProxy controllerProxy = proxyMap.get(routeResult.decodedPath());
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("\n=========================  getControllerProxy =========================\nrouteResult={}\ncontrollerProxy={}\n=========================  getControllerProxy =========================", routeResult, controllerProxy);
            }
            return controllerProxy;
        }
        return null;
    }


}
