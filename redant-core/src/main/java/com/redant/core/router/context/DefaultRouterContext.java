package com.redant.core.router.context;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.ClassScaner;
import cn.hutool.core.util.StrUtil;
import com.redant.core.bean.annotation.Bean;
import com.redant.core.bean.context.BeanContext;
import com.redant.core.bean.context.DefaultBeanContext;
import com.redant.core.common.constants.CommonConstants;
import com.redant.core.common.enums.RequestMethod;
import com.redant.core.controller.ControllerProxy;
import com.redant.core.controller.annotation.Controller;
import com.redant.core.controller.annotation.Mapping;
import com.redant.core.controller.context.ControllerContext;
import com.redant.core.controller.context.DefaultControllerContext;
import com.redant.core.init.InitFunc;
import com.redant.core.init.InitOrder;
import com.redant.core.render.RenderType;
import com.redant.core.router.RouteResult;
import com.redant.core.router.Router;
import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * 默认的RouterContext
 *
 * @author houyi.wh
 * @date 2017-10-20
 */
@InitOrder(2)
public class DefaultRouterContext implements RouterContext, InitFunc {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRouterContext.class);

    /**
     * 所有的路由信息
     */
    private static Router<RenderType> router;

    /**
     * 初始化完毕的标志
     */
    private static volatile boolean inited;

    /**
     * Bean上下文
     */
    private static BeanContext beanContext;

    /**
     * Controller上下文
     */
    private static ControllerContext controllerContext;

    private static final class DefaultRouterContextHolder {
        private static DefaultRouterContext context = new DefaultRouterContext();
    }

    private DefaultRouterContext() {

    }

    public static RouterContext getInstance() {
        return DefaultRouterContextHolder.context;
    }

    @Override
    public void init() {
        doInit();
    }

    /**
     * 获取路由结果
     */
    @Override
    public RouteResult<RenderType> getRouteResult(HttpMethod method, String uri) {
        if (inited()) {
            RouteResult<RenderType> routeResult = router.route(method, uri);
            LOGGER.debug("getRouteResult with method={}, uri={}, routeResult={}", method, uri, routeResult);
            return routeResult;
        }
        return null;
    }

    /**
     * Router是否加载完毕
     */
    private boolean inited() {
        while (!inited) {
            doInit();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return inited;
    }

    /**
     * 执行初始化工作
     */
    private void doInit() {
        // 初始化时需要同步
        synchronized (DefaultRouterContext.class) {
            if (!inited) {
                LOGGER.info("[DefaultRouterContext] doInit");
                beanContext = DefaultBeanContext.getInstance();
                controllerContext = DefaultControllerContext.getInstance();
                initRouter();
                // 加载完毕
                inited = true;
                LOGGER.info("[DefaultRouterContext] doInit success!");
            }
        }
    }

    private void initRouter() {
        try {
            LOGGER.info("[DefaultRouterContext] initRouter");
            // 获取所有RouterController
            Set<Class<?>> classSet = ClassScaner.scanPackageByAnnotation(CommonConstants.BEAN_SCAN_PACKAGE, Controller.class);
            router = new Router<>();
            if (CollectionUtil.isNotEmpty(classSet)) {
                for (Class<?> cls : classSet) {
                    Controller controller = cls.getAnnotation(Controller.class);
                    // 获取Controller中所有的方法
                    Method[] methods = cls.getMethods();
                    for (Method method : methods) {
                        Mapping mapping = method.getAnnotation(Mapping.class);
                        if (mapping != null) {
                            addRoute(controller, mapping);
                            // 添加控制器
                            addProxy(cls, method, controller, mapping);
                        }
                    }
                }
                router.notFound(RenderType.HTML);
                LOGGER.info("[DefaultRouterContext] initRouter success! routers are listed blow:" +
                                "\n*************************************" +
                                "\n{}" +
                                "*************************************\n",
                        router);
            } else {
                LOGGER.warn("[DefaultRouterContext] No Controller Scanned!");
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("[DefaultRouterContext] Init controller error,cause:", e);
        }
    }

    private void addRoute(Controller controller, Mapping mapping) {
        // Controller+Mapping 唯一确定一个控制器的方法
        String path = controller.path() + mapping.path();
        HttpMethod method = RequestMethod.getHttpMethod(mapping.requestMethod());
        // 添加路由
        router.addRoute(method, path, mapping.renderType());
    }

    private void addProxy(Class<?> cls, Method method, Controller controller, Mapping mapping) {
        try {
            // Controller+Mapping 唯一确定一个控制器的方法
            String path = controller.path() + mapping.path();
            ControllerProxy proxy = new ControllerProxy();
            proxy.setRenderType(mapping.renderType());
            proxy.setRequestMethod(mapping.requestMethod());
            Object object;
            // 如果该controller也使用了Bean注解，则从BeanContext中获取该controller的实现类
            Bean bean = cls.getAnnotation(Bean.class);
            if (bean != null) {
                // 如果该controller设置了bean的名字则以该名称从BeanHolder中获取bean，否则以
                String beanName = StrUtil.isNotBlank(bean.name()) ? bean.name() : cls.getName();
                object = beanContext.getBean(beanName);
            } else {
                object = cls.newInstance();
            }
            proxy.setController(object);
            proxy.setMethod(method);
            proxy.setMethodName(method.getName());

            controllerContext.addProxy(path, proxy);
            LOGGER.info("[DefaultRouterContext] addProxy path={} to proxy={}", path, proxy);
        } catch (Exception e) {
            LOGGER.error("[DefaultRouterContext] addProxy error,cause:", e.getMessage(), e);
        }
    }


}
